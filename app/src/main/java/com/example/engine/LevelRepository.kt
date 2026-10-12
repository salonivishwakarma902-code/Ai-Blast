package com.example.engine

import com.example.model.ArrowItem
import com.example.model.DifficultyTier
import com.example.model.Direction
import com.example.model.PuzzleLevel
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

enum class BoardShapeType(val displayName: String, val description: String) {
  HEART("Heart", "Anatomical cardio-lobed silhouette"),
  GLASSES("Spectacles", "Dual curved optical frames"),
  TV("Retro TV", "Classic display monitor with antennae"),
  FAN("Aerofan", "Sweeping quad aerodynamic vortex"),
  STAR("Radiant Star", "Geometric multi-point celestial"),
  ROCKET("Starship", "Streamlined aerodynamic aerospace craft"),
  BUTTERFLY("Monarch", "Bilateral symmetrical winged flutter"),
  DIAMOND("Brilliant", "Faceted jewel crystalline contour"),
  HOURGLASS("Chronometer", "Dual converging flow chamber"),
  SHIELD("Aegis", "Defensive heraldic crest"),
  PYRAMID("Monolith", "Stepped tiered monument"),
  RING("Halo Ring", "Concentric orbital torus"),
  LABYRINTH("Maze Matrix", "Interlocking pathway maze")
}

object LevelRepository {

  private val levelsCache = mutableMapOf<Int, PuzzleLevel>()

  fun getLevel(levelId: Int): PuzzleLevel {
    return levelsCache.getOrPut(levelId) {
      buildLevel(levelId)
    }
  }

  fun getTotalLevels(): Int = 100

  fun getDailyLevel(calendar: Calendar = Calendar.getInstance()): PuzzleLevel {
    val year = calendar.get(Calendar.YEAR)
    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

    val shapeRotation = when (dayOfYear % 8) {
      0 -> BoardShapeType.HEART
      1 -> BoardShapeType.GLASSES
      2 -> BoardShapeType.TV
      3 -> BoardShapeType.FAN
      4 -> BoardShapeType.STAR
      5 -> BoardShapeType.ROCKET
      6 -> BoardShapeType.BUTTERFLY
      else -> BoardShapeType.DIAMOND
    }

    return generateShapedLevel(
      id = 9999,
      name = "Daily Challenge (${shapeRotation.displayName})",
      tier = DifficultyTier.DAILY,
      shapeType = shapeRotation,
      gridDim = 12,
      targetArrows = 42,
      seed = 8888L + year * 365 + dayOfYear
    )
  }

  private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

