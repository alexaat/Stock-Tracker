package com.alexaat.stocktracker.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.stocktracker.database.StockItemDatabaseDao

class StockOverviewFragmentViewModel(database: StockItemDatabaseDao): ViewModel(){

    val stockItems = database.getAll()

    private val _navigateToNewItemFragment = MutableLiveData<Long>(-1)
    val navigateToNewItemFragment: LiveData<Long>
        get() = _navigateToNewItemFragment

    fun itemClicked(id:Long){
        _navigateToNewItemFragment.value = id
        _navigateToNewItemFragment.value = -1
    }


}