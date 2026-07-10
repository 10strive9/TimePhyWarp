package com.phywarp.time.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlipTimerDisplay(
    hours: Long,
    minutes: Long,
    seconds: Long,
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
            modifier = Modifier
        )
        FlipNumber(number = minutes)
        Text(
            text = ":",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00BFFF),
            modifier = Modifier
        )
        FlipNumber(number = seconds)
    }
}

@Composable
fun FlipNumber(
    number: Long,
    modifier: Modifier = Modifier
) {
    var previousNumber by remember { mutableStateOf(number) }
    val targetProgress by animateFloatAsState(
        targetValue = if (number != previousNumber) 1f else 0f,
        label = "flip"
    )

    LaunchedEffect(number) {
        if (number != previousNumber) {
            previousNumber = number
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // 基础显示当前数字
        Text(
            text = String.format("%02d", number),
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00BFFF)
        )

        // 如果数字变化，显示简单的动画（上下移动的效果）
        if (targetProgress > 0f && targetProgress < 1f) {
            Text(
                text = String.format("%02d", previousNumber),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00BFFF).copy(alpha = 1f - targetProgress),
                modifier = Modifier.offset(y = (-20 * targetProgress).dp)
            )
            Text(
                text = String.format("%02d", number),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00BFFF).copy(alpha = targetProgress),
                modifier = Modifier.offset(y = (20 * (1f - targetProgress)).dp)
            )
        }
    }
}
