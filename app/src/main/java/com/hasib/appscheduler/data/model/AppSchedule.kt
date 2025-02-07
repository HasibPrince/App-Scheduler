package com.hasib.appscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class AppSchedule(
    val appName: String,
    @PrimaryKey
    val packageName: String,
    val scheduleTime: Long = -1,
    val executed: Boolean = false
)
