package com.alexaat.stocktracker.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.stocktracker.R
import com.alexaat.stocktracker.adapters.MyAdapter
import com.alexaat.stocktracker.adapters.StockItemClickListener
import com.alexaat.stocktracker.database.StockItemDatabase
import com.alexaat.stocktracker.databinding.FragmentListOfItemsBinding
import com.alexaat.stocktracker.hideKeyboard
import com.alexaat.stocktracker.viewmodels.ListOfItemsFragmentViewModel
import com.alexaat.stocktracker.viewmodels.ListOfItemsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

val PERMISSION_CODE_SCANNER = 999

class ListOfItemsFragment : Fragment() {

    private lateinit var  viewModel: ListOfItemsFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentListOfItemsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_of_items, container, false)

        this.hideKeyboard()

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        //view Model
        val activity = this.requireActivity()
        val database = StockItemDatabase.getInstance(activity).stockItemDatabaseDao
        val viewModelFactory = ListOfItemsViewModelFactory(activity.applicationContext, database)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ListOfItemsFragmentViewModel::class.java)
        binding.viewModel = viewModel

        // recycler view
        val  viewAdapter = MyAdapter(StockItemClickListener {
            viewModel.onItemClicked(it)
        })
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(binding.listOfItemsRecyclerView)
        binding.listOfItemsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }


        //observers
        viewModel.stockItems.observe(viewLifecycleOwner, Observer {
            it?.let{
                 viewAdapter.submitList(it)
            }
        })
        viewModel.showSnackBarAllClearedEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                view?.let{ view ->
                    Snackbar.make(view,getString(R.string.all_deleted), Snackbar.LENGTH_LONG).show()
                }
                viewModel.showSnackBarAllClearedEventComplete()
            }
        })
        viewModel.navigateToNewItemFragment.observe(viewLifecycleOwner, Observer{
            if(it>=0){
                val action = ListOfItemsFragmentDirections.actionListOfItemsFragmentToNewItemFragment(it)
                this.findNavController().navigate(action)
                viewModel.navigateToNewItemFragmentComplete()
            }
        })

        viewModel.navigateToBarCodeScanner.observe(viewLifecycleOwner, Observer {
            if(it){

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
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

                viewModel.navigateToBarCodeScannerComplete()

            }
        })

        viewModel.showToastCannotFindItemEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                Toast.makeText(context,getString(R.string.cannot_find_item),Toast.LENGTH_SHORT).show()
                viewModel.showToastCannotFindItemEventComplete()
            }
        })

        viewModel.showItemDeletedSnackBar.observe(viewLifecycleOwner, Observer {
            if(it){
                this.view?.let{view->
                    val bar = Snackbar.make(view,getString(R.string.item_deleted),Snackbar.LENGTH_SHORT)
                    bar.show()
                }
            }
        })

        return binding.root
    }


    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE_SCANNER -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted from pop up menu
                    val scanIntent = IntentIntegrator.forSupportFragment(this)
                    scanIntent.initiateScan()
                } else {
                    // permission from pop up denied
                    Toast.makeText(
                        activity,
                        getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.activityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
             when (item.itemId) {
                 R.id.action_delete_all -> {

                     val builder = AlertDialog.Builder(context)
                     builder.setTitle(getString(R.string.delete_all_items))
                     builder.setMessage(getString(R.string.delete_all_items_message))

                     builder.setPositiveButton(getString(R.string.delete_all)){ dialog, _ ->
                         viewModel.clearAll()
                         dialog.dismiss()
                     }
                     builder.setNeutralButton(getString(R.string.cancel)){dialog,_ ->
                         dialog.dismiss()
                     }
                     val dialog: AlertDialog = builder.create()
                     dialog.show()
                 }
             }
        return  super.onOptionsItemSelected(item)
        }

    private val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
           return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.deleteItemAt(viewHolder.adapterPosition)
        }

    }



    }


