package com.hasib.appscheduler.data.repositories

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hasib.appscheduler.data.database.RecordsDao
import com.hasib.appscheduler.data.database.ScheduleDao
import com.hasib.appscheduler.data.model.AppSchedule
import com.hasib.appscheduler.data.model.Records
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.schedulers.ScheduleManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
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

    suspend fun insertSchedule(appInfo: AppInfo, scheduledTime: Long) {
        var appSchedule = scheduleDao.getScheduleByPackageName(packageName = appInfo.packageName)
        appSchedule = AppSchedule(
            appName = appInfo.appName,
            packageName = appInfo.packageName,
            scheduleTime = scheduledTime,
        )
        scheduleDao.insert(appSchedule)

        ScheduleManager.scheduleAppLaunch(
            context,
            appSchedule.id,
            appInfo.packageName,
            scheduledTime
        )
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
}