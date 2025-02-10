package com.hasib.appscheduler.domian.repositories

import com.hasib.appscheduler.domian.model.AppInfo

interface AppsSchedulerRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    suspend fun setAppSchedule(appInfo: AppInfo): AppInfo
    suspend fun getScheduledAppByPackageName(packageName: String): AppInfo?
    suspend fun deleteSchedule(appInfo: AppInfo)
}