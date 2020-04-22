package com.alexaat.stocktracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stock_items_table")
data class StockItem(

    @PrimaryKey(autoGenerate = true)
    var itemId:Long = 0,

    @ColumnInfo(name = "item_stock_code")
    var itemStockCode:String = "",

    @ColumnInfo(name = "item_name")
    var itemName:String = "",

    @ColumnInfo(name = "item_description")
    var itemDescription:String = "",

    @ColumnInfo(name = "item_bar_code")
    var itemBarCode:String = "",

    @ColumnInfo(name = "number_of_items")
    var numberOfItems:Double = 0.0,

    @ColumnInfo(name = "price_per_item")
    var pricePerItem:Double = 0.0,

    @ColumnInfo(name = "item_icon_url")
    var itemIconUri: String = "")



