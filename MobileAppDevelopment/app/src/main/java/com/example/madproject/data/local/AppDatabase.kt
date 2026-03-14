package com.example.madproject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room db
 * @database lists existing tables/entitie, tracks schema ver, and increments with changes.
 * companion object = singleton pattern <- one db connection for the app
 * @volotile ensues instance is read from main mem not cpu cache <- prevents race condition
 */

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_logger_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
