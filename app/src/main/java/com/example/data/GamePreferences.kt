package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LevelProgress
import org.json.JSONObject

class GamePreferences(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("arrow_escape_game_prefs", Context.MODE_PRIVATE)

  var currentLevel: Int
    get() = prefs.getInt(KEY_CURRENT_LEVEL, 1)
    set(value) = prefs.edit().putInt(KEY_CURRENT_LEVEL, value).apply()

  var coins: Int
    get() = prefs.getInt(KEY_COINS, 100) // Starting balance 100 coins
    set(value) = prefs.edit().putInt(KEY_COINS, value.coerceAtLeast(0)).apply()

  var hearts: Int
    get() {
      checkHeartRegeneration()
      return prefs.getInt(KEY_HEARTS, 3)
    }
    set(value) {
      prefs.edit().putInt(KEY_HEARTS, value.coerceIn(0, 3)).apply()
      if (value < 3 && lastHeartRefillTime == 0L) {
        lastHeartRefillTime = System.currentTimeMillis()
      }
    }

  var lastHeartRefillTime: Long
    get() = prefs.getLong(KEY_LAST_HEART_REFILL, 0L)
    set(value) = prefs.edit().putLong(KEY_LAST_HEART_REFILL, value).apply()

  var freeHints: Int
    get() = prefs.getInt(KEY_FREE_HINTS, 1)
    set(value) = prefs.edit().putInt(KEY_FREE_HINTS, value.coerceAtLeast(0)).apply()

  var dailyStreak: Int
    get() = prefs.getInt(KEY_DAILY_STREAK, 0)
    set(value) = prefs.edit().putInt(KEY_DAILY_STREAK, value).apply()

  var lastDailyClaimDate: String
    get() = prefs.getString(KEY_LAST_DAILY_CLAIM, "") ?: ""
    set(value) = prefs.edit().putString(KEY_LAST_DAILY_CLAIM, value).apply()

  var soundEnabled: Boolean
    get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

  var musicEnabled: Boolean
    get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

  var vibrationEnabled: Boolean
    get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

  var selectedThemeId: String
    get() = prefs.getString(KEY_SELECTED_THEME, "classic_white") ?: "classic_white"
    set(value) = prefs.edit().putString(KEY_SELECTED_THEME, value).apply()

  var totalMovesMade: Int
    get() = prefs.getInt(KEY_TOTAL_MOVES, 0)
    set(value) = prefs.edit().putInt(KEY_TOTAL_MOVES, value).apply()

  var totalPerfectLevels: Int
    get() = prefs.getInt(KEY_PERFECT_LEVELS, 0)
    set(value) = prefs.edit().putInt(KEY_PERFECT_LEVELS, value).apply()

  fun getUnlockedThemes(): Set<String> {
    return prefs.getStringSet(KEY_UNLOCKED_THEMES, setOf("classic_white")) ?: setOf("classic_white")
  }

  fun unlockTheme(themeId: String) {
    val current = getUnlockedThemes().toMutableSet()
    current.add(themeId)
    prefs.edit().putStringSet(KEY_UNLOCKED_THEMES, current).apply()
  }

  fun getLevelProgress(levelId: Int): LevelProgress {
    val jsonString = prefs.getString(KEY_LEVEL_PREFIX + levelId, null)
    if (jsonString != null) {
      try {
        val json = JSONObject(jsonString)
        return LevelProgress(
          levelId = levelId,
          isUnlocked = json.optBoolean("unlocked", levelId == 1),
          isCompleted = json.optBoolean("completed", false),
          stars = json.optInt("stars", 0),
          bestMoves = json.optInt("bestMoves", 0)
        )
      } catch (_: Exception) {}
    }
    return LevelProgress(
      levelId = levelId,
      isUnlocked = levelId <= currentLevel,
      isCompleted = false,
      stars = 0,
      bestMoves = 0
    )
  }

  fun saveLevelProgress(progress: LevelProgress) {
    val json = JSONObject().apply {
      put("unlocked", progress.isUnlocked)
      put("completed", progress.isCompleted)
      put("stars", progress.stars)
      put("bestMoves", progress.bestMoves)
    }
    prefs.edit().putString(KEY_LEVEL_PREFIX + progress.levelId, json.toString()).apply()
  }

  fun getCompletedAchievements(): Set<String> {
    return prefs.getStringSet(KEY_COMPLETED_ACHIEVEMENTS, emptySet()) ?: emptySet()
  }

  fun markAchievementCompleted(achievementId: String) {
    val set = getCompletedAchievements().toMutableSet()
    set.add(achievementId)
    prefs.edit().putStringSet(KEY_COMPLETED_ACHIEVEMENTS, set).apply()
  }

  fun getClaimedAchievements(): Set<String> {
    return prefs.getStringSet(KEY_CLAIMED_ACHIEVEMENTS, emptySet()) ?: emptySet()
  }

  fun markAchievementClaimed(achievementId: String) {
    val set = getClaimedAchievements().toMutableSet()
    set.add(achievementId)
    prefs.edit().putStringSet(KEY_CLAIMED_ACHIEVEMENTS, set).apply()
  }

  fun isDailyChallengeCompleted(dateStr: String): Boolean {
    val completed = prefs.getStringSet(KEY_COMPLETED_DAILY_CHALLENGES, emptySet()) ?: emptySet()
    return completed.contains(dateStr)
  }

  fun markDailyChallengeCompleted(dateStr: String) {
    val set = (prefs.getStringSet(KEY_COMPLETED_DAILY_CHALLENGES, emptySet()) ?: emptySet()).toMutableSet()
    set.add(dateStr)
    prefs.edit().putStringSet(KEY_COMPLETED_DAILY_CHALLENGES, set).apply()
  }

  fun resetAllProgress() {
    prefs.edit().clear().apply()
  }

  fun resetAllData() {
    prefs.edit().clear().apply()
  }

  private fun checkHeartRegeneration() {
    val currentHearts = prefs.getInt(KEY_HEARTS, 3)
    if (currentHearts >= 3) {
      lastHeartRefillTime = 0L
      return
    }

    val lastRefill = lastHeartRefillTime
    if (lastRefill == 0L) return

    val now = System.currentTimeMillis()
    val refillIntervalMs = 15 * 60 * 1000L // 15 minutes per heart
    val elapsed = now - lastRefill
    val heartsToAdd = (elapsed / refillIntervalMs).toInt()

    if (heartsToAdd > 0) {
      val newHearts = (currentHearts + heartsToAdd).coerceAtMost(3)
      prefs.edit().putInt(KEY_HEARTS, newHearts).apply()
      if (newHearts < 3) {
        lastHeartRefillTime = lastRefill + (heartsToAdd * refillIntervalMs)
      } else {
        lastHeartRefillTime = 0L
      }
    }
  }

  fun getSecondsUntilNextHeart(): Int {
    val currentHearts = prefs.getInt(KEY_HEARTS, 3)
    if (currentHearts >= 3) return 0
    val lastRefill = lastHeartRefillTime
    if (lastRefill == 0L) return 0
    val now = System.currentTimeMillis()
    val refillIntervalMs = 15 * 60 * 1000L
    val elapsed = now - lastRefill
    val remainingMs = (refillIntervalMs - (elapsed % refillIntervalMs)).coerceAtLeast(0)
    return (remainingMs / 1000).toInt()
  }

  companion object {
    private const val KEY_CURRENT_LEVEL = "current_level"
    private const val KEY_COINS = "coins"
    private const val KEY_HEARTS = "hearts"
    private const val KEY_LAST_HEART_REFILL = "last_heart_refill"
    private const val KEY_FREE_HINTS = "free_hints"
    private const val KEY_DAILY_STREAK = "daily_streak"
    private const val KEY_LAST_DAILY_CLAIM = "last_daily_claim"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    private const val KEY_SELECTED_THEME = "selected_theme"
    private const val KEY_TOTAL_MOVES = "total_moves"
    private const val KEY_PERFECT_LEVELS = "perfect_levels"
    private const val KEY_UNLOCKED_THEMES = "unlocked_themes"
    private const val KEY_LEVEL_PREFIX = "level_prog_"
    private const val KEY_COMPLETED_ACHIEVEMENTS = "completed_achievements"
    private const val KEY_CLAIMED_ACHIEVEMENTS = "claimed_achievements"
    private const val KEY_COMPLETED_DAILY_CHALLENGES = "completed_daily_challenges"
    private const val KEY_SAVED_FEEDBACK = "saved_feedback_list"
  }

  fun saveFeedback(rating: Int, category: String, comment: String) {
    val existing = prefs.getString(KEY_SAVED_FEEDBACK, "[]") ?: "[]"
    try {
      val array = org.json.JSONArray(existing)
      val item = org.json.JSONObject().apply {
        put("rating", rating)
        put("category", category)
        put("comment", comment)
        put("timestamp", System.currentTimeMillis())
      }
      array.put(item)
      prefs.edit().putString(KEY_SAVED_FEEDBACK, array.toString()).apply()
    } catch (_: Exception) {}
  }
}
