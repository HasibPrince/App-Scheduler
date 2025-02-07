package com.hasib.appscheduler.di

import android.app.Application
import android.content.Context
import com.hasib.appscheduler.data.database.AppScheduleDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): ScheduleDao {
        return AppScheduleDatabase.getDatabase(context).scheduleDao()
    }
}