package com.hasib.appscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hasib.appscheduler.ui.applist.AppListPage
import com.hasib.appscheduler.ui.applist.AppSchedulerViewModel
import com.hasib.appscheduler.ui.reocrdsList.RecordListPage
import kotlinx.serialization.Serializable

@Serializable
object AppList

@Serializable
data class RecordList (val packageName: String, val appName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerApp() {
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