  private fun buildLevel(id: Int): PuzzleLevel {
    val (shape, gridDim, arrowCount, tier) = when {
      id == 1 -> Quadruple(BoardShapeType.HEART, 10, 16, DifficultyTier.EASY)
      id == 2 -> Quadruple(BoardShapeType.GLASSES, 11, 20, DifficultyTier.EASY)
      id == 3 -> Quadruple(BoardShapeType.TV, 11, 24, DifficultyTier.EASY)
      id == 4 -> Quadruple(BoardShapeType.FAN, 11, 26, DifficultyTier.NORMAL)
      id == 5 -> Quadruple(BoardShapeType.STAR, 11, 30, DifficultyTier.NORMAL)
      id == 6 -> Quadruple(BoardShapeType.ROCKET, 12, 34, DifficultyTier.NORMAL)
      id == 7 -> Quadruple(BoardShapeType.BUTTERFLY, 12, 36, DifficultyTier.NORMAL)
      id == 8 -> Quadruple(BoardShapeType.DIAMOND, 12, 40, DifficultyTier.HARD)
      id in 9..15 -> {
        val s = when ((id - 9) % 7) {
          0 -> BoardShapeType.HEART
          1 -> BoardShapeType.HOURGLASS
          2 -> BoardShapeType.GLASSES
          3 -> BoardShapeType.TV
          4 -> BoardShapeType.FAN
          5 -> BoardShapeType.STAR
          else -> BoardShapeType.ROCKET
        }
        val count = 28 + (id - 9) * 2
        Quadruple(s, 11, count, DifficultyTier.NORMAL)
      }
      id in 16..25 -> {
        val s = when ((id - 16) % 8) {
          0 -> BoardShapeType.BUTTERFLY
          1 -> BoardShapeType.SHIELD
          2 -> BoardShapeType.HEART
          3 -> BoardShapeType.GLASSES
          4 -> BoardShapeType.TV
          5 -> BoardShapeType.FAN
          6 -> BoardShapeType.STAR
          else -> BoardShapeType.DIAMOND
        }
        val count = 38 + (id - 16) * 2
        Quadruple(s, 12, count, DifficultyTier.HARD)
      }
      id in 26..40 -> {
        val s = when ((id - 26) % 8) {
          0 -> BoardShapeType.PYRAMID
          1 -> BoardShapeType.ROCKET
          2 -> BoardShapeType.HEART
          3 -> BoardShapeType.GLASSES
          4 -> BoardShapeType.TV
          5 -> BoardShapeType.FAN
          6 -> BoardShapeType.BUTTERFLY
          else -> BoardShapeType.RING
        }
        val count = 52 + (id - 26) * 2
        Quadruple(s, 13, count, DifficultyTier.HARDER)
      }
      id in 41..60 -> {
        val s = when ((id - 41) % 9) {
          0 -> BoardShapeType.LABYRINTH
          1 -> BoardShapeType.STAR
          2 -> BoardShapeType.HEART
          3 -> BoardShapeType.GLASSES
          4 -> BoardShapeType.TV
          5 -> BoardShapeType.FAN
          6 -> BoardShapeType.ROCKET
          7 -> BoardShapeType.BUTTERFLY
          else -> BoardShapeType.DIAMOND
        }
        val count = 72 + (id - 41) * 2
        Quadruple(s, 13, count, DifficultyTier.HARDER)
      }
      id in 61..85 -> {
        val s = when ((id - 61) % 8) {
          0 -> BoardShapeType.HEART
          1 -> BoardShapeType.GLASSES
          2 -> BoardShapeType.TV
          3 -> BoardShapeType.FAN
          4 -> BoardShapeType.STAR
          5 -> BoardShapeType.ROCKET
          6 -> BoardShapeType.BUTTERFLY
          else -> BoardShapeType.HOURGLASS
        }
        val count = 100 + (id - 61) * 2
        Quadruple(s, 14, count, DifficultyTier.IMPOSSIBLE)
      }
      else -> {
        val s = when ((id - 86) % 7) {
          0 -> BoardShapeType.HEART
          1 -> BoardShapeType.TV
          2 -> BoardShapeType.GLASSES
          3 -> BoardShapeType.ROCKET
          4 -> BoardShapeType.STAR
          5 -> BoardShapeType.BUTTERFLY
          else -> BoardShapeType.DIAMOND
        }
        val count = 135 + ((id - 86) * 25 / 14)
        Quadruple(s, 15, count, DifficultyTier.IMPOSSIBLE)
      }
    }

    val levelName = when {
      id == 1 -> "Heart Chamber Escape"
      id == 2 -> "Spectacles Optical Maze"
      id == 3 -> "Retro Screen Puzzle"
      id == 4 -> "Vortex Fan Blades"
      id == 5 -> "Starlight Constellation"
      id == 6 -> "Starship Launch Vector"
      id == 7 -> "Monarch Wing Flight"
      id == 8 -> "Prism Diamond Facet"
      else -> "${shape.displayName} Flight Path"
    }

    return generateShapedLevel(
      id = id,
      name = levelName,
      tier = tier,
      shapeType = shape,
      gridDim = gridDim,
      targetArrows = arrowCount,
      seed = 2026L + id * 87654L
    )
  }

  fun generateShapedLevel(
    id: Int,
    name: String,
    tier: DifficultyTier,
    shapeType: BoardShapeType,
    gridDim: Int,
    targetArrows: Int,
    seed: Long
  ): PuzzleLevel {
    val random = Random(seed)
    val validCells = generateShapeMask(shapeType, gridDim)

    val placedArrows = mutableListOf<ArrowItem>()
    val occupied = mutableSetOf<Pair<Int, Int>>()

    var nextId = 1
    var attempts = 0
    val maxAttempts = 3200

    val availableCellsList = validCells.toMutableList()

    while (placedArrows.size < targetArrows && attempts < maxAttempts) {
      attempts++
      val candidateCells = availableCellsList.filter { !occupied.contains(it) }
      if (candidateCells.isEmpty()) break

      val head = candidateCells.random(random)
      val headX = head.first
      val headY = head.second

      // Check directions with clear exit path
      val validDirections = Direction.values().filter { dir ->
        var cx = headX + dir.dx
        var cy = headY + dir.dy
        var isClear = true
        while (cx in 0 until gridDim && cy in 0 until gridDim) {
          if (occupied.contains(cx to cy)) {
            isClear = false
            break
          }
          cx += dir.dx
          cy += dir.dy
        }
        isClear
      }

      if (validDirections.isNotEmpty()) {
        val chosenDir = validDirections.random(random)
        val pathPoints = mutableListOf<Pair<Int, Int>>()
        pathPoints.add(headX to headY)

        // 1 to 2 segment lengths
        val maxLen = if (random.nextFloat() > 0.5f) 2 else 1
        var curX = headX
        var curY = headY

        for (step in 1..maxLen) {
          val oppDir = when (chosenDir) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
          }

          val tailDirs = mutableListOf(oppDir)
          if (random.nextFloat() > 0.45f) {
            if (oppDir == Direction.DOWN || oppDir == Direction.UP) {
              tailDirs.add(Direction.LEFT)
              tailDirs.add(Direction.RIGHT)
            } else {
              tailDirs.add(Direction.UP)
              tailDirs.add(Direction.DOWN)
            }
          }

          val nextCandidate = tailDirs.filter { d ->
            val nx = curX + d.dx
            val ny = curY + d.dy
            validCells.contains(nx to ny) && !occupied.contains(nx to ny) && !pathPoints.contains(nx to ny)
          }

          if (nextCandidate.isNotEmpty()) {
            val d = nextCandidate.random(random)
            curX += d.dx
            curY += d.dy
            pathPoints.add(curX to curY)
          } else {
            break
          }
        }

        pathPoints.forEach { occupied.add(it) }

        placedArrows.add(
          ArrowItem(
            id = nextId++,
            x = headX,
            y = headY,
            direction = chosenDir,
            pathPoints = pathPoints
          )
        )
      }
    }

