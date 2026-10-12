package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BoardShapeType
import com.example.engine.LevelRepository
import com.example.model.ArrowItem
import com.example.model.GameTheme
import com.example.ui.components.ArrowRenderer.drawDotGrid
import com.example.ui.components.ArrowRenderer.drawMinimalistArrow
import com.example.ui.components.ArrowRenderer.drawShakingArrow
import com.example.ui.components.ArrowRenderer.drawSlitheringEscape
import com.example.ui.components.ArrowRenderer.drawTrajectory
import kotlin.math.roundToInt

@Composable
fun PuzzleBoard(
  modifier: Modifier = Modifier,
  theme: GameTheme,
  gridWidth: Int,
  gridHeight: Int,
  arrows: List<ArrowItem>,
  highlightedArrowId: Int? = null,
  trajectoryArrow: ArrowItem? = null,
  showRuler: Boolean = false,
  shapeType: BoardShapeType? = null,
  onArrowClick: (ArrowItem) -> Unit,
  onZoomChange: (() -> Unit)? = null
) {
  // Smooth pinch-to-zoom & pan values
  var zoomScale by remember { mutableFloatStateOf(1.0f) }
  var panOffset by remember { mutableStateOf(Offset.Zero) }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    val boardAspectRatio = gridWidth.toFloat() / gridHeight.toFloat()

    // Outer aesthetic glass card container with curved corners and soft elevation
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(boardAspectRatio)
        .shadow(
          elevation = 18.dp,
          shape = RoundedCornerShape(28.dp),
          spotColor = Color(0x240284C7),
          ambientColor = Color(0x18000000)
        )
        .clip(RoundedCornerShape(28.dp))
        .background(
          brush = Brush.linearGradient(
            colors = listOf(
              theme.boardBackground,
              theme.boardBackground.copy(alpha = 0.95f),
              if (theme.isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
            )
          )
        )
        .border(
          width = 1.5.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0x550284C7),
              theme.boardBorder,
              Color(0x3338BDF8)
            )
          ),
          shape = RoundedCornerShape(28.dp)
        )
        .testTag("puzzle_board_container")
    ) {
      // Shimmer transition for hints
      val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
      val shimmerAlphaVal by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
          animation = tween(1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
      )

      // Transformable area supporting pure 2-finger pinch-to-zoom and pan gestures
      Box(
        modifier = Modifier
          .fillMaxSize()
          // 1. Pure multi-touch pinch to zoom and pan detector
          .pointerInput(gridWidth, gridHeight) {
            detectTransformGestures { _, pan, zoom, _ ->
              val targetZoom = (zoomScale * zoom).coerceIn(0.85f, 2.75f)
              if (targetZoom != zoomScale) {
                zoomScale = targetZoom
                onZoomChange?.invoke()
              }
              val maxPanX = (size.width * (zoomScale - 1f)).coerceAtLeast(0f) / 2f
              val maxPanY = (size.height * (zoomScale - 1f)).coerceAtLeast(0f) / 2f
              panOffset = Offset(
                x = (panOffset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                y = (panOffset.y + pan.y).coerceIn(-maxPanY, maxPanY)
              )
            }
          }
          // 2. Tap detector mapped with inverse matrix transforms
          .pointerInput(gridWidth, gridHeight, arrows, zoomScale, panOffset) {
            detectTapGestures { tapOffset ->
              val boxW = size.width
              val boxH = size.height

              val centerX = boxW / 2f
              val centerY = boxH / 2f

              val relativeX = tapOffset.x - centerX - panOffset.x
              val relativeY = tapOffset.y - centerY - panOffset.y

              val unzoomedX = (relativeX / zoomScale) + centerX
              val unzoomedY = (relativeY / zoomScale) + centerY

              val cellW = boxW / gridWidth
              val cellH = boxH / gridHeight

              val col = (unzoomedX / cellW).toInt().coerceIn(0, gridWidth - 1)
              val row = (unzoomedY / cellH).toInt().coerceIn(0, gridHeight - 1)

              val tapped = arrows.find { arrow ->
                !arrow.isExiting && arrow.occupiedCells.contains(col to row)
              }
              if (tapped != null) {
                onArrowClick(tapped)
              }
            }
          }
      ) {
        // GPU accelerated rendering container for pinch & pan
        Box(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
              scaleX = zoomScale
              scaleY = zoomScale
              translationX = panOffset.x
              translationY = panOffset.y
            }
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / gridWidth
            val cellH = size.height / gridHeight

            // 0. Smooth rounded shape mask tiles (soft curved cells)
            if (shapeType != null) {
              val mask = LevelRepository.generateShapeMask(shapeType, gridWidth)
              val shapeColor = Color(0xFF0284C7).copy(alpha = 0.05f)
              val shapeStrokeColor = Color(0xFF0284C7).copy(alpha = 0.08f)
              val roundPx = (cellW * 0.28f).coerceIn(4f, 10f)

              for (cell in mask) {
                drawRoundRect(
                  color = shapeColor,
                  topLeft = Offset(cell.first * cellW + 1.5f, cell.second * cellH + 1.5f),
                  size = Size(cellW - 3f, cellH - 3f),
                  cornerRadius = CornerRadius(roundPx, roundPx)
                )
                drawRoundRect(
                  color = shapeStrokeColor,
                  topLeft = Offset(cell.first * cellW + 1.5f, cell.second * cellH + 1.5f),
                  size = Size(cellW - 3f, cellH - 3f),
                  cornerRadius = CornerRadius(roundPx, roundPx),
                  style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
              }
            }

            // 1. Grid lines if ruler is enabled
            if (showRuler) {
              for (c in 0..gridWidth) {
                drawLine(
                  color = Color(0xFF93C5FD).copy(alpha = 0.5f),
                  start = Offset(c * cellW, 0f),
                  end = Offset(c * cellW, size.height),
                  strokeWidth = 2f
                )
              }
              for (r in 0..gridHeight) {
                drawLine(
                  color = Color(0xFF93C5FD).copy(alpha = 0.5f),
                  start = Offset(0f, r * cellH),
                  end = Offset(size.width, r * cellH),
                  strokeWidth = 2f
                )
              }
            }

            // Dotted guide grid
            drawDotGrid(
              gridWidth = gridWidth,
              gridHeight = gridHeight,
              cellW = cellW,
              cellH = cellH,
              dotColor = if (theme.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
            )

            // 2. Trajectory hint line
            if (trajectoryArrow != null && !trajectoryArrow.isExiting) {
              drawTrajectory(
                arrow = trajectoryArrow,
                cellW = cellW,
                cellH = cellH,
                hintColor = Color(0xFF0284C7)
              )
            }

            // 3. Static arrows with 3D tactile rendering
            arrows.forEach { arrow ->
              val isHighlighted = arrow.id == highlightedArrowId
              drawMinimalistArrow(
                arrow = arrow,
                cellW = cellW,
                cellH = cellH,
                theme = theme,
                isHighlighted = isHighlighted,
                shimmerProgress = shimmerAlphaVal
              )
            }
          }

          // Animated escape glide & rebound overlays
          arrows.forEach { arrow ->
            key(arrow.id) {
              AnimatedArrowOverlay(
                arrow = arrow,
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                theme = theme
              )
            }
          }
        }
      }

      // 4. Subtle, floating pill HUD showing zoom scale with interactive tap-to-reset
      if (zoomScale != 1.0f || panOffset != Offset.Zero) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = theme.cardBackground.copy(alpha = 0.92f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x330284C7)),
          shadowElevation = 8.dp,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier
              .clickable {
                zoomScale = 1.0f
                panOffset = Offset.Zero
                onZoomChange?.invoke()
              }
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FitScreen,
              contentDescription = "Pinch Zoom Active - Tap to Fit",
              tint = Color(0xFF0284C7),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${(zoomScale * 100).roundToInt()}% • Fit",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }
        }
      }

      // Victory celebration banner
      AnimatedVisibility(
        visible = arrows.isEmpty(),
        enter = scaleIn() + fadeIn(),
        modifier = Modifier.align(Alignment.Center)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("✨", fontSize = 44.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Puzzle Solved!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0284C7)
          )
        }
      }
    }
  }
}

