package com.example.dayorganizer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
@Database(entities = [CardInfo::class], version = 5, exportSchema = false)
public abstract class CardDatabase : RoomDatabase() {

    abstract fun CardDAO(): CardDAO

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null

        fun getDatabase(context: Context): CardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardDatabase::class.java,
                    "Card"
                )

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}