package com.hasib.appscheduler.di

import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import com.hasib.appscheduler.schedulers.AppScheduleManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun provideAppsScheduleManager(appScheduleManagerImpl: AppScheduleManagerImpl): AppsScheduleManager

    @Singleton
    @Binds
    fun provideAppsSchedulerRepository(appsSchedulerRepository: PackageInfoRepository): AppsSchedulerRepository
}