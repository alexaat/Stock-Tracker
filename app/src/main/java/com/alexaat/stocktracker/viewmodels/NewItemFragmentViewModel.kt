package com.alexaat.stocktracker.viewmodels

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.database.StockItemDatabaseDao
import com.alexaat.stocktracker.getRealPathFromUri
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class NewItemFragmentViewModel(private val context:Context, private val database: StockItemDatabaseDao): ViewModel(){

    var stockItem: StockItem? = null
    private var imageUri: Uri? = null

    var oldImageUri = ""
    var newImageUri = ""

    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToListOfItemsFragmentEvent = MutableLiveData<Boolean>(false)
    val navigateToListOfItemsFragmentEvent: LiveData<Boolean>
        get() = _navigateToListOfItemsFragmentEvent

    private val _showSnackBarItemSavedEvent = MutableLiveData<Boolean>(false)
    val showSnackBarItemSavedEvent:LiveData<Boolean>
        get() = _showSnackBarItemSavedEvent

    private val _showSnackBarItemUpdatedEvent = MutableLiveData<Boolean>(false)
    val showSnackBarItemUpdatedEvent:LiveData<Boolean>
        get() = _showSnackBarItemUpdatedEvent

    private val _stockItemIsReady = MutableLiveData<StockItem>(null)
    val stockItemIsReady:LiveData<StockItem>
        get() = _stockItemIsReady

    private val _enableSaveUpdateButton = MutableLiveData<Boolean>(false)
    val enableSaveUpdateButton:LiveData<Boolean>
        get() = _enableSaveUpdateButton

    private val _changeSaveButtonText = MutableLiveData<Boolean>(false)
    val changeSaveButtonText:LiveData<Boolean>
         get() = _changeSaveButtonText

    private val _openScanner = MutableLiveData<Boolean>(false)
    val openScanner:LiveData<Boolean>
        get() = _openScanner

    private val _launchCropImage = MutableLiveData<Intent>(null)
    val launchCropImage:LiveData<Intent>
        get() = _launchCropImage

    private val _launchCamera = MutableLiveData<Intent>(null)
    val launchCamera:LiveData<Intent>
        get() = _launchCamera

    private val _checkCameraPermission = MutableLiveData<Boolean>(false)
    val checkCameraPermission:LiveData<Boolean>
        get() = _checkCameraPermission


    private val _showStockItemConflictDialog = MutableLiveData<StockItem>(null)
    val showStockItemConflictDialog:LiveData<StockItem>
        get() = _showStockItemConflictDialog

    fun getItem(itemId:Long){

         uiScope.launch {
            if(stockItem==null && itemId==0L){
                stockItem = StockItem()
                _changeSaveButtonText.value = false
            }
            if(stockItem!=null && itemId==0L){
                _stockItemIsReady.value = stockItem
                _stockItemIsReady.value = null
                _changeSaveButtonText.value = false
            }
            if(stockItem==null && itemId>0){
                stockItem = get(itemId)
                _stockItemIsReady.value = stockItem
                _stockItemIsReady.value = null
                _changeSaveButtonText.value = true
            }
            if(stockItem!=null && itemId>0){
                _stockItemIsReady.value = stockItem
                _stockItemIsReady.value = null
                _changeSaveButtonText.value = true
            }
             _enableSaveUpdateButton.value = true
        }
    }

    fun saveToDb(stockItem:StockItem) {
         uiScope.launch {
             if(stockItem.itemId>0){
                 update(stockItem)
                 _showSnackBarItemUpdatedEvent.value = true
                 _showSnackBarItemUpdatedEvent.value = false

             }else{
                 insert(stockItem)
                 _showSnackBarItemSavedEvent.value = true
                 _showSnackBarItemSavedEvent.value = false
             }
             deleteImage(oldImageUri)
             oldImageUri = ""
             newImageUri = ""
             _navigateToListOfItemsFragmentEvent.value = true
             _navigateToListOfItemsFragmentEvent.value = false
         }
     }

    fun onSaveButtonClicked(){

        stockItem?.let{
            uiScope.launch {

                val newCode = it.itemStockCode
                val itemFromDb: StockItem? = getByStockCode(newCode)

                if(itemFromDb==null){
                    saveToDb(it)
                    return@launch
                }
                if(it.itemId > 0){
                    saveToDb(it)
                    return@launch
                }
                _showStockItemConflictDialog.value = stockItem
                _showStockItemConflictDialog.value = null



            }

        }

    }

    fun launchBarCodeScanner(){
        _openScanner.value = true
        _openScanner.value = false

    }

    fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        _launchCamera.value = cameraIntent
    }

    fun launchCamera() {
        imageUri?.let{
            uiScope.launch {
                deleteImage(it.toString())
            }
        }
        _checkCameraPermission.value = true
        _checkCameraPermission.value = false
    }

    private fun launchCropImage(uri:Uri?){
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri, "image/*")

            cropIntent.putExtra("crop","true")
            cropIntent.putExtra("outputX",128)
            cropIntent.putExtra("outputY",128)
            cropIntent.putExtra("aspectX",1)
            cropIntent.putExtra("aspectY",1)
            cropIntent.putExtra("scaleUpIfNeeded", true)
            cropIntent.putExtra("return-data",true)

        _launchCropImage.value = cropIntent

    }

    fun launchCropImageComplete(){
        _launchCropImage.value = null
    }

    fun launchCameraComplete(){
        _launchCamera.value = null
    }

    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            launchCropImage(imageUri)
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //crop successful
            if (data != null) {
                val bundle = data.extras
                bundle?.let {
                    val bitmap:Bitmap? = bundle.getParcelable("data")
                        // save image
                        stockItem?.let {item->
                            imageUri?.let {uri->
                                bitmap?.let {bm->
                                    uiScope.launch {

                                        overWriteFile(uri, bm)
                                        oldImageUri = item.itemIconUri
                                        newImageUri =  uri.toString()
                                        item.itemIconUri = newImageUri
                                        _stockItemIsReady.value=stockItem
                                        imageUri = null

                                    }
                                }
                            }
                        }
                }
            }
        }
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        result?.let{
            val contents = it.contents
            contents?.let{
                stockItem?.let{ item->
                    item.itemBarCode = contents
                    _stockItemIsReady.value=stockItem
                }
            }
        }
    }

    fun onBackButtonPressed(){
        if(imageUri==null){
            if(newImageUri!=""){
                stockItem?.let{
                    uiScope.launch {
                        deleteImage(newImageUri)
                        newImageUri=""
                        it.itemIconUri = oldImageUri
                        oldImageUri=""
                        _stockItemIsReady.value=stockItem
                    }
                }
            }


        }else{
            val uri = imageUri.toString()
            if(uri!=""){
                uiScope.launch {
                    deleteImage(uri)
                }
            }
        }
    }
    /////////////////////suspend//////////////////////////
    private suspend fun deleteImage(uri: String){
    withContext(Dispatchers.IO){
        if(uri=="") return@withContext
        context.contentResolver.delete(Uri.parse(uri),null,null)
    }
    }
    private suspend fun overWriteFile(uri: Uri, bitmap: Bitmap) {
    //overwrite large original image with icon
    withContext(Dispatchers.IO){
        val file = File(getRealPathFromUri(context, uri))
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
    private suspend fun update(stockItem:StockItem){
        withContext(Dispatchers.IO) {
            database.update(stockItem)
        }
    }
    private suspend fun insert(stockItem: StockItem){
        withContext(Dispatchers.IO){
            database.insert(stockItem)
        }
    }
    private suspend fun get(itemId:Long):StockItem{
        return withContext(Dispatchers.IO){
             database.get(itemId)
        }
    }
    private suspend fun getByStockCode(stockCode:String):StockItem{
        return withContext(Dispatchers.IO) {
            database.getByStockCode(stockCode)
        }
    }
}