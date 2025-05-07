package com.example.aigiftrecommender.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aigiftrecommender.model.Holiday

@Database(entities = [Holiday::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_gift_recommender_database"
                )
                // Add migrations here if schema changes in the future
                .fallbackToDestructiveMigration() // Not ideal for production, but okay for this scope
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

