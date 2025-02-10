package com.hasib.appscheduler.schedulers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hasib.appscheduler.R
import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.data.repositories.RecordsRepository
import com.hasib.appscheduler.ui.PACKAGE_NAME
import com.hasib.appscheduler.ui.REQUEST_CODE
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
    @Inject
    lateinit var recordsRepository: RecordsRepository

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra(PACKAGE_NAME) ?: return
        val requestCode = intent.getIntExtra(REQUEST_CODE, -1)
        Timber.d( "Received intent to launch app: $packageName requestCode: $requestCode")
        coroutineScope.launch {
            recordsRepository.saveScheduleRecord(packageName)
            packageInfoRepository.updateAppSchedule(packageName)
        }

        NotificationViewer.showNotification(
            context!!,
            requestCode,
            context.getString(R.string.app_name),
            "Launching app: $packageName",
            packageName
        )
    }
}