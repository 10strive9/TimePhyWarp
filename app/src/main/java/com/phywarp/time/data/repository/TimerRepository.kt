package com.phywarp.time.data.repository

import com.phywarp.time.data.local.TimerDao
import com.phywarp.time.data.local.TimerRecord
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {
    val allRecords: Flow<List<TimerRecord>> = timerDao.getAllRecords()

    suspend fun insert(record: TimerRecord) {
        timerDao.insertRecord(record)
    }

    suspend fun delete(id: Long) {
        timerDao.deleteRecord(id)
    }

    suspend fun deleteByDate(date: String) {
        timerDao.deleteRecordsByDate(date)
    }

    fun search(query: String): Flow<List<TimerRecord>> {
        return timerDao.searchRecords(query)
    }
}
