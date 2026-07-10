package com.phywarp.time.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.phywarp.time.data.local.AppDatabase
import com.phywarp.time.data.local.TimerRecord
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.service.TimerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val timerDao = database.timerDao()
    private var timerService: TimerService? = null
    private var isBound = false

    // 状态
    private val _timerValue = MutableStateFlow(0L)
    val timerValue: StateFlow<Long> = _timerValue.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _taskTitle = MutableStateFlow("")
    val taskTitle: StateFlow<String> = _taskTitle.asStateFlow()

    private val _isCountUp = MutableStateFlow(true)
    val isCountUp: StateFlow<Boolean> = _isCountUp.asStateFlow()

    private var startTime: Long = 0
    private var countdownTarget: Long = 0

    // 服务绑定
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

    init {
        val intent = Intent(application, TimerService::class.java)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // 设置标题
    fun setTaskTitle(title: String) {
        _taskTitle.value = title
    }

    // 设置模式
    fun setCountUp(isCountUp: Boolean) {
        _isCountUp.value = isCountUp
    }

    // 设置倒计时目标
    fun setCountdownTime(seconds: Long) {
        countdownTarget = seconds
    }

    // 切换计时（统一处理开始/暂停/继续）
    fun toggleTimer() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            if (_timerValue.value > 0 && startTime > 0) {
                // 继续
                resumeTimer()
            } else {
                // 全新开始
                startTime = System.currentTimeMillis()
                startTimer()
            }
        }
    }

    private fun startTimer() {
        val intent = Intent(getApplication(), TimerService::class.java)
        getApplication<Application>().startForegroundService(intent)

        timerService?.startTimer(
            _taskTitle.value,
            if (_isCountUp.value) 0 else countdownTarget,
            _isCountUp.value
        )
    }

    fun pauseTimer() {
        timerService?.pauseTimer()
    }

    fun resumeTimer() {
        timerService?.resumeTimer()
    }

    fun resetTimer() {
        if (_timerValue.value > 0) {
            saveRecord()
        }
        timerService?.resetTimer()
        _timerValue.value = 0
        startTime = 0
        countdownTarget = 0
    }

    // 保存记录（现在支持 tag，暂留空）
    private fun saveRecord() {
        val endTime = System.currentTimeMillis()
        val duration = if (_isCountUp.value) {
            (endTime - startTime) / 1000
        } else {
            countdownTarget - _timerValue.value
        }

        val record = TimerRecord(
            title = _taskTitle.value.ifEmpty { "未命名任务" },
            tag = null, // 预留标签
            type = if (_isCountUp.value) "正计时" else "倒计时",
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startTime))
        )

        viewModelScope.launch {
            timerDao.insertRecord(record)
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
