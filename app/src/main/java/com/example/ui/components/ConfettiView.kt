package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
  val x: Float,
  val speedY: Float,
  val speedX: Float,
  val size: Float,
  val color: Color,
  val rotationSpeed: Float,
  val initialRotation: Float
)

@Composable
fun ConfettiView(
  modifier: Modifier = Modifier,
  particleCount: Int = 70
) {
  val colors = listOf(
    Color(0xFF38BDF8),
    Color(0xFF818CF8),
    Color(0xFFF472B6),
    Color(0xFFFBBF24),
    Color(0xFF34D399),
    Color(0xFFA78BFA),
    Color(0xFFFB923C)
  )

  val particles = remember {
    val rand = Random(System.currentTimeMillis())
    List(particleCount) {
      ConfettiParticle(
        x = rand.nextFloat(),
        speedY = rand.nextFloat() * 0.8f + 0.6f,
        speedX = (rand.nextFloat() - 0.5f) * 0.3f,
        size = rand.nextFloat() * 14f + 8f,
        color = colors[rand.nextInt(colors.size)],
        rotationSpeed = (rand.nextFloat() - 0.5f) * 720f,
        initialRotation = rand.nextFloat() * 360f
      )
    }
  }

  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
    )
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    val t = progress.value
    particles.forEach { p ->
      val currentY = -40f + (size.height + 80f) * (t * p.speedY)
      val currentX = (p.x * size.width) + (p.speedX * size.width * t)
      val rotation = p.initialRotation + (p.rotationSpeed * t)
      val alpha = (1f - (t * 0.8f)).coerceIn(0f, 1f)

      rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
        drawRect(
          color = p.color.copy(alpha = alpha),
          topLeft = Offset(currentX - p.size / 2, currentY - p.size / 2),
          size = Size(p.size, p.size * 0.6f)
        )
      }
    }
  }
}
