package com.example.model

data class DailyRewardDay(
  val dayNumber: Int,
  val coins: Int,
  val freeHint: Boolean = false,
  val isClaimed: Boolean = false,
  val isToday: Boolean = false
)
