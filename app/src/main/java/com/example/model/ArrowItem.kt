package com.example.model

data class ArrowItem(
  val id: Int,
  val x: Int, // Head X coordinate
  val y: Int, // Head Y coordinate
  val direction: Direction, // Direction arrow points at head
  val pathPoints: List<Pair<Int, Int>> = listOf(x to y), // Polyline from tail to head
  val colorIdx: Int = 0,
  val isExiting: Boolean = false,
  val isBlockedShaking: Boolean = false,
  val isHighlighted: Boolean = false
) {
  val occupiedCells: Set<Pair<Int, Int>>
    get() = if (pathPoints.isEmpty()) setOf(x to y) else pathPoints.toSet()
}
