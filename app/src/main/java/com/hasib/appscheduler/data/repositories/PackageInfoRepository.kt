package com.hasib.appscheduler.data.repositories

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hasib.appscheduler.data.database.ScheduleDao
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.schedulers.ScheduleManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PackageInfoRepository @Inject constructor(@ApplicationContext private val context: Context, private val scheduleDao: ScheduleDao) {
    suspend fun getInstalledApps(): List<AppInfo> {
        val installedPackages: List<ApplicationInfo> =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val scheduledApps = scheduleDao.getAllSchedules()
        return installedPackages.map { packageInfo ->
            var appName = context.packageManager.getApplicationLabel(packageInfo).toString()

            val packageName = packageInfo.packageName
            val scheduledTime = scheduledApps.find { it.packageName == packageName }?.scheduleTime
            AppInfo(appName, packageName, scheduledTime)
        }
    }

    suspend fun insertSchedule(appInfo: AppInfo, scheduledTime: Long ) {
        val appSchedule = scheduleDao.getScheduleByPackageName(packageName = appInfo.packageName)
        if (appSchedule != null) {
            scheduleDao.updateTime(appSchedule.packageName, scheduledTime)
        } else {
            val appSchedule = AppSchedule(appInfo.appName, appInfo.packageName, scheduledTime)
            scheduleDao.insert(appSchedule)
        }
        ScheduleManager.scheduleAppLaunch(context, 11, appInfo.packageName, scheduledTime)
    }

    suspend fun deleteSchedule(appInfo: AppInfo) {
        scheduleDao.delete(appInfo.packageName)
    }

}