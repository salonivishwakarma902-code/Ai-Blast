package com.example.engine

import com.example.model.ArrowItem
import com.example.model.Direction

object PuzzleSolver {

  /**
   * Returns true if [arrow] has an unobstructed escape path off the grid in its [direction].
   * An arrow escapes from its head (x, y) along (direction.dx, direction.dy) to the grid edge.
   * If any other active arrow occupies any cell in that escape ray, it is blocked.
   */
  fun isArrowFree(
    arrow: ArrowItem,
    allArrows: List<ArrowItem>,
    gridWidth: Int,
    gridHeight: Int
  ): Boolean {
    val activeOtherArrows = allArrows.filter { it.id != arrow.id && !it.isExiting }
    val occupiedByOthers = mutableSetOf<Pair<Int, Int>>()
    for (other in activeOtherArrows) {
      occupiedByOthers.addAll(other.occupiedCells)
    }

    var cx = arrow.x + arrow.direction.dx
    var cy = arrow.y + arrow.direction.dy

    while (cx in 0 until gridWidth && cy in 0 until gridHeight) {
      if (occupiedByOthers.contains(cx to cy)) {
        return false
      }
      cx += arrow.direction.dx
      cy += arrow.direction.dy
    }
    return true
  }

  fun getBlockingArrow(
    arrow: ArrowItem,
    allArrows: List<ArrowItem>,
    gridWidth: Int,
    gridHeight: Int
  ): ArrowItem? {
    val activeOtherArrows = allArrows.filter { it.id != arrow.id && !it.isExiting }
    var cx = arrow.x + arrow.direction.dx
    var cy = arrow.y + arrow.direction.dy

    while (cx in 0 until gridWidth && cy in 0 until gridHeight) {
      val blocker = activeOtherArrows.find { it.occupiedCells.contains(cx to cy) }
      if (blocker != null) {
        return blocker
      }
      cx += arrow.direction.dx
      cy += arrow.direction.dy
    }
    return null
  }

  /**
   * Returns all arrows that currently have an unobstructed path off the board.
   */
  fun findFreeArrows(
    allArrows: List<ArrowItem>,
    gridWidth: Int,
    gridHeight: Int
  ): List<ArrowItem> {
    val active = allArrows.filter { !it.isExiting }
    return active.filter { isArrowFree(it, active, gridWidth, gridHeight) }
  }

  /**
   * Solves the level from current state to verify solvability.
   * Returns ordered list of arrow IDs in solving sequence, or null if deadlocked.
   */
  fun solveSequence(
    initialArrows: List<ArrowItem>,
    gridWidth: Int,
    gridHeight: Int
  ): List<Int>? {
    val remaining = initialArrows.filter { !it.isExiting }.toMutableList()
    val sequence = mutableListOf<Int>()

    while (remaining.isNotEmpty()) {
      val free = remaining.firstOrNull { isArrowFree(it, remaining, gridWidth, gridHeight) }
      if (free == null) {
        return null // Deadlocked
      }
      sequence.add(free.id)
      remaining.remove(free)
    }
    return sequence
  }
}
