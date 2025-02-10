package com.hasib.appscheduler.ui

import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import com.hasib.appscheduler.ui.model.RecordsUIModel
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.Calendar

@Serializable
object AppList

@Serializable
data class RecordList (val packageName: String, val appName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerApp() {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppContainer(innerPadding)
    }

}

@Composable
fun AppContainer(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppList) {
        composable<AppList> {
            val appScheduleViewModel: AppSchedulerViewModel = hiltViewModel()
            AppListPage(innerPadding, appScheduleViewModel) { packageName, appName ->
                navController.navigate(RecordList(packageName, appName))
            }
        }
        composable<RecordList> {
            val recordList: RecordList = it.toRoute()
            RecordListPage(recordList.packageName, recordList.appName, innerPadding)
        }
    }
}

@Composable
fun AppListPage(
    innerPadding: PaddingValues,
    viewModel: AppSchedulerViewModel,
    onNavigateToRecordList: (String, String) -> Unit
) {
    AppList(innerPadding, viewModel, onNavigateToRecordList)
}

@Composable
fun RecordListPage(packageName: String, appName: String, innerPadding: PaddingValues, viewModel: RecordsViewModel = hiltViewModel()) {
    viewModel.fetchRecords(packageName)
    val records = viewModel.recordsStateList
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = appName, style = MaterialTheme.typography.headlineMedium)
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            items(records) {
                AppItem(it)
            }
        }
    }

}

@Composable
fun AppItem(record: RecordsUIModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Package Name: ${record.records.packageName}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Scheduled Time: ${record.formattedTimeStamp}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
private fun AppList(
    innerPadding: PaddingValues,
    viewModel: AppSchedulerViewModel = viewModel(),
    onNavigateToRecordList: (String, String) -> Unit
) {
    val appList = viewModel.appsStateList
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(appList, key = { it.appInfo.packageName }) {
            AppListItem(app = it, onScheduleUpdated = { app, date, time ->
                viewModel.setSchedule(app, date, time)
            }, onNavigateToRecordList) {
                viewModel.deleteSchedule(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListItem(
    app: AppInfoUiModel,
    onScheduleUpdated: (AppInfoUiModel, Long, String) -> Unit,
    onNavigateToRecordList: (String, String) -> Unit,
    onDelete: (AppInfoUiModel) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
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

    if (false) {
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
