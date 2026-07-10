package com.phywarp.time.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert
    suspend fun insertRecord(record: TimerRecord)

    @Query("SELECT * FROM timer_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<TimerRecord>>

    // 新：按标签筛选
    @Query("SELECT * FROM timer_records WHERE tag = :tag ORDER BY startTime DESC")
    fun getRecordsByTag(tag: String): Flow<List<TimerRecord>>

    // 新：按标签统计总时长
    @Query("SELECT SUM(duration) FROM timer_records WHERE tag = :tag")
    suspend fun getTotalDurationByTag(tag: String): Long?

    // 新：获取所有用过的标签（去重）
    @Query("SELECT DISTINCT tag FROM timer_records WHERE tag IS NOT NULL")
    fun getAllUsedTags(): Flow<List<String>>

    @Query("SELECT * FROM timer_records WHERE dateString = :date ORDER BY startTime DESC")
    fun getRecordsByDate(date: String): Flow<List<TimerRecord>>

    @Query("DELETE FROM timer_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("DELETE FROM timer_records WHERE dateString = :date")
    suspend fun deleteRecordsByDate(date: String)

    @Query("SELECT * FROM timer_records WHERE title LIKE '%' || :query || '%' ORDER BY startTime DESC")
    fun searchRecords(query: String): Flow<List<TimerRecord>>
}
