package com.hasib.appscheduler.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.ui.PACKAGE_NAME
import com.hasib.appscheduler.ui.REQUEST_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppScheduleManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AppsScheduleManager {
    override fun scheduleAppLaunch(
        requestCode: Int,
        packageName: String,
        triggerTime: Long
    ) {
        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra(PACKAGE_NAME, packageName)
            putExtra(REQUEST_CODE, requestCode)
        }

        Timber.d("Scheduling app launch with requestcode: $requestCode for $packageName at $triggerTime")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    override fun cancelSchedule(packageName: String, requestCode: Int) {
        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra(PACKAGE_NAME, packageName)
            putExtra(REQUEST_CODE, requestCode)
        }

        Timber.d("Cancelling app launch with requestcode: $requestCode for $packageName")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}