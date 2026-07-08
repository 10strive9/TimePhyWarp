package com.phywarp.time.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_records")
data class TimerRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "COUNT_UP" or "COUNT_DOWN"
    val startTime: Long, // Timestamp
    val endTime: Long,   // Timestamp
    val duration: Long,  // Seconds
    val dateString: String // "yyyy-MM-dd" for grouping
)
