package com.alexaat.stocktracker

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.database.StockItemDatabase
import com.alexaat.stocktracker.database.StockItemDatabaseDao
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class StockItemDatabaseTest {

    private lateinit var stockItemDatabaseDao: StockItemDatabaseDao
    private lateinit var db: StockItemDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, StockItemDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        stockItemDatabaseDao = db.stockItemDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertStockItem() {
        val stockItem = StockItem(itemName = "nothing")
        stockItemDatabaseDao.insert(stockItem)
        val item = stockItemDatabaseDao.getLastItem()
        assertEquals(item.itemName, "nothing")
    }
}