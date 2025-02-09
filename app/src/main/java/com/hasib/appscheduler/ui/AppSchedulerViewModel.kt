package com.hasib.appscheduler.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    private val packageInfoRepository: PackageInfoRepository
) : ViewModel() {

    private val _appsStateList = mutableStateListOf<AppInfoUiModel>()
    val appsStateList: SnapshotStateList<AppInfoUiModel> get() = _appsStateList

    init {
        getPackageInfo()
    }

    fun getPackageInfo() {
        viewModelScope.launch {
            _appsStateList.clear()
            _appsStateList.addAll(
                packageInfoRepository.getInstalledApps().map {
                    AppInfoUiModel(it, formatTimestampToDateTime(it.scheduledTime))
                }
            )
        }
    }

    fun setSchedule(appInfoUiModel: AppInfoUiModel, date: Long, time: String) {
        viewModelScope.launch {
            Timber.d("Date: $date Time: $time")
            val scheduleTimeInMillis = convertTimeToMillis(time)
            appInfoUiModel.appInfo.scheduledTime = scheduleTimeInMillis
            packageInfoRepository.setAppSchedule(appInfoUiModel.appInfo)

            val index = _appsStateList.indexOf(appInfoUiModel)
            _appsStateList[index] = appInfoUiModel.copy(formattedScheduledTime = formatTimestampToDateTime(scheduleTimeInMillis))
            Timber.d("Current Time: ${System.currentTimeMillis()} Scheduled Time: $scheduleTimeInMillis")
        }
    }

    fun deleteSchedule(appInfoUiModel: AppInfoUiModel) {
        viewModelScope.launch {
            packageInfoRepository.deleteSchedule(appInfoUiModel.appInfo)
            val index = _appsStateList.indexOf(appInfoUiModel)
            _appsStateList[index] = appInfoUiModel.copy(formattedScheduledTime = null)
        }
    }

    private fun formatTimestampToDateTime(timestamp: Long?): String? {
        if (timestamp == null) return null

        val timeInMin = timestamp / (1000L * 60)
        val hours = timeInMin / 60
        val minutes = timeInMin % 60
        Timber.d("Current TimeZone: ${TimeZone.getDefault()}")
        Timber.d("Parsed Time: $hours : $minutes")

        return "$hours : $minutes"

    }

    private fun convertTimeToMillis(time: String): Long {
        val parts = time.split(":")
        if (parts.size != 2) return 0L

        val hours = parts[0].trim().toIntOrNull() ?: return 0L
        val minutes = parts[1].trim().toIntOrNull() ?: return 0L

        return (hours * 3600 * 1000L) + (minutes * 60 * 1000L)
    }
}