package com.example.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.ArrowItem
import com.example.model.Direction
import com.example.model.GameTheme
import kotlin.math.min

/**
 * Ultra-realistic, 3D tactile DrawScope renderer for arrow puzzle items.
 * Features realistic cylindrical lighting, bevel reflections, glossy specular spine,
 * embossed chevron arrowheads, and high-fidelity slide motion effects.
 */
object ArrowRenderer {

  /**
   * Renders a static / idle arrow on the puzzle grid with realistic 3D tactile depth,
   * specular gloss reflections, and subtle drop shadows.
   */
  fun DrawScope.drawMinimalistArrow(
    arrow: ArrowItem,
    cellW: Float,
    cellH: Float,
    theme: GameTheme,
    isHighlighted: Boolean = false,
    shimmerProgress: Float = 0f
  ) {
    if (arrow.isExiting || arrow.isBlockedShaking) return

    val minDim = min(cellW, cellH)
    val strokeWidth = (minDim * 0.095f).coerceIn(2.8f, 5.2f)
    val headSize = (minDim * 0.44f).coerceIn(10f, 26f)

    // Palette: Sleek obsidian/slate with metallic highlights or neon blue highlight
    val baseColor = if (isHighlighted) Color(0xFF0284C7) else Color(0xFF1E293B)
    val coreColor = if (isHighlighted) Color(0xFF38BDF8) else Color(0xFF334155)
    val reflectionColor = if (isHighlighted) Color(0xFFE0F2FE) else Color(0x90CBD5E1)
    val shadowColor = Color.Black.copy(alpha = 0.28f)

    val path = arrow.pathPoints

    // 1. Draw Arrow Polyline Body with 3D cylindrical lighting
    if (path.size > 1) {
      val bodyPath = Path().apply {
        val first = path.first()
        moveTo((first.first + 0.5f) * cellW, (first.second + 0.5f) * cellH)
        for (i in 1 until path.size) {
          val pt = path[i]
          lineTo((pt.first + 0.5f) * cellW, (pt.second + 0.5f) * cellH)
        }
      }

      // Outer glow aura when highlighted or hinted
      if (isHighlighted) {
        drawPath(
          path = bodyPath,
          color = Color(0x660284C7),
          style = Stroke(
            width = strokeWidth * 2.8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
          )
        )
      }

      // 3D Drop Shadow (offset slightly down and right)
      val shadowOffsetPx = strokeWidth * 0.4f
      val shadowPath = Path().apply {
        val first = path.first()
        moveTo((first.first + 0.5f) * cellW + shadowOffsetPx, (first.second + 0.5f) * cellH + shadowOffsetPx)
        for (i in 1 until path.size) {
          val pt = path[i]
          lineTo((pt.first + 0.5f) * cellW + shadowOffsetPx, (pt.second + 0.5f) * cellH + shadowOffsetPx)
        }
      }
      drawPath(
        path = shadowPath,
        color = shadowColor,
        style = Stroke(
          width = strokeWidth * 1.3f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Layer 1: Dark outer bevel border
      drawPath(
        path = bodyPath,
        color = baseColor,
        style = Stroke(
          width = strokeWidth + 2.2f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Layer 2: Main core line
      drawPath(
        path = bodyPath,
        color = coreColor,
        style = Stroke(
          width = strokeWidth,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Layer 3: Top specular gloss sheen reflection along the spine
      drawPath(
        path = bodyPath,
        color = reflectionColor,
        style = Stroke(
          width = strokeWidth * 0.36f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }

    // 2. Draw Arrow Head at head coordinates (arrow.x, arrow.y)
    val headCenterX = (arrow.x + 0.5f) * cellW
    val headCenterY = (arrow.y + 0.5f) * cellH

    // Highlight aura behind head
    if (isHighlighted) {
      drawCircle(
        color = Color(0x550284C7),
        radius = headSize * 0.95f,
        center = Offset(headCenterX, headCenterY)
      )
    }

    // Embossed Chevron Head with realistic depth
    rotate(degrees = arrow.direction.angle, pivot = Offset(headCenterX, headCenterY)) {
      drawRealisticChevronHead(
        center = Offset(headCenterX, headCenterY),
        size = headSize,
        baseColor = baseColor,
        coreColor = coreColor,
        reflectionColor = reflectionColor,
        strokeWidth = strokeWidth
      )
    }
  }

  /**
   * Silky smooth, dynamic slide escape rendering with motion trail and specular glide.
   */
  fun DrawScope.drawSlitheringEscape(
    arrow: ArrowItem,
    cellW: Float,
    cellH: Float,
    distProgress: Float,
    alpha: Float,
    accentColor: Color = Color(0xFF0284C7)
  ) {
    val originalPath = arrow.pathPoints
    val minDim = min(cellW, cellH)
    val strokeWidth = (minDim * 0.10f).coerceIn(3.0f, 5.5f)
    val headSize = (minDim * 0.44f).coerceIn(10f, 26f)
    val color = accentColor.copy(alpha = alpha)
    val reflection = Color.White.copy(alpha = alpha * 0.85f)

    val pts = mutableListOf<Offset>()
    for (p in originalPath) {
      pts.add(Offset((p.first + 0.5f) * cellW, (p.second + 0.5f) * cellH))
    }

    val headDirUnit = when (arrow.direction) {
      Direction.UP -> Offset(0f, -1f)
      Direction.DOWN -> Offset(0f, 1f)
      Direction.LEFT -> Offset(-1f, 0f)
      Direction.RIGHT -> Offset(1f, 0f)
    }

    val segLengths = mutableListOf<Float>()
    var totalLen = 0f
    for (i in 0 until pts.size - 1) {
      val d = (pts[i + 1] - pts[i]).getDistance()
      segLengths.add(d)
      totalLen += d
    }

    val advancePx = distProgress * minDim
    val tailAdvancePx = advancePx
    val headAdvancePx = advancePx + totalLen

    fun getPointAtDistance(dist: Float): Offset {
      var cur = dist
      if (cur <= 0f) return pts.first()
      for (i in 0 until segLengths.size) {
        if (cur <= segLengths[i]) {
          val frac = cur / segLengths[i]
          return pts[i] + (pts[i + 1] - pts[i]) * frac
        }
        cur -= segLengths[i]
      }
      return pts.last() + headDirUnit * cur
    }

    val animatedPts = mutableListOf<Offset>()
    animatedPts.add(getPointAtDistance(tailAdvancePx))

    var accLen = 0f
    for (i in 0 until segLengths.size) {
      accLen += segLengths[i]
      if (accLen > tailAdvancePx && accLen < headAdvancePx) {
        animatedPts.add(pts[i + 1])
      }
    }
    animatedPts.add(getPointAtDistance(headAdvancePx))

    if (animatedPts.size > 1) {
      val linePath = Path().apply {
        moveTo(animatedPts.first().x, animatedPts.first().y)
        for (i in 1 until animatedPts.size) {
          lineTo(animatedPts[i].x, animatedPts[i].y)
        }
      }

      // 1. Motion Trail Glow
      drawPath(
        path = linePath,
        color = color.copy(alpha = alpha * 0.5f),
        style = Stroke(width = strokeWidth * 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // 2. Drop Shadow
      drawPath(
        path = linePath,
        color = Color.Black.copy(alpha = alpha * 0.3f),
        style = Stroke(width = strokeWidth * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // 3. Dark Outer Border
      drawPath(
        path = linePath,
        color = color.copy(alpha = alpha * 0.9f),
        style = Stroke(
          width = strokeWidth + 2f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // 4. Main Body
      drawPath(
        path = linePath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // 5. Specular Reflection Sheen
      drawPath(
        path = linePath,
        color = reflection,
        style = Stroke(width = strokeWidth * 0.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )
    }

    val curHead = animatedPts.last()
    rotate(degrees = arrow.direction.angle, pivot = curHead) {
      drawRealisticChevronHead(
        center = curHead,
        size = headSize,
        baseColor = color,
        coreColor = color,
        reflectionColor = reflection,
        strokeWidth = strokeWidth
      )
    }
  }

  /**
   * Renders a blocked arrow with elastic vibration and vivid red alert flash.
   */
  fun DrawScope.drawShakingArrow(
    arrow: ArrowItem,
    cellW: Float,
    cellH: Float,
    shakeX: Float,
    alertColor: Color = Color(0xFFEF4444)
  ) {
    val minDim = min(cellW, cellH)
    val strokeWidth = (minDim * 0.095f).coerceIn(2.8f, 5.2f)
    val headSize = (minDim * 0.44f).coerceIn(10f, 26f)
    val path = arrow.pathPoints

    if (path.size > 1) {
      val linePath = Path().apply {
        val first = path.first()
        moveTo((first.first + 0.5f) * cellW + shakeX, (first.second + 0.5f) * cellH)
        for (i in 1 until path.size) {
          val pt = path[i]
          lineTo((pt.first + 0.5f) * cellW + shakeX, (pt.second + 0.5f) * cellH)
        }
      }

      // Red Alert Halo
      drawPath(
        path = linePath,
        color = alertColor.copy(alpha = 0.4f),
        style = Stroke(width = strokeWidth * 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // Shadow
      drawPath(
        path = linePath,
        color = Color.Black.copy(alpha = 0.3f),
        style = Stroke(width = strokeWidth * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // Main alert stroke
      drawPath(
        path = linePath,
        color = alertColor,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // Inner white/pink specular
      drawPath(
        path = linePath,
        color = Color(0xFFFECACA),
        style = Stroke(width = strokeWidth * 0.35f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )
    }

    val headCenterX = (arrow.x + 0.5f) * cellW + shakeX
    val headCenterY = (arrow.y + 0.5f) * cellH

    drawCircle(
      color = alertColor.copy(alpha = 0.3f),
      radius = headSize * 0.9f,
      center = Offset(headCenterX, headCenterY)
    )

    rotate(degrees = arrow.direction.angle, pivot = Offset(headCenterX, headCenterY)) {
      drawRealisticChevronHead(
        center = Offset(headCenterX, headCenterY),
        size = headSize,
        baseColor = alertColor,
        coreColor = alertColor,
        reflectionColor = Color(0xFFFECACA),
        strokeWidth = strokeWidth
      )
    }
  }

  /**
   * Draws a realistic, 3D chevron arrow tip with bevel edge and specular reflection.
   */
  private fun DrawScope.drawRealisticChevronHead(
    center: Offset,
    size: Float,
    baseColor: Color,
    coreColor: Color,
    reflectionColor: Color,
    strokeWidth: Float
  ) {
    val cx = center.x
    val cy = center.y
    val halfS = size / 2f

    val headTop = cy - halfS * 0.96f
    val headLeft = cx - halfS * 0.74f
    val headRight = cx + halfS * 0.74f
    val headBase = cy + halfS * 0.18f

    val chevronPath = Path().apply {
      moveTo(headLeft, headBase)
      lineTo(cx, headTop)
      lineTo(headRight, headBase)
    }

    // 1. Soft Drop Shadow
    val shadowOffset = strokeWidth * 0.35f
    val shadowChevron = Path().apply {
      moveTo(headLeft + shadowOffset, headBase + shadowOffset)
      lineTo(cx + shadowOffset, headTop + shadowOffset)
      lineTo(headRight + shadowOffset, headBase + shadowOffset)
    }
    drawPath(
      path = shadowChevron,
      color = Color.Black.copy(alpha = 0.28f),
      style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Base Dark Bevel Outline
    drawPath(
      path = chevronPath,
      color = baseColor,
      style = Stroke(width = strokeWidth + 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3. Core Chevron
    drawPath(
      path = chevronPath,
      color = coreColor,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 4. Inner Specular Highlight Sheen
    drawPath(
      path = chevronPath,
      color = reflectionColor,
      style = Stroke(width = strokeWidth * 0.36f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
  }

  /**
   * Draws a subtle dotted grid background.
   */
  fun DrawScope.drawDotGrid(
    gridWidth: Int,
    gridHeight: Int,
    cellW: Float,
    cellH: Float,
    dotColor: Color
  ) {
    val radius = (min(cellW, cellH) * 0.055f).coerceIn(1.5f, 2.6f)
    for (c in 0 until gridWidth) {
      for (r in 0 until gridHeight) {
        val cx = (c + 0.5f) * cellW
        val cy = (r + 0.5f) * cellH
        drawCircle(
          color = dotColor,
          radius = radius,
          center = Offset(cx, cy)
        )
      }
    }
  }

  /**
   * Draws the electric escape trajectory line for hints.
   */
  fun DrawScope.drawTrajectory(
    arrow: ArrowItem,
    cellW: Float,
    cellH: Float,
    hintColor: Color
  ) {
    val startX = (arrow.x + 0.5f) * cellW
    val startY = (arrow.y + 0.5f) * cellH
    var endX = startX
    var endY = startY

    when (arrow.direction) {
      Direction.UP -> endY = -cellH * 2f
      Direction.DOWN -> endY = size.height + cellH * 2f
      Direction.LEFT -> endX = -cellW * 2f
      Direction.RIGHT -> endX = size.width + cellW * 2f
    }

    // Drop Shadow
    drawLine(
      color = Color.Black.copy(alpha = 0.2f),
      start = Offset(startX + 2f, startY + 2f),
      end = Offset(endX + 2f, endY + 2f),
      strokeWidth = 9f,
      cap = StrokeCap.Round
    )

    drawLine(
      color = hintColor.copy(alpha = 0.3f),
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 7f,
      cap = StrokeCap.Round
    )

    drawLine(
      color = hintColor,
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 3f,
      cap = StrokeCap.Round,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
    )
  }
}
