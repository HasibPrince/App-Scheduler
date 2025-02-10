package com.hasib.appscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hasib.appscheduler.domian.model.AppInfo

@Entity(tableName = "schedules")
data class AppSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appName: String,
    val packageName: String,
    var scheduleTime: Long = -1,
)

fun AppSchedule.toAppInfo(): AppInfo {
    return AppInfo(id, appName, packageName, scheduleTime)
}