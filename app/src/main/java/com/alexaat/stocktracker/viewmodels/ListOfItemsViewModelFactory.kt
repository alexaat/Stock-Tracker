package com.alexaat.stocktracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexaat.stocktracker.database.StockItemDatabaseDao

class ListOfItemsViewModelFactory(private val context: Context, private val database: StockItemDatabaseDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListOfItemsFragmentViewModel::class.java)) {
            return ListOfItemsFragmentViewModel(context = context, database = database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}