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

private const val NOTIFICATION_CHANNEL_ID = "CALLER_ID_NOTIFICATION_CHANNEL"
private const val NOTIFICATION_ID = 3535

object NotificationViewer {
    fun showNotification(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showNotificationForOldVersion(context, title, message)
            return
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java);
        var notificationChannel = NotificationChannel(
           NOTIFICATION_CHANNEL_ID, context.getString(R.string.title_incoming_call),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(notificationChannel)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true)
        builder.setCategory(NotificationCompat.CATEGORY_CALL)
        builder.setAutoCancel(false)

        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);
        builder.setContentText(message)

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private fun showNotificationForOldVersion(context: Context, title: String, message: String) {
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

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}