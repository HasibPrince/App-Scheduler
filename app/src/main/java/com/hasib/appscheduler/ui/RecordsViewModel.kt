package com.hasib.appscheduler.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.appscheduler.data.repositories.RecordsRepository
import com.hasib.appscheduler.ui.model.RecordsUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(private val recordsRepository: RecordsRepository) :
    ViewModel() {

    val recordsStateList = mutableStateListOf<RecordsUIModel>()

    fun fetchRecords(packageName: String) {
        viewModelScope.launch {
            recordsStateList.clear()
            recordsStateList.addAll(recordsRepository.getRecordsList(packageName).map {
                RecordsUIModel(it, formatTimestampToDateTime(it.executionTime))
            })
        }
    }

    private fun formatTimestampToDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}