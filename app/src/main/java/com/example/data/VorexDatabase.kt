package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HandoffRecord::class], version = 1, exportSchema = false)
abstract class VorexDatabase : RoomDatabase() {
    abstract fun handoffDao(): HandoffDao

    companion object {
        @Volatile
        private var INSTANCE: VorexDatabase? = null

        fun getDatabase(context: Context): VorexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VorexDatabase::class.java,
                    "vorex_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
