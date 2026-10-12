import re

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

# Replace AnimatedArrowOverlay completely
old_overlay = content[content.find("private fun AnimatedArrowOverlay("):]

new_overlay = """private fun AnimatedArrowOverlay(
  arrow: ArrowItem,
  gridWidth: Int,
  gridHeight: Int,
  theme: GameTheme
) {
  if (arrow.isExiting) {
    val progress = remember { androidx.compose.animation.core.Animatable(0f) }
    val exitAlpha = remember { androidx.compose.animation.core.Animatable(1f) }

    androidx.compose.runtime.LaunchedEffect(arrow.id) {
      progress.animateTo(
        targetValue = (gridWidth + gridHeight + 4).toFloat(),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 360, easing = androidx.compose.animation.core.LinearEasing)
      )
    }
    androidx.compose.runtime.LaunchedEffect(arrow.id) {
      exitAlpha.animateTo(
        targetValue = 0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 360, delayMillis = 100, easing = androidx.compose.animation.core.FastOutSlowInEasing)
      )
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
      val cellW = size.width / gridWidth
      val cellH = size.height / gridHeight

      com.example.ui.components.ArrowRenderer.apply {
        drawSlitheringEscape(
          arrow = arrow,
          cellW = cellW,
          cellH = cellH,
          distProgress = progress.value,
          alpha = exitAlpha.value,
          accentColor = Color(0xFF0091FF)
        )
      }
    }
  }

  if (arrow.isBlockedShaking) {
    val shakeX = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(arrow.id, arrow.isBlockedShaking) {
      shakeX.animateTo(
        targetValue = 0f,
        animationSpec = androidx.compose.animation.core.keyframes {
          durationMillis = 320
          -12f at 40
          12f at 90
          -10f at 140
          10f at 200
          -5f at 260
          0f at 320
        }
      )
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
      val cellW = size.width / gridWidth
      val cellH = size.height / gridHeight

      com.example.ui.components.ArrowRenderer.apply {
        drawShakingArrow(
          arrow = arrow,
          cellW = cellW,
          cellH = cellH,
          shakeX = shakeX.value,
          alertColor = Color(0xFFEF4444)
        )
      }
    }
  }
}
"""

content = content[:content.find("private fun AnimatedArrowOverlay(")] + new_overlay

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
