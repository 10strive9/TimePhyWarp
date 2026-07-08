package com.phywarp.time.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.ui.TimerViewModel
import com.phywarp.time.ui.components.ParticleBackground
import com.phywarp.time.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: TimerViewModel, userPreferences: UserPreferences) {
    val timeSeconds by viewModel.timerValue.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isCountUp by viewModel.isCountUp.collectAsState()
    val taskTitle by viewModel.taskTitle.collectAsState()
    
    val timerFontSize by userPreferences.timerFontSize.collectAsState(initial = 160f)
    
    val scope = rememberCoroutineScope()
    
    var currentTime by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Futuristic Background
        ParticleBackground(color = MaterialTheme.colorScheme.primary)
        
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Clock and Theme
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current Clock
                Column {
                    Text(
                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTime),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                ThemeSelector(userPreferences)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Task Title Card
            Surface(
                modifier = Modifier.fillMaxWidth(0.7f).shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ) {
                TextField(
                    value = taskTitle,
                    onValueChange = { viewModel.setTaskTitle(it) },
                    placeholder = { Text("输入任务目标...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mode Switcher (Glassmorphism)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                ModeButton("正计时", isCountUp) { viewModel.setMode(true) }
                ModeButton("倒计时", !isCountUp) { viewModel.setMode(false) }
            }

            // Countdown Input Area
            if (!isCountUp && timeSeconds == 0L && !isRunning) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var h by remember { mutableStateOf("00") }
                    var m by remember { mutableStateOf("25") }
                    var s by remember { mutableStateOf("00") }
                    
                    TimeInputCard(h, "H") { h = it }
                    Text(":", color = Color.Gray, fontSize = 24.sp)
                    TimeInputCard(m, "M") { m = it }
                    Text(":", color = Color.Gray, fontSize = 24.sp)
                    TimeInputCard(s, "S") { s = it }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    IconButton(
                        onClick = {
                            val total = (h.toLongOrNull() ?: 0) * 3600 + (m.toLongOrNull() ?: 0) * 60 + (s.toLongOrNull() ?: 0)
                            viewModel.startTimer(total)
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Massive Timer Display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = timerFontSize.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                )
                
                // Font Size Slider (Hidden when running to keep focus)
                AnimatedVisibility(visible = !isRunning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(300.dp).padding(top = 16.dp)
                    ) {
                        Text("A-", color = Color.Gray, fontSize = 12.sp)
                        Slider(
                            value = timerFontSize,
                            onValueChange = { scope.launch { userPreferences.setTimerFontSize(it) } },
                            valueRange = 80f..300f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text("A+", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.2f))

            // Futuristic Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainControlButton(
                    text = if (isRunning) "PAUSE" else if (timeSeconds > 0) "RESUME" else "START",
                    primary = true,
                    onClick = {
                        if (isRunning) viewModel.pauseTimer()
                        else viewModel.startTimer()
                    }
                )
                
                Spacer(modifier = Modifier.width(32.dp))
                
                MainControlButton(
                    text = "RESET",
                    primary = false,
                    onClick = { viewModel.resetTimer() }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TimeInputCard(value: String, label: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= 2) onValueChange(it) },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun MainControlButton(text: String, primary: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(72.dp)
            .width(200.dp)
            .shadow(if (primary) 12.dp else 0.dp, RoundedCornerShape(36.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        shape = RoundedCornerShape(36.dp),
        border = if (!primary) BorderStroke(2.dp, Color.Gray) else null
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = if (primary) Color.Black else Color.Gray,
            letterSpacing = 2.sp
        )
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
