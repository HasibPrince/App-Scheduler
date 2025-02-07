package com.hasib.appscheduler.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
            val scheduleTimeInMillis = convertTimeToMillis(date, time)
            packageInfoRepository.insertSchedule(appInfoUiModel.appInfo, scheduleTimeInMillis)
            Timber.d("Current Time: ${System.currentTimeMillis()} Scheduled Time: $scheduleTimeInMillis")
            getPackageInfo()
        }
    }

    fun deleteSchedule(appInfoUiModel: AppInfoUiModel) {
        viewModelScope.launch {
            packageInfoRepository.deleteSchedule(appInfoUiModel.appInfo)
            getPackageInfo()
        }
    }

    private fun formatTimestampToDateTime(timestamp: Long?): String? {
        if (timestamp == null) return null
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun convertTimeToMillis(dateInMilli: Long,time: String): Long {
        val parts = time.split(":")
        if (parts.size != 2) return 0L

        val hours = parts[0].trim().toIntOrNull() ?: return 0L
        val minutes = parts[1].trim().toIntOrNull() ?: return 0L

        val timeInMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
        }.timeInMillis
        Timber.d("Selected time: ${formatTimestampToDateTime(timeInMillis)}")
        return timeInMillis
    }
}