package com.hasib.appscheduler.domian.model

data class AppInfo(
    val id: Int,
    val appName: String,
    val packageName: String,
    var scheduledTime: Long? = null
)
