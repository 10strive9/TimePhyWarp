package com.phywarp.time.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.ui.TimerViewModel
import com.phywarp.time.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: TimerViewModel, userPreferences: UserPreferences) {
    val timeSeconds by viewModel.timerValue.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isCountUp by viewModel.isCountUp.collectAsState()
    val taskTitle by viewModel.taskTitle.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Theme Selector
        ThemeSelector(userPreferences)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Task Title Input
        OutlinedTextField(
            value = taskTitle,
            onValueChange = { viewModel.setTaskTitle(it) },
            label = { Text("任务标题") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mode Switcher
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilterChip(
                selected = isCountUp,
                onClick = { viewModel.setMode(true) },
                label = { Text("正计时") }
            )
            Spacer(modifier = Modifier.width(16.dp))
            FilterChip(
                selected = !isCountUp,
                onClick = { viewModel.setMode(false) },
                label = { Text("倒计时") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Countdown Input
        if (!isCountUp && timeSeconds == 0L && !isRunning) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var h by remember { mutableStateOf("0") }
                var m by remember { mutableStateOf("0") }
                var s by remember { mutableStateOf("0") }
                
                TimeInput(h, "时") { h = it }
                TimeInput(m, "分") { m = it }
                TimeInput(s, "秒") { s = it }
                
                Button(onClick = {
                    val total = (h.toLongOrNull() ?: 0) * 3600 + (m.toLongOrNull() ?: 0) * 60 + (s.toLongOrNull() ?: 0)
                    viewModel.startTimer(total)
                }) {
                    Text("设定")
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Large Timer Digits
        Text(
            text = formatTime(timeSeconds),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Control Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseTimer()
                    else viewModel.startTimer()
                },
                modifier = Modifier.size(width = 160.dp, height = 64.dp)
            ) {
                Text(if (isRunning) "暂停" else if (timeSeconds > 0) "继续" else "开始", fontSize = 20.sp)
            }

            OutlinedButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.size(width = 160.dp, height = 64.dp)
            ) {
                Text("重置", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun TimeInput(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 2) onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier.width(70.dp).padding(horizontal = 4.dp),
        singleLine = true
    )
}

@Composable
fun ThemeSelector(userPreferences: UserPreferences) {
    val scope = rememberCoroutineScope()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(AppTheme.values()) { theme ->
            Button(
                onClick = { scope.launch { userPreferences.setThemeColor(theme.name) } },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
            ) {
                Text(theme.label, color = if (theme == AppTheme.WHITE) Color.Black else Color.White)
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}
