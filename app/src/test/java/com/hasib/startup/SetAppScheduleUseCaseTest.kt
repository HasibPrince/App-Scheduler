package com.hasib.startup

import com.hasib.appscheduler.domian.model.AppInfo
import com.hasib.appscheduler.domian.model.Result
import com.hasib.appscheduler.domian.repositories.AppsScheduleManager
import com.hasib.appscheduler.domian.repositories.AppsSchedulerRepository
import com.hasib.appscheduler.domian.usecases.SetAppScheduleUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SetAppScheduleUseCaseTest {

    private lateinit var appsSchedulerRepository: AppsSchedulerRepository
    private lateinit var appsScheduleManager: AppsScheduleManager
    private lateinit var setAppScheduleUseCase: SetAppScheduleUseCase

    @Before
    fun setup() {
        appsSchedulerRepository = mock(AppsSchedulerRepository::class.java)
        appsScheduleManager = mock(AppsScheduleManager::class.java)
        setAppScheduleUseCase = SetAppScheduleUseCase(appsSchedulerRepository, appsScheduleManager)
    }

    @Test
    fun `should schedule app successfully when scheduledTime is valid`() = runTest {
        val appInfo = AppInfo(id = 1, appName ="example app", packageName = "com.example.app", scheduledTime = System.currentTimeMillis())
        `when`(appsSchedulerRepository.setAppSchedule(appInfo)).thenReturn(appInfo)

        val result = setAppScheduleUseCase.invoke(appInfo)

        assertTrue(result is Result.Success)
        assertEquals(appInfo, (result as Result.Success<AppInfo>).data)
    }

    @Test
    fun `should throw exception when scheduledTime is null`() = runTest {
        val appInfo = AppInfo(id = 1, appName = "example app", packageName = "com.example.app", scheduledTime = null)
        `when`(appsSchedulerRepository.setAppSchedule(appInfo)).thenReturn(appInfo)

        val result = setAppScheduleUseCase.invoke(appInfo)

        assertTrue(result is Result.Error)
        assertEquals("Scheduled time is not available: $appInfo", (result as Result.Error).e.message)
    }
}