package com.hasib.appscheduler.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hasib.appscheduler.schedulers.ScheduleManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerApp(padding: PaddingValues) {
    val context = LocalContext.current.applicationContext
    Column(modifier = Modifier.padding(padding).padding(16.dp)) {
        Text("Welcome to the App Scheduler!", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = {
            ScheduleManager.scheduleAppLaunch(context, 123, "com.example.consoleplayground", System.currentTimeMillis() + 30000)
//            val launchIntent = context.packageManager?.getLaunchIntentForPackage("com.example.consoleplayground")?.apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//            context.startActivity(launchIntent)
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Create a new schedule")
        }
    }
}
