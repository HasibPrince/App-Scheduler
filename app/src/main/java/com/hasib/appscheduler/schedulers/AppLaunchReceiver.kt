package com.hasib.appscheduler.schedulers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.utils.NotificationViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppLaunchReceiver : BroadcastReceiver() {

    @Inject
    lateinit var packageInfoRepository: PackageInfoRepository

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra("packageName") ?: return
        val requestCode = intent.getIntExtra("requestCode", -1)
        Timber.d( "Received intent to launch app: $packageName requestCode: $requestCode")
        coroutineScope.launch {
            packageInfoRepository.saveScheduleRecord(packageName)
        }

        NotificationViewer.showNotification(
            context!!,
            requestCode,
            "App Scheduler",
            "Launching app: $packageName",
            packageName
        )
    }
}