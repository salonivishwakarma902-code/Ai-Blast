package com.example.model

data class Achievement(
  val id: String,
  val title: String,
  val description: String,
  val iconEmoji: String,
  val targetCount: Int,
  val currentProgress: Int = 0,
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false,
  val coinReward: Int = 50
) {
  val progressFraction: Float
    get() = (currentProgress.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
}