/**
 * High-performance, silky smooth arrow slide exit and elastic rebound overlay.
 * Uses Animatable with a custom bezier acceleration curve and spring physics
 * to make every slide fast, snappy, responsive, and tactile.
 */
@Composable
private fun AnimatedArrowOverlay(
  arrow: ArrowItem,
  gridWidth: Int,
  gridHeight: Int,
  theme: GameTheme
) {
  if (arrow.isExiting) {
    val progress = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    // Fast, responsive launch with silky smooth follow-through (custom bezier easing)
    val fastSlideEasing = remember { CubicBezierEasing(0.16f, 1f, 0.3f, 1f) }

    LaunchedEffect(arrow.id) {
      progress.animateTo(
        targetValue = (gridWidth + gridHeight + 6).toFloat(),
        animationSpec = tween(
          durationMillis = 320,
          easing = fastSlideEasing
        )
      )
    }

    LaunchedEffect(arrow.id) {
      exitAlpha.animateTo(
        targetValue = 0f,
        animationSpec = tween(
          durationMillis = 280,
          delayMillis = 40,
          easing = FastOutSlowInEasing
        )
      )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
      val cellW = size.width / gridWidth
      val cellH = size.height / gridHeight

      ArrowRenderer.apply {
        drawSlitheringEscape(
          arrow = arrow,
          cellW = cellW,
          cellH = cellH,
          distProgress = progress.value,
          alpha = exitAlpha.value,
          accentColor = Color(0xFF0284C7)
        )
      }
    }
  }

  if (arrow.isBlockedShaking) {
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(arrow.id, arrow.isBlockedShaking) {
      // Elastic spring impulse: quick bump followed by an ultra-responsive spring snap-back
      shakeOffset.animateTo(
        targetValue = -10f,
        animationSpec = tween(durationMillis = 45, easing = FastOutSlowInEasing)
      )
      shakeOffset.animateTo(
        targetValue = 0f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessHigh
        )
      )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
      val cellW = size.width / gridWidth
      val cellH = size.height / gridHeight

      ArrowRenderer.apply {
        drawShakingArrow(
          arrow = arrow,
          cellW = cellW,
          cellH = cellH,
          shakeX = shakeOffset.value,
          alertColor = Color(0xFFEF4444)
        )
      }
    }
  }
}
