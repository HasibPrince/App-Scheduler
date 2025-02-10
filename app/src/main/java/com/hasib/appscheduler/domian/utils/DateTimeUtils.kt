package com.hasib.appscheduler.domian.utils

import com.hasib.appscheduler.BuildConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getNextTimestampInMillis(timeInMillis: Long): Long {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.isLenient = false

    val now = Calendar.getInstance()
    var targetTimeStamp = getTodayMidnightMillis() + timeInMillis
    Timber.d("Now: ${now.timeInMillis} Target: $targetTimeStamp")

    if (targetTimeStamp - now.timeInMillis < 60000) {
        if (BuildConfig.DEBUG) {
            targetTimeStamp = now.timeInMillis + (1000 * 60 * 3)
        } else {
            targetTimeStamp += 24 * 3600 * 1000
        }
        Timber.d("Timestamp moved to next day: Now: ${now.timeInMillis} Target: $targetTimeStamp")
    }

    return targetTimeStamp
}

private fun getTodayMidnightMillis(): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}