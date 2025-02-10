package com.hasib.appscheduler.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Timber.d("onCreate: MainActivity")
        setContent {
            StartupTheme {
                AppSchedulerApp()
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            openAlarmsAndRemindersSettings(this)
        }


//        if (!isBatteryOptimizationIgnored()) {
//            requestDisableBatteryOptimization(this)
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            managePermissions()
        }
    }

    fun requestDisableBatteryOptimization(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        intent.getStringExtra("package")?.let {
            Timber.d("Package name: $it")
            val launchIntent = packageManager.getLaunchIntentForPackage(it)
            if (launchIntent != null) {
                startActivity(launchIntent)
                Timber.d("Launch Intent")
            } else {
                Timber.d("Launch intent is null")
            }
        }
        intent.getIntExtra("requestCode", -1).let {
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