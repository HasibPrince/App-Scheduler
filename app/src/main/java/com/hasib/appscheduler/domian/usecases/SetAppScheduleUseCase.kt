package com.hasib.appscheduler.domian.usecases

import com.hasib.appscheduler.domian.handleOperation
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.model.Result
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import com.hasib.appscheduler.domian.utils.getNextTimestampInMillis
import javax.inject.Inject

class SetAppScheduleUseCase @Inject constructor(
    private val appsSchedulerRepository: AppsSchedulerRepository,
    private val appsScheduleManager: AppsScheduleManager
) {
    suspend operator fun invoke(appInfo: AppInfo): Result<AppInfo> {
        return handleOperation {
            setAppSchedule(appInfo)
        }
    }

    private suspend fun setAppSchedule(appInfo: AppInfo): AppInfo {
        val updatedAppInfo = appsSchedulerRepository.setAppSchedule(appInfo)

        if (updatedAppInfo.scheduledTime == null) {
            throw IllegalStateException("Scheduled time is not available: $updatedAppInfo")
        }

        appsScheduleManager.scheduleAppLaunch(
            updatedAppInfo.id,
            updatedAppInfo.packageName,
            getNextTimestampInMillis(updatedAppInfo.scheduledTime!!)
        )

        return updatedAppInfo
    }
}