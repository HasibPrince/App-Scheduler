package com.hasib.startup

import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.model.Result
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import com.hasib.appscheduler.domian.usecases.UpdateNextAppScheduleUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class UpdateNextAppScheduleUseCaseTest {

    private lateinit var appsSchedulerRepository: AppsSchedulerRepository
    private lateinit var appsScheduleManager: AppsScheduleManager
    private lateinit var updateNextAppScheduleUseCase: UpdateNextAppScheduleUseCase

    @Before
    fun setup() {
        appsSchedulerRepository = mock(AppsSchedulerRepository::class.java)
        appsScheduleManager = mock(AppsScheduleManager::class.java)
        updateNextAppScheduleUseCase =
            UpdateNextAppScheduleUseCase(appsSchedulerRepository, appsScheduleManager)
    }

    @Test
    fun `should update next app schedule successfully`() = runTest {
        val packageName = "com.example.app"
        val appInfo = AppInfo(
            id = 1,
            appName = "example app",
            packageName = packageName,
            scheduledTime = System.currentTimeMillis()
        )

        `when`(appsSchedulerRepository.getScheduledAppByPackageName(packageName)).thenReturn(appInfo)

        val result = updateNextAppScheduleUseCase.invoke(packageName)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `should throw exception when schedule is not found`() = runTest {
        val packageName = "com.unknown.app"
        `when`(appsSchedulerRepository.getScheduledAppByPackageName(packageName)).thenReturn(null)

        val result = updateNextAppScheduleUseCase.invoke(packageName)
        assertTrue(result is Result.Error)
    }

    @Test
    fun `should throw exception when scheduled time is null`() = runTest {
        val packageName = "com.example.app"
        val appInfo =
            AppInfo(id = 1, "example app", packageName = packageName, scheduledTime = null)

        `when`(appsSchedulerRepository.getScheduledAppByPackageName(packageName)).thenReturn(appInfo)

        val result = updateNextAppScheduleUseCase.invoke(packageName)

        assertTrue(result is Result.Error)
    }
}