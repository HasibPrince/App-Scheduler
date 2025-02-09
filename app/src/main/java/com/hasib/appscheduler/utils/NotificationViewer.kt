package com.hasib.appscheduler.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hasib.appscheduler.ui.MainActivity
import com.hasib.appscheduler.R
import timber.log.Timber

private const val NOTIFICATION_CHANNEL_ID = "SCHEDULER_NOTIFICATION_CHANNEL"

object NotificationViewer {
    fun showNotification(
        context: Context,
        notificationCode: Int,
        title: String,
        message: String,
        packageName: String
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showNotificationForOldVersion(context, notificationCode, title, message)
            return
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java);
        var notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, context.getString(R.string.title_incoming_call),
            NotificationManager.IMPORTANCE_HIGH
        )

        Timber.d("Notification channel created")

        notificationManager.createNotificationChannel(notificationChannel)
        val launchIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        launchIntent.putExtra("package", packageName)

        Timber.d("Launch intent created: $launchIntent")

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true)
        builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);
        builder.setContentText(message)

        notificationManager.notify(notificationCode, builder.build())
        Timber.d("Notification shown")
    }

    private fun showNotificationForOldVersion(
        context: Context,
        notificationCode: Int,
        title: String,
        message: String
    ) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(notificationCode, notification)
    }
}