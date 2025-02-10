package com.hasib.appscheduler.ui.reocrdsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasib.appscheduler.ui.model.RecordsUIModel

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