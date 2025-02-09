package com.hasib.appscheduler.data.repositories

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hasib.appscheduler.BuildConfig
import com.hasib.appscheduler.data.database.RecordsDao
import com.hasib.appscheduler.data.database.ScheduleDao
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.data.model.Records
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.schedulers.ScheduleManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class PackageInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleDao: ScheduleDao,
    private val recordsDao: RecordsDao
) {
    suspend fun getInstalledApps(): List<AppInfo> {
        val installedPackages: List<ApplicationInfo> =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val scheduledApps = scheduleDao.getAllSchedules()
        return installedPackages.map { packageInfo ->
            var appName = context.packageManager.getApplicationLabel(packageInfo).toString()
            val uniqueNumber = UUID.randomUUID().mostSignificantBits.hashCode()
            val packageName = packageInfo.packageName
            val scheduledTime = scheduledApps.find { it.packageName == packageName }?.scheduleTime

            recordsDao.getRecordsByPackageName(packageName).forEach {
                Timber.d("Record: $it")
            }

            AppInfo(uniqueNumber, appName, packageName, scheduledTime)
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
        }

        if (appSchedule.scheduleTime < 0 || appSchedule.id == -1 || appSchedule.appName.isEmpty()) {
            throw IllegalStateException("Scheduled time or app info is invalid : $appSchedule")
        }

        scheduleDao.insert(appSchedule)

        ScheduleManager.scheduleAppLaunch(
            context,
            appSchedule.id,
            appInfo.packageName,
            getNextTimestampInMillis(appSchedule.scheduleTime)
        )
    }

    suspend fun updateAppSchedule(packageName: String) {
        val appInfo = AppInfo(-1, "", packageName)
        setAppSchedule(appInfo)
    }


    suspend fun deleteSchedule(appInfo: AppInfo) {
        ScheduleManager.cancelSchedule(context, appInfo.id)
        scheduleDao.delete(appInfo.packageName)
        recordsDao.deleteRecordsByPackageName(appInfo.packageName)
    }

    suspend fun saveScheduleRecord(packageName: String) {
        val record = Records(packageName = packageName, executionTime = System.currentTimeMillis())
        recordsDao.insertRecord(record)
    }

    private fun getNextTimestampInMillis(timeInMillis: Long): Long {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.isLenient = false

        val now = Calendar.getInstance()
        var targetTimestamp = getTodayMidnightMillis() + timeInMillis
        Timber.d("Now: ${now.timeInMillis} Target: $targetTimestamp")

        if (targetTimestamp - now.timeInMillis < 60000) {
            if (BuildConfig.DEBUG) {
                targetTimestamp = now.timeInMillis + (1000 * 600)
            } else {
                targetTimestamp += 24 * 3600 * 1000
            }
            Timber.d("Timestamp moved to next day: Now: ${now.timeInMillis} Target: $targetTimestamp")
        }

        return targetTimestamp
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