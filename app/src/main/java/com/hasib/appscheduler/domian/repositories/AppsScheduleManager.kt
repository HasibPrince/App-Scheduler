package com.hasib.appscheduler.domian.repositories

interface AppsScheduleManager {
    fun scheduleAppLaunch(
        requestCode: Int,
        packageName: String,
        triggerTime: Long
    )

    fun cancelSchedule( packageName: String, requestCode: Int)
}