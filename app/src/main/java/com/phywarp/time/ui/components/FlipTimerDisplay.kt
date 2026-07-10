package com.phywarp.time.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 简单的翻页效果数字组件
@Composable
fun FlipNumber(
    number: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontSize = 120.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF00BFFF)
    )
) {
    var previousNumber by remember { mutableStateOf(number) }
    val flipProgress = remember { Animatable(0f) }

    LaunchedEffect(number) {
        if (number != previousNumber) {
            flipProgress.snapTo(0f)
            flipProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200)
            )
            previousNumber = number
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // 静态显示当前数字（上半部分）
        Text(
            text = String.format("%02d", number),
            style = style,
            color = style.color.copy(alpha = 0.6f)
        )

        // 翻页动画部分
        if (flipProgress.value > 0f && flipProgress.value < 1f) {
            // 简单的视觉提示：稍微移动下数字
            Text(
                text = String.format("%02d", previousNumber),
                style = style,
                color = style.color.copy(alpha = 1f - flipProgress.value),
                modifier = Modifier.translate(
                    y = (-10 * flipProgress.value).dp
                )
            )
            Text(
                text = String.format("%02d", number),
                style = style,
                color = style.color.copy(alpha = flipProgress.value),
                modifier = Modifier.translate(
                    y = (10 * (1 - flipProgress.value)).dp
                )
            )
        }
    }
}

// 格式化显示的计时器（带有翻页效果提示）
@Composable
fun FlipTimerDisplay(
    hours: Int,
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipNumber(number = hours)
        Text(
            text = ":",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00BFFF),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        FlipNumber(number = minutes)
        Text(
            text = ":",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00BFFF),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        FlipNumber(number = seconds)
    }
}
