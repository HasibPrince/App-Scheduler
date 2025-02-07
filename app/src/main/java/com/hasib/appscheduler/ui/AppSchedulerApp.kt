package com.hasib.appscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerApp(viewModel: AppSchedulerViewModel = viewModel()) {
    val context = LocalContext.current
    val appList = viewModel.appsStateList
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(appList) {
                AppListItem(app = it, onScheduleUpdated = { app, date, time ->
                    viewModel.setSchedule(app, date, time)
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
    onScheduleUpdated: (AppInfoUiModel, Long, String) -> Unit,
    onDelete: (AppInfoUiModel) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var timePickerState = rememberTimePickerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
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
                    onClick = { showDialog = true }
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

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val selectedDate = datePickerState.selectedDateMillis
                    showTimePickerDialog = true
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerDialog) {
        DialWithDialogExample(onConfirm = {
            timePickerState = it
            val time = "${timePickerState.hour} : ${timePickerState.minute}"
            Timber.d("Time: $time")
            showTimePickerDialog = false
            onScheduleUpdated.invoke(app, datePickerState.selectedDateMillis ?: -1, time)
        }, onDismiss = {})
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
