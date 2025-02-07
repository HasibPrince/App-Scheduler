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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
//
//        // Add necessary flags to keep the screen on
//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//        )

        enableEdgeToEdge()
        Timber.d("onCreate: MainActivity")
        setContent {
            StartupTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppSchedulerApp(it)
                }
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            openAlarmsAndRemindersSettings(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with your logic
                getInstalledApps()
            } else {
                // Permission denied, handle accordingly
                Timber.d("Permission denied")
            }
        }
    }

    private fun getInstalledApps() {
        val installedPackages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        for (packageInfo in installedPackages) {
            val packageName = packageInfo.packageName
            Timber.d("Package Name: $packageName")
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun openAlarmsAndRemindersSettings(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }


    @Composable
    private fun FirstScreen(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World: FirstScreen")
        }
    }

    @Composable
    private fun SecondScreen(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World: SecondScreen")
        }
    }
}