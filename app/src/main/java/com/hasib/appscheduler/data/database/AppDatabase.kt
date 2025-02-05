package com.hasib.appscheduler.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class CallerIdDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "callerId_database"

        @Volatile
        private var INSTANCE: CallerIdDatabase? = null

        fun getDatabase(context: Context): CallerIdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CallerIdDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}