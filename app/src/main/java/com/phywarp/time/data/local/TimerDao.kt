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

    @Query("SELECT * FROM timer_records WHERE dateString = :date ORDER BY startTime DESC")
    fun getRecordsByDate(date: String): Flow<List<TimerRecord>>

    @Query("DELETE FROM timer_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("DELETE FROM timer_records WHERE dateString = :date")
    suspend fun deleteRecordsByDate(date: String)

    @Query("SELECT * FROM timer_records WHERE title LIKE '%' || :query || '%' ORDER BY startTime DESC")
    fun searchRecords(query: String): Flow<List<TimerRecord>>
}