    if (placedArrows.size < (targetArrows * 0.4f).toInt()) {
      return buildDenseFallbackLevel(id, name, tier, shapeType, gridDim, seed)
    }

    return PuzzleLevel(
      id = id,
      name = name,
      tier = tier,
      gridWidth = gridDim,
      gridHeight = gridDim,
      arrows = placedArrows,
      targetMoves = placedArrows.size,
      shapeType = shapeType
    )
  }

  /**
   * Generates organic silhouette masks for boards.
   * Uses realistic geometric and mathematical equations:
   * HEART: Real cardioid parametric heart (x^2 + (y - cbrt(x^2))^2 <= r^2)
   * GLASSES: Dual rounded optical frames with connecting bridge and ear arms
   * TV: Monitor screen with angled antennae and base
   * FAN: Aerodynamic 4-blade hub sweep
   */
  fun generateShapeMask(shape: BoardShapeType, dim: Int): Set<Pair<Int, Int>> {
    val cells = mutableSetOf<Pair<Int, Int>>()
    val mid = dim / 2.0f

    for (c in 0 until dim) {
      for (r in 0 until dim) {
        val inShape = when (shape) {
          BoardShapeType.HEART -> {
            // Authentic heart curve:
            // Normalize: x in [-1.3, 1.3], y in [-1.2, 1.3] (with top at positive y)
            val nx = (c - mid + 0.5f) / (dim * 0.36f)
            val ny = (mid - r - 0.2f) / (dim * 0.36f)
            
            // Standard smooth cardioid heart equation: (x^2 + y^2 - 1)^3 - x^2 * y^3 <= 0
            val x2 = nx * nx
            val y2 = ny * ny
            val a = x2 + y2 - 1.0f
            val eq = a * a * a - x2 * (ny * ny * ny)
            eq <= 0.08f
          }

          BoardShapeType.GLASSES -> {
            // Spectacles: Two rounded rectangular/oval lens frames with a nose bridge
            val midInt = dim / 2
            val leftLens = (c in (midInt - 5)..(midInt - 1)) && (r in (midInt - 2)..(midInt + 2))
            val rightLens = (c in (midInt + 1)..(midInt + 5)) && (r in (midInt - 2)..(midInt + 2))
            val bridge = (c in (midInt - 2)..(midInt + 2)) && (r == midInt - 1)
            val leftArm = (c in (midInt - 5)..(midInt - 4)) && (r == midInt - 3)
            val rightArm = (c in (midInt + 4)..(midInt + 5)) && (r == midInt - 3)
            leftLens || rightLens || bridge || leftArm || rightArm
          }

          BoardShapeType.TV -> {
            val midInt = dim / 2
            val screen = (c in (midInt - 4)..(midInt + 4)) && (r in (midInt - 1)..(midInt + 3))
            val leftAntenna = (c == midInt - 2 && r == midInt - 2) || (c == midInt - 3 && r == midInt - 3) || (c == midInt - 4 && r == midInt - 4)
            val rightAntenna = (c == midInt + 2 && r == midInt - 2) || (c == midInt + 3 && r == midInt - 3) || (c == midInt + 4 && r == midInt - 4)
            val stand = (c in (midInt - 2)..(midInt + 2)) && (r == midInt + 4)
            screen || leftAntenna || rightAntenna || stand
          }

          BoardShapeType.FAN -> {
            val midInt = dim / 2
            val centerHub = abs(c - midInt) <= 1 && abs(r - midInt) <= 1
            val topBlade = (r in 1 until midInt) && (c in midInt..(midInt + 2))
            val bottomBlade = (r in (midInt + 1)..(dim - 2)) && (c in (midInt - 2)..midInt)
            val rightBlade = (c in (midInt + 1)..(dim - 2)) && (r in midInt..(midInt + 2))
            val leftBlade = (c in 1 until midInt) && (r in (midInt - 2)..midInt)
            centerHub || topBlade || bottomBlade || rightBlade || leftBlade
          }

          BoardShapeType.STAR -> {
            val midInt = dim / 2
            val dist = abs(c - midInt) + abs(r - midInt)
            dist <= midInt || c == midInt || r == midInt || (abs(c - midInt) == abs(r - midInt) && dist <= midInt + 2)
          }

          BoardShapeType.ROCKET -> {
            val midInt = dim / 2
            val nose = (r in 1..(midInt - 2)) && (abs(c - midInt) <= (r - 1))
            val fuselage = (r in (midInt - 2)..(midInt + 2)) && (abs(c - midInt) <= 2)
            val leftFin = (r in (midInt + 1)..(midInt + 3)) && (c in (midInt - 4)..(midInt - 2))
            val rightFin = (r in (midInt + 1)..(midInt + 3)) && (c in (midInt + 2)..(midInt + 4))
            val flame = (r in (midInt + 3)..(midInt + 4)) && (abs(c - midInt) <= 1)
            nose || fuselage || leftFin || rightFin || flame
          }

          BoardShapeType.BUTTERFLY -> {
            val midInt = dim / 2
            val dx = abs(c - midInt)
            val dy = abs(r - midInt)
            (dx in 1..midInt && dy <= dx + 1 && dy <= midInt - 1) || (dx == 0 && dy <= midInt - 2)
          }

          BoardShapeType.DIAMOND -> {
            val midInt = dim / 2
            abs(c - midInt) + abs(r - midInt) <= midInt
          }

          BoardShapeType.HOURGLASS -> {
            val midInt = dim / 2
            abs(c - midInt) >= abs(r - midInt) - 1
          }

          BoardShapeType.RING -> {
            val midInt = dim / 2
            val dist = abs(c - midInt) + abs(r - midInt)
            dist in 2..midInt
          }

          BoardShapeType.SHIELD -> {
            val midInt = dim / 2
            val halfW = midInt - 1
            val inTop = r <= midInt && abs(c - midInt) <= halfW
            val inBottom = r > midInt && abs(c - midInt) <= (dim - 1 - r)
            inTop || inBottom
          }

          BoardShapeType.PYRAMID -> {
            val midInt = dim / 2
            val rowFromTop = r - 1
            rowFromTop in 0 until dim - 2 && abs(c - midInt) <= rowFromTop
          }

          BoardShapeType.LABYRINTH -> {
            val midInt = dim / 2
            (c % 2 == 1 || r % 2 == 1) && abs(c - midInt) < midInt && abs(r - midInt) < midInt
          }
        }

        if (inShape) {
          cells.add(c to r)
        }
      }
    }

    if (cells.size < (dim * dim * 0.2f).toInt()) {
      val midInt = dim / 2
      for (c in 0 until dim) {
        for (r in 0 until dim) {
          if (abs(c - midInt) + abs(r - midInt) <= midInt) {
            cells.add(c to r)
          }
        }
      }
    }

    return cells
  }

  private fun buildDenseFallbackLevel(
    id: Int,
    name: String,
    tier: DifficultyTier,
    shape: BoardShapeType,
    dim: Int,
    seed: Long
  ): PuzzleLevel {
    val random = Random(seed)
    val validCells = generateShapeMask(shape, dim).toList()
    val arrows = mutableListOf<ArrowItem>()
    var nextId = 1

    val cellCount = (validCells.size * 0.72f).toInt().coerceAtLeast(8)
    val chosenCells = validCells.shuffled(random).take(cellCount)

    for (pt in chosenCells) {
      val dir = Direction.values().random(random)
      arrows.add(
        ArrowItem(
          id = nextId++,
          x = pt.first,
          y = pt.second,
          direction = dir,
          pathPoints = listOf(pt)
        )
      )
    }

    return PuzzleLevel(
      id = id,
      name = name,
      tier = tier,
      gridWidth = dim,
      gridHeight = dim,
      arrows = arrows,
      targetMoves = arrows.size,
      shapeType = shape
    )
  }
}
