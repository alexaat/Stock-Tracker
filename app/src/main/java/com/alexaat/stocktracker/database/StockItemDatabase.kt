package com.alexaat.stocktracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StockItem::class], version = 1, exportSchema = false)
abstract class StockItemDatabase : RoomDatabase() {

    abstract val stockItemDatabaseDao: StockItemDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: StockItemDatabase? = null

        fun getInstance(context: Context): StockItemDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StockItemDatabase::class.java,
                        "work_day_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}