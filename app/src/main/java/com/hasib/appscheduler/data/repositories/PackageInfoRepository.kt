package com.hasib.appscheduler.data.repositories

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hasib.appscheduler.data.database.ScheduleDao
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.data.model.toAppInfo
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PackageInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleDao: ScheduleDao,
) : AppsSchedulerRepository {
    override suspend fun getInstalledApps(): List<AppInfo> {
        val installedPackages: List<ApplicationInfo> =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val scheduledApps = scheduleDao.getAllSchedules()
        return installedPackages
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { packageInfo ->
                var appName = context.packageManager.getApplicationLabel(packageInfo).toString()
                val packageName = packageInfo.packageName
                val scheduledApp = scheduledApps.find { it.packageName == packageName }
                val scheduledTime = scheduledApp?.scheduleTime

                AppInfo(scheduledApp?.id ?: 0, appName, packageName, scheduledTime)
            }
    }

    override suspend fun setAppSchedule(appInfo: AppInfo): AppInfo {
        var appSchedule = scheduleDao.getScheduleByPackageName(packageName = appInfo.packageName)

        if (appSchedule == null && appInfo.scheduledTime == null) {
            throw IllegalStateException("Schedule time is not available: $appInfo")
        }

        if (appSchedule == null) {
            appSchedule = AppSchedule(
                appName = appInfo.appName,
                packageName = appInfo.packageName,
                scheduleTime = appInfo.scheduledTime ?: -1,
            )
        } else if (appInfo.scheduledTime != null) {
            appSchedule.scheduleTime = appInfo.scheduledTime!!
        }

        if (appSchedule.scheduleTime < 0 || appSchedule.id < 0 || appSchedule.appName.isEmpty()) {
            throw IllegalStateException("Scheduled time or app info is invalid : $appSchedule")
        }

        scheduleDao.insert(appSchedule)
        appSchedule = scheduleDao.getScheduleByPackageName(appInfo.packageName)
            ?: throw IllegalStateException("Schedule not found for package: ${appInfo.packageName}")

        return appSchedule.toAppInfo()
    }

    override suspend fun getScheduledAppByPackageName(packageName: String): AppInfo? {
        return scheduleDao.getScheduleByPackageName(packageName)?.toAppInfo()
    }


    override suspend fun deleteSchedule(appInfo: AppInfo) {
        val appSchedule = scheduleDao.getScheduleByPackageName(packageName = appInfo.packageName)
        if (appSchedule == null) {
            throw IllegalStateException("${appInfo.packageName} cannot be deleted, Not available in database")
        }

        scheduleDao.delete(appInfo.packageName)
    }
}