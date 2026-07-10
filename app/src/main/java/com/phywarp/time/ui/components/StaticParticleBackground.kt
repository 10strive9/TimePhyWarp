package com.phywarp.time.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

// 静态粒子背景，只有点缀效果，不动
@Composable
fun StaticParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    // 预先定义好粒子位置，每次都一样
    val particles = remember {
        List(particleCount) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = 3f + Random.nextFloat() * 7f,
                alpha = 0.1f + Random.nextFloat() * 0.3f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 绘制静态粒子
        particles.forEach { p ->
            drawCircle(
                color = Color(0xFF00BFFF).copy(alpha = p.alpha), // 霓虹蓝
                radius = p.radius,
                center = Offset(p.x * width, p.y * height)
            )
        }

        // 绘制少量静态连线（增加氛围感）
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val p1 = particles[i]
                val p2 = particles[j]
                val dx = (p1.x - p2.x) * width
                val dy = (p1.y - p2.y) * height
                val distSq = dx * dx + dy * dy
                if (distSq < 250000f) { // 200dp 的平方
                    val alpha = (1 - distSq / 250000f) * 0.3f
                    drawLine(
                        color = Color(0xFF00BFFF).copy(alpha = alpha),
                        start = Offset(p1.x * width, p1.y * height),
                        end = Offset(p2.x * width, p2.y * height),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

private data class ParticleData(
    val x: Float, // 0-1
    val y: Float, // 0-1
    val radius: Float,
    val alpha: Float
)
