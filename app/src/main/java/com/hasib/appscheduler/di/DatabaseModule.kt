package com.hasib.appscheduler.di

import android.app.Application
import android.content.Context
import com.hasib.appscheduler.data.database.AppScheduleDatabase
import com.hasib.appscheduler.data.database.RecordsDao
import com.hasib.appscheduler.data.database.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideScheduleDao(@ApplicationContext context: Context): ScheduleDao {
        return AppScheduleDatabase.getDatabase(context).scheduleDao()
    }

    @Provides
    @Singleton
    fun provideRecordsDao(@ApplicationContext context: Context): RecordsDao {
        return AppScheduleDatabase.getDatabase(context).recordsDao()
    }
}