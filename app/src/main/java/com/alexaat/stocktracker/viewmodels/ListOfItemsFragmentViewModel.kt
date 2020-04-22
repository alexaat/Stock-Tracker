package com.alexaat.stocktracker.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.database.StockItemDatabaseDao
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.*



class ListOfItemsFragmentViewModel(private val context: Context, private val database: StockItemDatabaseDao): ViewModel() {

    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val stockItems = database.getAll()


    private val _showSnackBarAllClearedEvent = MutableLiveData<Boolean>(false)
    val showSnackBarAllClearedEvent:LiveData<Boolean>
        get() = _showSnackBarAllClearedEvent


    private val _showToastCannotFindItemEvent = MutableLiveData<Boolean>(false)
    val showToastCannotFindItemEvent:LiveData<Boolean>
        get() = _showToastCannotFindItemEvent

    private val _navigateToNewItemFragment = MutableLiveData<Long>(-1)
    val navigateToNewItemFragment:LiveData<Long>
        get() = _navigateToNewItemFragment


    private val _navigateToBarCodeScanner = MutableLiveData<Boolean>(false)
    val navigateToBarCodeScanner:LiveData<Boolean>
        get() = _navigateToBarCodeScanner


    fun onItemClicked(itemId: Long){
        _navigateToNewItemFragment.value = itemId
    }

    fun navigateToNewItemFragmentComplete(){
        _navigateToNewItemFragment.value = -1
    }

    fun addNewItemClick(){
        _navigateToNewItemFragment.value=0
    }

    fun lookUpBarCode(){
        _navigateToBarCodeScanner.value = true
    }

    fun navigateToBarCodeScannerComplete(){
        _navigateToBarCodeScanner.value = false
    }

    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?){
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        result?.let{
            val contents = it.contents
            contents?.let{
                uiScope.launch {
                    val stockItem: StockItem? = getByBarCode(contents)
                    if(stockItem==null){
                        _showToastCannotFindItemEvent.value = true
                    }else{
                        _navigateToNewItemFragment.value = stockItem.itemId
                    }
                }
            }
        }
    }

    fun showToastCannotFindItemEventComplete(){
        _showToastCannotFindItemEvent.value = false
    }

    fun clearAll(){
        uiScope.launch {

            val listOfItemUri = ArrayList<String>()
            stockItems.value?.let {
                for(item in it){
                    listOfItemUri.add(item.itemIconUri)
                }
            }
            clear()
            _showSnackBarAllClearedEvent.value = true

            for(uri in listOfItemUri){
                deleteImage(uri)
           }
        }
    }

    fun showSnackBarAllClearedEventComplete(){
        _showSnackBarAllClearedEvent.value = false
    }


    fun deleteItemAt(position:Int){
        val stockItem:StockItem? = stockItems.value?.get(position)
        stockItem?.let{
            uiScope.launch {
                delete(it.itemId)
                deleteImage(it.itemIconUri)
            }
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO ){
           database.clear()
        }
    }

    private suspend fun getByBarCode(barcode:String): StockItem {
        return withContext(Dispatchers.IO ){
            database.getByBarCode(barcode)
        }
    }

    private suspend fun delete(itemId:Long){
        withContext(Dispatchers.IO){
            database.delete(itemId)
        }
    }

    private suspend fun deleteImage(uri: String){
        withContext(Dispatchers.IO){
            if(uri=="") return@withContext
            context.contentResolver.delete(Uri.parse(uri),null,null)
        }
    }


}



