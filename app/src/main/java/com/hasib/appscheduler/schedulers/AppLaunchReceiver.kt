package com.hasib.appscheduler.schedulers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.hasib.appscheduler.utils.NotificationViewer
import timber.log.Timber

class AppLaunchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra("packageName") ?: return
        Timber.d( "Received intent to launch app: $packageName")
//        val launchIntent = context?.packageManager?.getLaunchIntentForPackage(packageName)
//        if (launchIntent != null) {
//            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//            val wakeLock = powerManager.newWakeLock(
//                PowerManager.PARTIAL_WAKE_LOCK,
//                "MyApp::MyWakeLockTag"
//            )
//            wakeLock.acquire(1 * 60 * 1000L /*10 minutes*/)
//
//            context.startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//            Timber.d( "Launching app: $packageName")
//        }

        NotificationViewer.showNotification(
            context!!,
            "App Scheduler",
            "Launching app: $packageName",
            packageName
        )
    }
}