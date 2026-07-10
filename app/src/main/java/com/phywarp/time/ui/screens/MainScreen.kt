package com.phywarp.time.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phywarp.time.ui.components.StaticParticleBackground
import com.phywarp.time.viewmodel.TimerViewModel

// 定义导航页面
sealed class Screen(val title: String, val icon: ImageVector) {
    object Timer : Screen("计时", Icons.Filled.Timer)
    object Log : Screen("日志", Icons.Filled.History)
    object Clock : Screen("时钟", Icons.Filled.AccessTime)
}

@Composable
fun MainScreen(
    viewModel: TimerViewModel = viewModel()
) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Timer) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0A0A0A) // 深色背景
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 静态粒子背景
            StaticParticleBackground()

            // 主内容区域
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // 页面内容（占上方大部分空间）
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (selectedScreen) {
                        Screen.Timer -> TimerScreen(viewModel = viewModel)
                        Screen.Log -> LogScreen()
                        Screen.Clock -> FullscreenClockScreen()
                    }
                }

                // 新的底部导航栏（美观版！）
                CustomBottomNavigation(
                    selectedScreen = selectedScreen,
                    onScreenSelected = { selectedScreen = it }
                )
            }
        }
    }
}

// 自定义美观底部导航栏
@Composable
fun CustomBottomNavigation(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val screens = listOf(Screen.Clock, Screen.Timer, Screen.Log)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x22FFFFFF)) // 半透明白
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEach { screen ->
            val isSelected = selectedScreen == screen
            NavItem(
                screen = screen,
                isSelected = isSelected,
                onClick = { onScreenSelected(screen) }
            )
        }
    }
}

// 单个导航项
@Composable
fun NavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            tint = if (isSelected) Color(0xFF00BFFF) else Color(0xFF888888),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = screen.title,
            color = if (isSelected) Color(0xFF00BFFF) else Color(0xFF888888)
        )
    }
}
