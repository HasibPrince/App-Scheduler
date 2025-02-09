package com.hasib.appscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Records(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val executionTime: Long,
)
