package com.hasib.appscheduler.domian.usecases

import com.hasib.appscheduler.domian.handleOperation
import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.model.Result
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import javax.inject.Inject

class DeleteAppScheduleUseCase @Inject constructor(
    private val appsSchedulerRepository: AppsSchedulerRepository,
    private val appsScheduleManager: AppsScheduleManager
) {

    suspend operator fun invoke(appInfo: AppInfo): Result<Unit> {
        return handleOperation {
            deleteScheduledApp(appInfo)
        }
    }

    private suspend fun deleteScheduledApp(appInfo: AppInfo) {
        appsSchedulerRepository.deleteSchedule(appInfo)
        appsScheduleManager.cancelSchedule(appInfo.packageName, appInfo.id)
    }
}