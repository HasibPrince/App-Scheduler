package com.hasib.appscheduler.data.model

data class AppInfo(
    val appName: String,
    val packageName: String,
    var scheduledTime: String? = null // Null if not scheduled
)
