package com.hasib.appscheduler.data.database

import androidx.room.*
import com.hasib.appscheduler.data.model.AppSchedule

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY scheduleTime ASC")
    suspend fun getAllSchedules(): List<AppSchedule>

    @Query("SELECT * FROM schedules WHERE packageName = :packageName")
    suspend fun getScheduleByPackageName(packageName: String): AppSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: AppSchedule)

    @Query("DELETE FROM schedules WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("UPDATE schedules SET scheduleTime = :newTime WHERE packageName = :packageName")
    suspend fun updateTime(packageName: String, newTime: Long)
}