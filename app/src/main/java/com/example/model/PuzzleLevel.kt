package com.example.model

import com.example.engine.BoardShapeType

enum class DifficultyTier(val title: String, val badgeColor: Long) {
  EASY("Easy", 0xFF0284C7),
  NORMAL("Normal", 0xFF6366F1),
  HARD("Hard", 0xFFF59E0B),
  HARDER("Harder", 0xFFEA580C),
  IMPOSSIBLE("Impossible", 0xFFDC2626),
  DAILY("Daily", 0xFFEC4899)
}

data class PuzzleLevel(
  val id: Int,
  val name: String,
  val tier: DifficultyTier,
  val gridWidth: Int,
  val gridHeight: Int,
  val arrows: List<ArrowItem>,
  val targetMoves: Int = arrows.size,
  val shapeType: BoardShapeType = BoardShapeType.LABYRINTH
)

data class LevelProgress(
  val levelId: Int,
  val isUnlocked: Boolean = false,
  val isCompleted: Boolean = false,
  val stars: Int = 0,
  val bestMoves: Int = 0
)
