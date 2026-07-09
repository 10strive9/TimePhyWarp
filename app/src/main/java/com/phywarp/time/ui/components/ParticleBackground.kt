package com.phywarp.time.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val speedX: Float,
    val speedY: Float,
    val alpha: Float
)

@Composable
fun ParticleBackground(modifier: Modifier = Modifier, color: Color) {
    // 初始化粒子
    val particles = remember {
        List(80) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 4f + 1.5f,
                speedX = (Random.nextFloat() - 0.5f) * 0.003f,
                speedY = (Random.nextFloat() - 0.5f) * 0.003f,
                alpha = Random.nextFloat() * 0.4f + 0.1f
            )
        }
    }

    // 使用一个无限循环的协程来驱动粒子更新
    // 这确保了不管 UI 有没有其他操作，粒子都会动
    val frameState = remember { mutableStateOf(0L) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60fps
            frameState.value++
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // 只要 frameState 变化，这个绘制块就会重新执行
        val width = size.width
        val height = size.height

        // 1. 更新所有粒子位置
        particles.forEach { p ->
            p.x += p.speedX
            p.y += p.speedY

            // 边界反弹逻辑
            if (p.x < 0f || p.x > 1f) p.speedX *= -1
            if (p.y < 0f || p.y > 1f) p.speedY *= -1
        }

        // 2. 绘制粒子之间的连线
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = (particles[i].x - particles[j].x) * width
                val dy = (particles[i].y - particles[j].y) * height
                val distSq = dx * dx + dy * dy
                
                if (distSq < 200 * 200) { // 距离判断 (避免开平方运算，更快)
                    val lineAlpha = (1f - sqrt(distSq) / 200f) * 0.15f
                    drawLine(
                        color = color.copy(alpha = lineAlpha),
                        start = Offset(particles[i].x * width, particles[i].y * height),
                        end = Offset(particles[j].x * width, particles[j].y * height),
                        strokeWidth = 1f
                    )
                }
            }
        }

        // 3. 绘制粒子和光晕
        particles.forEach { p ->
            // 光晕
            drawCircle(
                color = color.copy(alpha = p.alpha * 0.6f),
                radius = p.radius * 2.5f,
                center = Offset(p.x * width, p.y * height)
            )
            // 粒子核心
            drawCircle(
                color = color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * width, p.y * height)
            )
        }
    }
}
