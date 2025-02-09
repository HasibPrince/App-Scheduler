package com.hasib.appscheduler.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.data.model.Records

@Database(entities = [AppSchedule::class, Records::class], version = 1, exportSchema = false)
abstract class AppScheduleDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun recordsDao(): RecordsDao

    companion object {
        const val DATABASE_NAME = "appSchedule_database"

        @Volatile
        private var INSTANCE: AppScheduleDatabase? = null

        fun getDatabase(context: Context): AppScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppScheduleDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}