package com.phywarp.time.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phywarp.time.ui.components.FlipTimerDisplay
import com.phywarp.time.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val timeSeconds by viewModel.timerValue.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isCountUp by viewModel.isCountUp.collectAsState()
    val taskTitle by viewModel.taskTitle.collectAsState()

    // 倒计时输入
    var countdownHours by remember { mutableStateOf("00") }
    var countdownMinutes by remember { mutableStateOf("25") }
    var countdownSeconds by remember { mutableStateOf("00") }

    // 任务标题
    var titleText by remember { mutableStateOf(TextFieldValue("")) }

    // 计算显示的时:分:秒
    val hours = timeSeconds / 3600
    val minutes = (timeSeconds % 3600) / 60
    val seconds = timeSeconds % 60

    // 刷新时间显示
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 顶部小小时钟
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(currentTime)),
            color = Color(0xFF666666),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 模式切换
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterChip(
                selected = isCountUp,
                onClick = { viewModel.setCountUp(true) },
                label = { Text("正计时") }
            )
            FilterChip(
                selected = !isCountUp,
                onClick = { viewModel.setCountUp(false) },
                label = { Text("倒计时") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 翻页计时显示
        FlipTimerDisplay(
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )

        // 倒计时输入框（仅在倒计时模式且未运行时显示）
        if (!isCountUp && !isRunning) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = countdownHours,
                    onValueChange = { if (it.length <= 2) countdownHours = it },
                    modifier = Modifier.width(80.dp),
                    label = { Text("时") },
                    singleLine = true
                )
                Text(text = ":", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 8.dp))
                OutlinedTextField(
                    value = countdownMinutes,
                    onValueChange = { if (it.length <= 2) countdownMinutes = it },
                    modifier = Modifier.width(80.dp),
                    label = { Text("分") },
                    singleLine = true
                )
                Text(text = ":", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 8.dp))
                OutlinedTextField(
                    value = countdownSeconds,
                    onValueChange = { if (it.length <= 2) countdownSeconds = it },
                    modifier = Modifier.width(80.dp),
                    label = { Text("秒") },
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 标题输入
        OutlinedTextField(
            value = titleText,
            onValueChange = {
                titleText = it
                viewModel.setTaskTitle(it.text)
            },
            modifier = Modifier.fillMaxWidth(0.7f),
            label = { Text("任务标题") },
            singleLine = true
        )

        // TODO: 这里预留标签选择的位置

        Spacer(modifier = Modifier.height(48.dp))

        // 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Button(
                onClick = {
                    if (!isCountUp && !isRunning) {
                        // 设置倒计时
                        val h = countdownHours.toIntOrNull() ?: 0
                        val m = countdownMinutes.toIntOrNull() ?: 0
                        val s = countdownSeconds.toIntOrNull() ?: 0
                        viewModel.setCountdownTime(h * 3600 + m * 60 + s)
                    }
                    viewModel.toggleTimer()
                },
                modifier = Modifier
                    .height(64.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00BFFF)
                )
            ) {
                Text(
                    text = if (isRunning) "暂停" else "开始",
                    fontSize = 24.sp
                )
            }

            OutlinedButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .height(64.dp)
                    .width(180.dp)
            ) {
                Text(text = "重置", fontSize = 24.sp)
            }
        }
    }
}
