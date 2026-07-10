package com.phywarp.time.ui.screens

import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.phywarp.time.ui.components.StaticParticleBackground
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FullscreenClockScreen(
    modifier: Modifier = Modifier
) {
    // 保持屏幕常亮
    val view = LocalView.current
    val window = (view.context as? androidx.activity.ComponentActivity)?.window
    LaunchedEffect(Unit) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window!!, view).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // 每秒更新时间
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    // 格式化时间
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA)
    val timeStr = timeFormat.format(Date(currentTime))
    val dateStr = dateFormat.format(Date(currentTime))

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 静态粒子背景
        StaticParticleBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 日期
            Text(
                text = dateStr,
                fontSize = 32.sp,
                color = Color(0xFF888888),
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 超大时间
            Text(
                text = timeStr,
                fontSize = 200.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00BFFF)
            )
        }
    }
}
