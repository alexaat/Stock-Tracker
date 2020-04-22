package com.alexaat.stocktracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexaat.stocktracker.database.StockItemDatabaseDao

class StockOverviewFragmentViewModelFactory(private val database: StockItemDatabaseDao):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockOverviewFragmentViewModel::class.java)) {
            return StockOverviewFragmentViewModel(database = database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}