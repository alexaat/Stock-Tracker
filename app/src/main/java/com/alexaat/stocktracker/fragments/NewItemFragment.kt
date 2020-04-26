package com.alexaat.stocktracker.fragments


import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.stocktracker.R
import com.alexaat.stocktracker.database.StockItemDatabase
import com.alexaat.stocktracker.databinding.FragmentNewItemBinding
import com.alexaat.stocktracker.viewmodels.NewItemFragmentViewModel
import com.alexaat.stocktracker.viewmodels.NewItemFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator


class NewItemFragment : Fragment() {

    val IMAGE_CAPTURE_CODE = 1001
    val PERMISSION_CODE = 1000
    val PERMISSION_CODE_SCANNER = 999

    lateinit var binding: FragmentNewItemBinding
    lateinit var viewModel: NewItemFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)

        val itemId = NewItemFragmentArgs.fromBundle(requireArguments()).itemId

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this

        val activity = this.requireActivity()
        val db = StockItemDatabase.getInstance(activity).stockItemDatabaseDao
        val viewModelFactory = NewItemFragmentViewModelFactory(activity.applicationContext, db)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewItemFragmentViewModel::class.java)
        viewModel.getItem(itemId)
        binding.viewModel = viewModel

        viewModel.navigateToListOfItemsFragmentEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val action = NewItemFragmentDirections.actionNewItemFragmentToListOfItemsFragment()
                findNavController().navigate(action)
            }
        })

        viewModel.showSnackBarItemSavedEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                view?.let { view ->
                    Snackbar.make(
                        view,
                        resources.getString(R.string.item_saved),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })

        viewModel.showSnackBarItemUpdatedEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                view?.let { view ->
                    Snackbar.make(
                        view,
                        resources.getString(R.string.item_updated),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })

        viewModel.stockItemIsReady.observe(viewLifecycleOwner, Observer {
            it?.let { stockItem ->
                binding.apply {
                        this.stockItem = stockItem
                        this.executePendingBindings()
                }
            }
        })

        viewModel.enableSaveUpdateButton.observe(viewLifecycleOwner, Observer {
            enableSaveButton(binding.saveItemButton, it)
        })

        viewModel.changeSaveButtonText.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.saveItemButton.text = resources.getString(R.string.update_item)

            }else{
                binding.saveItemButton.text = resources.getString(R.string.save_item)
            }
        })

        viewModel.openScanner.observe(viewLifecycleOwner, Observer {
           if(it){

               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                   if(activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                       // permission was not enabled
                       val permission  = arrayOf(Manifest.permission.CAMERA)
                       // show pop up to request permission
                       this.requestPermissions(permission, PERMISSION_CODE_SCANNER)

                   }else{
                       // permission already granted
                       val scanIntent = IntentIntegrator.forSupportFragment(this)
                       scanIntent.initiateScan()
                   }
               }else{
                   //  os is < marshmallow
                   val scanIntent = IntentIntegrator.forSupportFragment(this)
                   scanIntent.initiateScan()
               }
           }
        })

        viewModel.launchCamera.observe(viewLifecycleOwner, Observer {
            it?.let { intent->
                val pm = activity.packageManager
                pm?.let{
                    if(intent.resolveActivity(pm)!=null){
                        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
                        viewModel.launchCameraComplete()
                    }
                }
            }
        })

        viewModel.launchCropImage.observe(viewLifecycleOwner, Observer {
            it?.let{intent->
                try{
                    startActivityForResult(intent,1)
                    viewModel.launchCropImageComplete()
                }catch(e:ActivityNotFoundException){
                   Toast.makeText(context,getString(R.string.corp_fail_message),Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.checkCameraPermission.observe(viewLifecycleOwner, Observer {
            if(it){

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    val cameraPermissionDenied = activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    val storagePermissionDenied = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

                    if(cameraPermissionDenied || storagePermissionDenied){
                        // permission was not enabled

                        var permission  = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if(!cameraPermissionDenied){
                            permission  = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                        if(!storagePermissionDenied){
                            permission  = arrayOf((Manifest.permission.CAMERA))
                        }
                        // show pop up to request permission
                        this.requestPermissions(permission, PERMISSION_CODE)

                    }else{
                        // permission already granted
                        viewModel.openCamera()
                    }
                }else{
                    //  os is < marshmallow
                    viewModel.openCamera()
                }



            }
        })

        viewModel.showStockItemConflictDialog.observe(viewLifecycleOwner, Observer {

                it?.let{
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.stock_code_conflict))
                val stockCode = it.itemStockCode
                builder.setMessage(getString(R.string.stock_code_conflict_message,stockCode))

                builder.setPositiveButton(getString(R.string.yes)){ dialog, _ ->
                    viewModel.saveToDb(it)
                    dialog.dismiss()
                }
                builder.setNeutralButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        })


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
             viewModel.onBackButtonPressed()
             findNavController().navigateUp()
            }
        }
       requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         when(requestCode){
           PERMISSION_CODE ->{
               if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   // permission granted from pop up menu
                   viewModel.openCamera()

               }else{
                   // permission from pop up denied
                   Toast.makeText(activity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
               }
           }
           PERMISSION_CODE_SCANNER ->{
               if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   // permission granted from pop up menu
                   val scanIntent = IntentIntegrator.forSupportFragment(this)
                   scanIntent.initiateScan()

               }else{
                   // permission from pop up denied
                   Toast.makeText(activity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
               }
           }
       }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           viewModel.activityResult(requestCode, resultCode, data)
    }

    private fun enableSaveButton(saveButton: Button, enabled:Boolean){
       if(enabled){
           saveButton.alpha = 1.0f
       }else{
           saveButton.alpha = 0.5f
       }
       saveButton.isEnabled = enabled
    }

    //up button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                viewModel.onBackButtonPressed()
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }



}


