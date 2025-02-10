package com.hasib.appscheduler.data.repositories

import com.hasib.appscheduler.data.database.RecordsDao
import com.hasib.appscheduler.data.model.Records
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordsRepository @Inject constructor(
    private val recordsDao: RecordsDao,
) {

    suspend fun saveScheduleRecord(packageName: String) {
        val record = Records(
            packageName = packageName,
            executionTime = System.currentTimeMillis()
        )
        recordsDao.insertRecord(record)
    }

    suspend fun getRecordsList(packageName: String): List<Records> {
        return recordsDao.getRecordsByPackageName(packageName)
    }
}