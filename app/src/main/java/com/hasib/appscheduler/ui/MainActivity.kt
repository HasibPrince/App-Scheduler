package com.hasib.appscheduler.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hasib.appscheduler.theme.StartupTheme
import com.hasib.appscheduler.utils.NotificationViewer
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        enableEdgeToEdge()
        setContent {
            StartupTheme {
                AppSchedulerApp()
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            openAlarmsAndRemindersSettings(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            managePermissions()
        }
    }


    override fun onResume() {
        super.onResume()
        intent.getStringExtra(PACKAGE_NAME)?.let {
            Timber.d("Opening app for Package name: $it")
            val launchIntent = packageManager.getLaunchIntentForPackage(it)
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }

        intent.getIntExtra(REQUEST_CODE, -1).let {
            Timber.d("Request code: $it")
            if (it == -1) {
                return
            }
            NotificationViewer.cancelNotification(this.applicationContext, it)
        }

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun managePermissions() {
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
        )

        if (isPermissionsGranted()) {

        } else {
            ActivityCompat.requestPermissions(
                this, permissions, 1001
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isPermissionsGranted(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED


    @RequiresApi(Build.VERSION_CODES.S)
    fun openAlarmsAndRemindersSettings(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
}