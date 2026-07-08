package com.phywarp.time.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.phywarp.time.data.local.AppDatabase
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.data.local.TimerState
import com.phywarp.time.data.local.TimerRecord
import com.phywarp.time.data.repository.TimerRepository
import com.phywarp.time.service.TimerService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TimerRepository
    private var timerService: TimerService? = null
    private var isBound = false

    private val _timerValue = MutableStateFlow(0L)
    val timerValue = _timerValue.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _taskTitle = MutableStateFlow("")
    val taskTitle = _taskTitle.asStateFlow()

    private val _isCountUp = MutableStateFlow(true)
    val isCountUp = _isCountUp.asStateFlow()

    val allRecords: Flow<List<TimerRecord>>
    
    private val _searchQuery = MutableStateFlow("")
    val records: Flow<List<TimerRecord>>

    private var startTime: Long = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true
            
            viewModelScope.launch {
                timerService?.timeInSeconds?.collect { _timerValue.value = it }
            }
            viewModelScope.launch {
                timerService?.isRunning?.collect { _isRunning.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            timerService = null
        }
    }

    private val userPreferences: UserPreferences

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TimerRepository(database.timerDao())
        userPreferences = UserPreferences(application)
        
        allRecords = repository.allRecords
        records = _searchQuery.flatMapLatest { query ->
            if (query.isEmpty()) repository.allRecords else repository.search(query)
        }

        // Restore state
        viewModelScope.launch {
            userPreferences.timerState.first().let { state ->
                if (state.isRunning) {
                    val now = System.currentTimeMillis()
                    val elapsed = (now - state.startTime) / 1000
                    val newSeconds = if (state.isCountUp) state.targetSeconds + elapsed else state.targetSeconds - elapsed
                    
                    _taskTitle.value = state.title
                    _isCountUp.value = state.isCountUp
                    startTime = state.startTime
                    
                    if (state.isCountUp || newSeconds > 0) {
                        startTimer(if (state.isCountUp) 0 else newSeconds)
                    }
                }
            }
        }

        val intent = Intent(application, TimerService::class.java)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun saveState() {
        viewModelScope.launch {
            userPreferences.saveTimerState(
                TimerState(
                    title = _taskTitle.value,
                    startTime = startTime,
                    targetSeconds = _timerValue.value,
                    isRunning = _isRunning.value,
                    isCountUp = _isCountUp.value
                )
            )
        }
    }

    fun setTaskTitle(title: String) {
        _taskTitle.value = title
    }

    fun setMode(countUp: Boolean) {
        _isCountUp.value = countUp
    }

    fun startTimer(initialSeconds: Long = 0) {
        startTime = if (startTime == 0L) System.currentTimeMillis() else startTime
        val intent = Intent(getApplication(), TimerService::class.java)
        getApplication<Application>().startForegroundService(intent)
        timerService?.startTimer(_taskTitle.value, if (_isCountUp.value) 0 else initialSeconds, _isCountUp.value)
        saveState()
    }

    fun pauseTimer() {
        timerService?.pauseTimer()
        saveState()
    }

    fun resumeTimer() {
        timerService?.resumeTimer()
        saveState()
    }

    fun resetTimer() {
        if (_timerValue.value > 0) {
            saveRecord()
        }
        timerService?.resetTimer()
        _timerValue.value = 0
        startTime = 0
        saveState()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun saveRecord() {
        val endTime = System.currentTimeMillis()
        val duration = _timerValue.value
        val record = TimerRecord(
            title = _taskTitle.value.ifEmpty { "未命名任务" },
            type = if (_isCountUp.value) "正计时" else "倒计时",
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startTime))
        )
        viewModelScope.launch {
            repository.insert(record)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun deleteRecordsByDate(date: String) {
        viewModelScope.launch {
            repository.deleteByDate(date)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
