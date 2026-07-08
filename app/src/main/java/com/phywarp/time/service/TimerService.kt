package com.phywarp.time.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.phywarp.time.MainActivity
import com.phywarp.time.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private val _timeInSeconds = MutableStateFlow(0L)
    val timeInSeconds = _timeInSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timerJob: Job? = null
    private var isCountUp = true
    private var taskTitle = ""

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun startTimer(title: String, initialSeconds: Long, countUp: Boolean) {
        taskTitle = title
        isCountUp = countUp
        _timeInSeconds.value = initialSeconds
        _isRunning.value = true

        startForeground(1, createNotification(formatTime(initialSeconds)))

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive && _isRunning.value) {
                delay(1000)
                if (isCountUp) {
                    _timeInSeconds.value++
                } else {
                    if (_timeInSeconds.value > 0) {
                        _timeInSeconds.value--
                    } else {
                        _isRunning.value = false
                        onTimerFinished()
                    }
                }
                updateNotification(formatTime(_timeInSeconds.value))
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        updateNotification("已暂停: ${formatTime(_timeInSeconds.value)}")
    }

    fun resumeTimer() {
        if (!_isRunning.value) {
            startTimer(taskTitle, _timeInSeconds.value, isCountUp)
        }
    }

    fun resetTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        _timeInSeconds.value = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun onTimerFinished() {
        // Handle vibration
        val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(1000, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }

        // Send broadcast or update notification to show finished state
        val intent = Intent("com.phywarp.time.TIMER_FINISHED")
        sendBroadcast(intent)
        updateNotification("计时已结束！")
    }

    private fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel",
                "计时器通知",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle(if (taskTitle.isEmpty()) "正在计时" else taskTitle)
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(contentText: String) {
        notificationManager.notify(1, createNotification(contentText))
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
