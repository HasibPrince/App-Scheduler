package com.hasib.appscheduler.ui.applist

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import timber.log.Timber
import java.util.Calendar

@Composable
fun AppListPage(
    innerPadding: PaddingValues,
    viewModel: AppSchedulerViewModel,
    onNavigateToRecordList: (String, String) -> Unit
) {
    AppList(innerPadding, viewModel, onNavigateToRecordList)
}

@Composable
private fun AppList(
    innerPadding: PaddingValues,
    viewModel: AppSchedulerViewModel = viewModel(),
    onNavigateToRecordList: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val appList = viewModel.appsStateList
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchApps(query = searchQuery)
                            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(

            contentPadding = PaddingValues(16.dp)
        ) {
            items(appList, key = { it.appInfo.packageName }) {
                AppListItem(app = it, onNavigateToRecordList, onScheduleUpdated = { app, time ->
                    viewModel.setSchedule(app, time)
                }) {
                    viewModel.deleteSchedule(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListItem(
    app: AppInfoUiModel,
    onNavigateToRecordList: (String, String) -> Unit,
    onScheduleUpdated: (AppInfoUiModel, String) -> Unit,
    onDelete: (AppInfoUiModel) -> Unit
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var timePickerState = rememberTimePickerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onNavigateToRecordList.invoke(app.appInfo.packageName, app.appInfo.appName)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                bitmap = getAppIconBitmap(app.appInfo.packageName, LocalContext.current.applicationContext)!!.asImageBitmap(),
                contentDescription = app.appInfo.appName,
                modifier = Modifier
                    .size(85.dp)
                    .padding(horizontal = 10.dp)
                    .clip(CircleShape),
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "App Name: ${app.appInfo.appName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Package Name: ${app.appInfo.packageName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showTimePickerDialog = true }
                    ) {
                        Text(text = app.formattedScheduledTime ?: "Set Schedule")
                    }

                    if (app.formattedScheduledTime != null) {
                        IconButton(
                            onClick = { onDelete(app) },
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.Bottom)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTimePickerDialog) {
        DialWithDialogExample(onConfirm = {
            timePickerState = it
            val time = "${timePickerState.hour} : ${timePickerState.minute}"
            Timber.d("Time: $time")
            showTimePickerDialog = false
            onScheduleUpdated.invoke(app, time)
        }, onDismiss = {
            showTimePickerDialog = false
        })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialWithDialogExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}

fun getAppIconBitmap(packageName: String, context: Context): Bitmap? {
    val pm = context.packageManager
    return try {
        val drawable = pm.getApplicationIcon(packageName)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
