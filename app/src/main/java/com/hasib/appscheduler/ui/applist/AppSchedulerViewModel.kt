package com.hasib.appscheduler.ui.applist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.StateObject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.appscheduler.data.repositories.PackageInfoRepository
import com.hasib.appscheduler.domian.handleOperation
import com.hasib.appscheduler.domian.model.doOnError
import com.hasib.appscheduler.domian.model.doOnSuccess
import com.hasib.appscheduler.domian.usecases.DeleteAppScheduleUseCase
import com.hasib.appscheduler.domian.usecases.SetAppScheduleUseCase
import com.hasib.appscheduler.ui.model.AppInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.mutableListOf

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    private val packageInfoRepository: PackageInfoRepository,
    private val setAppScheduleUseCase: SetAppScheduleUseCase,
    private val deleteAppScheduleUseCase: DeleteAppScheduleUseCase
) : ViewModel() {
    val appsList = mutableListOf<AppInfoUiModel>()
    private val _appsStateList = mutableStateListOf<AppInfoUiModel>()
    val appsStateList: List<AppInfoUiModel> get() = _appsStateList

    val searchQuery = mutableStateOf("")
    val errorMessage: MutableState<String> = mutableStateOf("")

    init {
        getPackageInfo()
    }

    fun getPackageInfo() {
        viewModelScope.launch {
            _appsStateList.clear()
            val appInfoUiModels = handleOperation {
                getApplicationInfo()
            }

            appInfoUiModels.doOnError {
                errorMessage.value = it.message ?: "Something went wrong"
            }

            appInfoUiModels.doOnSuccess {
                appsList.clear()
                appsList.addAll(it)
                _appsStateList.addAll(
                    it
                )
            }
        }
    }

    private suspend fun getApplicationInfo(): List<AppInfoUiModel> =
        packageInfoRepository.getInstalledApps().map {
            AppInfoUiModel(it, formatTimestampToDateTime(it.scheduledTime))
        }

    fun setSchedule(appInfoUiModel: AppInfoUiModel, time: String) {
        viewModelScope.launch {
            Timber.d("Selected Time for ${appInfoUiModel.appInfo.packageName}: $time")
            val scheduleTimeInMillis = convertTimeToMillis(time)
            appInfoUiModel.appInfo.scheduledTime = scheduleTimeInMillis

            val result = setAppScheduleUseCase.invoke(appInfoUiModel.appInfo)
            result.doOnError {
                errorMessage.value = it.message ?: "Something went wrong"
            }

            val index = _appsStateList.indexOf(appInfoUiModel)
            _appsStateList[index] = appInfoUiModel.copy(
                formattedScheduledTime = formatTimestampToDateTime(scheduleTimeInMillis)
            )
            Timber.d("Current Time: ${System.currentTimeMillis()} Scheduled Time: $scheduleTimeInMillis")
        }
    }

    fun deleteSchedule(appInfoUiModel: AppInfoUiModel) {
        viewModelScope.launch {
            val result = deleteAppScheduleUseCase.invoke(appInfoUiModel.appInfo)
            result.doOnError {
                errorMessage.value = it.message ?: "Something went wrong"
            }
            val index = _appsStateList.indexOf(appInfoUiModel)
            _appsStateList[index] = appInfoUiModel.copy(formattedScheduledTime = null)
        }
    }

    fun searchApps(listState: LazyListState) {
        _appsStateList.clear()
        _appsStateList.addAll(
            appsList.filter { it.appInfo.appName.contains(searchQuery.value, ignoreCase = true) }
        )

        viewModelScope.launch {
            listState.scrollToItem(0)
        }
    }

    private fun formatTimestampToDateTime(timestamp: Long?): String? {
        if (timestamp == null) return null

        val timeInMin = timestamp / (1000L * 60)
        val hours = timeInMin / 60
        val minutes = timeInMin % 60
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