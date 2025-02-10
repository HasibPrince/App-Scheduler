package com.hasib.appscheduler.data.repositories

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hasib.appscheduler.BuildConfig
import com.hasib.appscheduler.data.database.ScheduleDao
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.schedulers.ScheduleManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class PackageInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleDao: ScheduleDao,
) {
    suspend fun getInstalledApps(): List<AppInfo> {
        val installedPackages: List<ApplicationInfo> =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val scheduledApps = scheduleDao.getAllSchedules()
        return installedPackages.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { packageInfo ->
                var appName = context.packageManager.getApplicationLabel(packageInfo).toString()
                val packageName = packageInfo.packageName
                val scheduledApp = scheduledApps.find { it.packageName == packageName }
                val scheduledTime = scheduledApp?.scheduleTime

                AppInfo(scheduledApp?.id ?: 0, appName, packageName, scheduledTime)
            }
    }

    suspend fun setAppSchedule(appInfo: AppInfo) {
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

        ScheduleManager.scheduleAppLaunch(
            context,
            appSchedule.id,
            appSchedule.packageName,
            getNextTimestampInMillis(appSchedule.scheduleTime)
        )
    }

    suspend fun updateAppSchedule(packageName: String) {
        val appSchedule = scheduleDao.getScheduleByPackageName(packageName)
        if (appSchedule == null) {
            throw IllegalStateException("Schedule not found for package: $packageName")
        }

        ScheduleManager.scheduleAppLaunch(
            context,
            appSchedule.id,
            appSchedule.packageName,
            getNextTimestampInMillis(appSchedule.scheduleTime)
        )
    }


    suspend fun deleteSchedule(appInfo: AppInfo) {
        val appSchedule = scheduleDao.getScheduleByPackageName(packageName = appInfo.packageName)
        if (appSchedule == null) {
            throw IllegalStateException("${appInfo.packageName} cannot be deleted, Not available in database")
        }
        ScheduleManager.cancelSchedule(context, appSchedule.packageName, appSchedule.id)
        scheduleDao.delete(appInfo.packageName)
    }

    private fun getNextTimestampInMillis(timeInMillis: Long): Long {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.isLenient = false

        val now = Calendar.getInstance()
        var targetTimeStamp = getTodayMidnightMillis() + timeInMillis
        Timber.d("Now: ${now.timeInMillis} Target: $targetTimeStamp")

        if (targetTimeStamp - now.timeInMillis < 60000) {
            if (BuildConfig.DEBUG) {
                targetTimeStamp = now.timeInMillis + (1000 * 60 * 3)
            } else {
                targetTimeStamp += 24 * 3600 * 1000
            }
            Timber.d("Timestamp moved to next day: Now: ${now.timeInMillis} Target: $targetTimeStamp")
        }

        return targetTimeStamp
    }

    private fun getTodayMidnightMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}