package com.hasib.appscheduler.domian.usecases

import com.hasib.appscheduler.domian.handleOperation
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.model.Result
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import com.hasib.appscheduler.domian.utils.getNextTimestampInMillis
import javax.inject.Inject

class UpdateNextAppScheduleUseCase @Inject constructor(
    private val appsSchedulerRepository: AppsSchedulerRepository,
    private val appsScheduleManager: AppsScheduleManager
) {

    suspend operator fun invoke(packageName: String): Result<AppInfo> {
        return handleOperation {
            updateNextAppSchedule(packageName)
        }
    }

    private suspend fun updateNextAppSchedule(
        packageName: String,
    ): AppInfo {
        val appInfo = appsSchedulerRepository.getScheduledAppByPackageName(packageName)
        if (appInfo == null) {
            throw IllegalStateException("Schedule not found for package: $packageName")
        }

        if (appInfo.scheduledTime == null) {
            throw IllegalStateException("Scheduled time is not available: $appInfo")
        }

        appsScheduleManager.scheduleAppLaunch(
            appInfo.id,
            appInfo.packageName,
            getNextTimestampInMillis(appInfo.scheduledTime!!)
        )

        return appInfo
    }

}