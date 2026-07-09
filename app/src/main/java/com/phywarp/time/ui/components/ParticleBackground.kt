package com.phywarp.time.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speedX: Float,
    var speedY: Float,
    var alpha: Float
)

@Composable
fun ParticleBackground(modifier: Modifier = Modifier, color: Color) {
    val particles = remember {
        List(60) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 6f + 2f,
                speedX = (Random.nextFloat() - 0.5f) * 0.002f,
                speedY = (Random.nextFloat() - 0.5f) * 0.002f,
                alpha = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }

    var frame by remember { mutableStateOf(0L) }

    // 驱动动画的循环
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(16) // ~60fps
            frame++
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 更新粒子位置 (这里的代码会因为 frame 变化而重新执行)
        particles.forEach { p ->
            p.x += p.speedX
            p.y += p.speedY

            if (p.x < 0) p.x = 1f
            if (p.x > 1) p.x = 0f
            if (p.y < 0) p.y = 1f
            if (p.y > 1) p.y = 0f
        }

        // 绘制连线
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = (particles[i].x - particles[j].x) * canvasWidth
                val dy = (particles[i].y - particles[j].y) * canvasHeight
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                if (distance < 200f) {
                    drawLine(
                        color = color.copy(alpha = (1f - distance / 200f) * 0.15f),
                        start = Offset(particles[i].x * canvasWidth, particles[i].y * canvasHeight),
                        end = Offset(particles[j].x * canvasWidth, particles[j].y * canvasHeight),
                        strokeWidth = 1f
                    )
                }
            }
        }

        // 绘制粒子
        particles.forEach { p ->
            // 光晕
            drawCircle(
                color = color.copy(alpha = p.alpha * 0.5f),
                radius = p.radius * 2.5f,
                center = Offset(p.x * canvasWidth, p.y * canvasHeight)
            )
            // 核心
            drawCircle(
                color = color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * canvasWidth, p.y * canvasHeight)
            )
        }
    }
}
