package com.phywarp.time.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.random.Random

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

data class Particle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speedX: Float,
    var speedY: Float,
    var alpha: Float,
    var colorShift: Float
)

@Composable
fun ParticleBackground(modifier: Modifier = Modifier, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val particles = remember {
        List(60) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 6f + 2f,
                speedX = (Random.nextFloat() - 0.5f) * 0.0008f,
                speedY = (Random.nextFloat() - 0.5f) * 0.0008f,
                alpha = Random.nextFloat() * 0.3f + 0.1f,
                colorShift = Random.nextFloat()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val drawContextSize = this.size
        val canvasWidth = drawContextSize.width
        val canvasHeight = drawContextSize.height

        // Draw connections between close particles
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

        particles.forEach { p ->
            p.x += p.speedX
            p.y += p.speedY

            if (p.x < 0) p.x = 1f
            if (p.x > 1) p.x = 0f
            if (p.y < 0) p.y = 1f
            if (p.y > 1) p.y = 0f

            // Glow effect
            drawCircle(
                color = color.copy(alpha = p.alpha * 0.5f),
                radius = p.radius * 2.5f,
                center = Offset(p.x * canvasWidth, p.y * canvasHeight)
            )
            
            drawCircle(
                color = color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * canvasWidth, p.y * canvasHeight)
            )
        }
    }
}
