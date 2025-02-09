package com.hasib.appscheduler.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hasib.appscheduler.data.model.Records

@Dao
interface RecordsDao {
    @Insert
    suspend fun insertRecord(record: Records)

    @Query("SELECT * FROM records WHERE packageName = :packageName")
    suspend fun getRecordsByPackageName(packageName: String): List<Records>

    @Query("DELETE FROM records WHERE packageName = :packageName")
    suspend fun deleteRecordsByPackageName(packageName: String)
}