package com.alexaat.stocktracker.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StockItemDatabaseDao{

    @Insert
    fun insert(stockItem:StockItem)
    @Update
    fun update(stockItem:StockItem)

    @Query("DELETE FROM stock_items_table")
    fun clear()

    @Query("DELETE FROM stock_items_table WHERE itemId = :itemId")
    fun delete(itemId:Long)

    @Query("SELECT * FROM stock_items_table ORDER BY itemId DESC")
    fun getAll():LiveData<List<StockItem>>

    @Query("SELECT * FROM stock_items_table WHERE itemId = :key ORDER BY itemId DESC LIMIT 1")
    fun  get(key:Long):StockItem

    //used for database test
    @Query("SELECT * FROM stock_items_table ORDER BY itemId DESC LIMIT 1")
    fun getLastItem():StockItem

    @Query("SELECT * FROM stock_items_table WHERE item_bar_code = :barcode ORDER BY itemId DESC LIMIT 1")
    fun getByBarCode(barcode:String):StockItem

    @Query("SELECT * FROM stock_items_table WHERE item_stock_code = :stockCode ORDER BY itemId DESC LIMIT 1")
    fun getByStockCode(stockCode:String):StockItem




}