package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GamePreferences
import com.example.engine.LevelRepository
import com.example.engine.PuzzleSolver
import com.example.haptics.HapticManager
import com.example.model.Achievement
import com.example.model.ArrowItem
import com.example.model.DailyRewardDay
import com.example.model.GameTheme
import com.example.model.HintType
import com.example.model.LevelProgress
import com.example.model.PuzzleLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class Screen {
  HOME,
  LEVEL_SELECT,
  GAMEPLAY,
  ACHIEVEMENTS,
  THEMES
}

data class MoveAction(
  val arrow: ArrowItem,
  val movesBefore: Int,
  val mistakesBefore: Int
)

data class GameUiState(
  val currentScreen: Screen = Screen.HOME,
  val currentTheme: GameTheme = GameTheme.CLASSIC_WHITE,
  val unlockedThemeIds: Set<String> = setOf("classic_white"),
  val coins: Int = 100,
  val hearts: Int = 3,
  val secondsUntilNextHeart: Int = 0,
  val freeHints: Int = 1,
  val currentLevelId: Int = 1,
  val activeLevel: PuzzleLevel? = null,
  val activeArrows: List<ArrowItem> = emptyList(),
  val movesCount: Int = 0,
  val mistakesCount: Int = 0,
  val isLevelCompleted: Boolean = false,
  val isGameOver: Boolean = false,
  val winStars: Int = 3,
  val winCoinsEarned: Int = 15,
  val activeHintType: HintType? = null,
  val highlightedArrowId: Int? = null,
  val trajectoryArrow: ArrowItem? = null,
  val canUndo: Boolean = false,
  val showDailyRewardDialog: Boolean = false,
  val showSettingsDialog: Boolean = false,
  val showHowToPlayDialog: Boolean = false,
  val showOutOfHeartsDialog: Boolean = false,
  val showWinDialog: Boolean = false,
  val showShopDialog: Boolean = false,
  val dailyStreak: Int = 0,
  val isDailyRewardClaimable: Boolean = false,
  val dailyRewards: List<DailyRewardDay> = emptyList(),
  val achievements: List<Achievement> = emptyList(),
  val isDailyChallengeDoneToday: Boolean = false,
  val soundEnabled: Boolean = true,
  val musicEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
  val showRuler: Boolean = false,
  val showFeedbackDialog: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val prefs = GamePreferences(application)
  val soundManager = SoundManager()
  val hapticManager = HapticManager(application)

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  private val moveHistory = mutableListOf<MoveAction>()

  fun toggleRuler() {
    _uiState.update { it.copy(showRuler = !it.showRuler) }
    
  }

  init {
    loadSettings()
    loadTheme()
    refreshPlayerStats()
    refreshDailyRewards()
    refreshAchievements()
    startHeartTimer()
  }

  private fun loadSettings() {
    val sound = prefs.soundEnabled
    val music = prefs.musicEnabled
    val vib = prefs.vibrationEnabled
    soundManager.isSoundEnabled = sound
    hapticManager.isVibrationEnabled = vib

    _uiState.update {
      it.copy(
        soundEnabled = sound,
        musicEnabled = music,
        vibrationEnabled = vib
      )
    }
  }

  private fun loadTheme() {
    val savedThemeId = prefs.selectedThemeId
    val theme = GameTheme.ALL_THEMES.find { it.id == savedThemeId } ?: GameTheme.CLASSIC_WHITE
    val unlocked = prefs.getUnlockedThemes()
    _uiState.update {
      it.copy(
        currentTheme = theme,
        unlockedThemeIds = unlocked
      )
    }
  }

  fun selectTheme(theme: GameTheme) {
    if (prefs.getUnlockedThemes().contains(theme.id)) {
      prefs.selectedThemeId = theme.id
      _uiState.update { it.copy(currentTheme = theme) }
      soundManager.playTap()
    } else {
      // Buy theme
      if (prefs.coins >= theme.priceCoins) {
        prefs.coins -= theme.priceCoins
        prefs.unlockTheme(theme.id)
        prefs.selectedThemeId = theme.id
        soundManager.playCoin()
        hapticManager.success()
        _uiState.update {
          it.copy(
            currentTheme = theme,
            coins = prefs.coins,
            unlockedThemeIds = prefs.getUnlockedThemes()
          )
        }
      } else {
        soundManager.playBlocked()
        hapticManager.error()
      }
    }
  }

  fun refreshPlayerStats() {
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val lastClaim = prefs.lastDailyClaimDate
    val claimable = lastClaim != todayStr
    val isDailyDone = prefs.isDailyChallengeCompleted(todayStr)

    _uiState.update {
      it.copy(
        coins = prefs.coins,
        hearts = prefs.hearts,
        secondsUntilNextHeart = prefs.getSecondsUntilNextHeart(),
        freeHints = prefs.freeHints,
        currentLevelId = prefs.currentLevel,
        dailyStreak = prefs.dailyStreak,
        isDailyRewardClaimable = claimable,
        isDailyChallengeDoneToday = isDailyDone
      )
    }
  }

  private fun startHeartTimer() {
    viewModelScope.launch {
      while (true) {
        delay(1000)
        val h = prefs.hearts
        val secs = prefs.getSecondsUntilNextHeart()
        _uiState.update { it.copy(hearts = h, secondsUntilNextHeart = secs) }
      }
    }
  }

  fun navigateTo(screen: Screen) {
    soundManager.playTap()
    _uiState.update { it.copy(currentScreen = screen) }
    if (screen == Screen.HOME) {
      refreshPlayerStats()
      refreshAchievements()
    }
  }

  fun startLevel(levelId: Int) {
    if (prefs.hearts <= 0) {
      _uiState.update { it.copy(showOutOfHeartsDialog = true) }
      return
    }

    val level = LevelRepository.getLevel(levelId)
    moveHistory.clear()

    _uiState.update {
      it.copy(
        currentScreen = Screen.GAMEPLAY,
        activeLevel = level,
        activeArrows = level.arrows.map { a -> a.copy(isExiting = false, isBlockedShaking = false, isHighlighted = false) },
        movesCount = 0,
        mistakesCount = 0,
        isLevelCompleted = false,
        isGameOver = false,
        showWinDialog = false,
        showOutOfHeartsDialog = false,
        activeHintType = null,
        highlightedArrowId = null,
        trajectoryArrow = null,
        canUndo = false
      )
    }
    soundManager.playTap()
  }

  fun startDailyChallenge() {
    if (prefs.hearts <= 0) {
      _uiState.update { it.copy(showOutOfHeartsDialog = true) }
      return
    }

    val level = LevelRepository.getDailyLevel()
    moveHistory.clear()

    _uiState.update {
      it.copy(
        currentScreen = Screen.GAMEPLAY,
        activeLevel = level,
        activeArrows = level.arrows.map { a -> a.copy(isExiting = false, isBlockedShaking = false, isHighlighted = false) },
        movesCount = 0,
        mistakesCount = 0,
        isLevelCompleted = false,
        isGameOver = false,
        showWinDialog = false,
        showOutOfHeartsDialog = false,
        activeHintType = null,
        highlightedArrowId = null,
        trajectoryArrow = null,
        canUndo = false
      )
    }
    soundManager.playTap()
  }

  fun onArrowClicked(arrow: ArrowItem) {
    val level = _uiState.value.activeLevel ?: return
    if (_uiState.value.isLevelCompleted || _uiState.value.isGameOver) return
    if (arrow.isExiting) return

    val currentActive = _uiState.value.activeArrows.filter { !it.isExiting }
    val isFree = PuzzleSolver.isArrowFree(arrow, currentActive, level.gridWidth, level.gridHeight)

    if (isFree) {
      // SUCCESS: Arrow escapes!
      soundManager.playEscape()
      hapticManager.success()

      // Save move to undo history
      moveHistory.add(MoveAction(arrow, _uiState.value.movesCount, _uiState.value.mistakesCount))

      val newMoves = _uiState.value.movesCount + 1
      prefs.totalMovesMade += 1

      // Mark arrow as exiting
      _uiState.update { state ->
        val updatedList = state.activeArrows.map { a ->
          if (a.id == arrow.id) a.copy(isExiting = true, isHighlighted = false) else a
        }
        state.copy(
          activeArrows = updatedList,
          movesCount = newMoves,
          canUndo = true,
          highlightedArrowId = null,
          trajectoryArrow = null,
          activeHintType = null
        )
      }

      // Check for level completion
      checkWinCondition()
    } else {
      // BLOCKED: Collision!
      soundManager.playBlocked()
      hapticManager.error()

      val newMistakes = _uiState.value.mistakesCount + 1
      val currentHearts = prefs.hearts
      val newHearts = (currentHearts - 1).coerceAtLeast(0)
      prefs.hearts = newHearts

      if (newHearts <= 0) {
        soundManager.playHeartLost()
      }

      // Trigger shake animation for arrow
      viewModelScope.launch {
        _uiState.update { state ->
          val shakenList = state.activeArrows.map { a ->
            if (a.id == arrow.id) a.copy(isBlockedShaking = true) else a
          }
          state.copy(
            activeArrows = shakenList,
            mistakesCount = newMistakes,
            hearts = newHearts,
            secondsUntilNextHeart = prefs.getSecondsUntilNextHeart()
          )
        }
        delay(400)
        _uiState.update { state ->
          val resetList = state.activeArrows.map { a ->
            if (a.id == arrow.id) a.copy(isBlockedShaking = false) else a
          }
          state.copy(
            activeArrows = resetList,
            showOutOfHeartsDialog = (newHearts <= 0),
            isGameOver = (newHearts <= 0)
          )
        }
      }
    }
  }

  private fun checkWinCondition() {
    val remaining = _uiState.value.activeArrows.filter { !it.isExiting }
    if (remaining.isEmpty()) {
      // Level Cleared!
      viewModelScope.launch {
        delay(300)
        soundManager.playVictory()
        hapticManager.victory()

        val mistakes = _uiState.value.mistakesCount
        val stars = when {
          mistakes == 0 -> 3
          mistakes == 1 -> 2
          else -> 1
        }

        val level = _uiState.value.activeLevel!!
        val totalEarned = 5 // Each match won gives exactly 5 coins

        prefs.coins += totalEarned
        if (stars == 3) {
          prefs.totalPerfectLevels += 1
        }

        if (level.tier == com.example.model.DifficultyTier.DAILY) {
          val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
          prefs.markDailyChallengeCompleted(todayStr)
          prefs.coins += 25 // Daily bonus
        } else {
          // Normal level progress
          val existingProgress = prefs.getLevelProgress(level.id)
          val bestStars = maxOf(existingProgress.stars, stars)
          val bestMoves = if (existingProgress.bestMoves == 0) _uiState.value.movesCount else minOf(existingProgress.bestMoves, _uiState.value.movesCount)

          prefs.saveLevelProgress(
            LevelProgress(
              levelId = level.id,
              isUnlocked = true,
              isCompleted = true,
              stars = bestStars,
              bestMoves = bestMoves
            )
          )

          // Unlock next level
          if (level.id + 1 <= LevelRepository.getTotalLevels()) {
            val nextProg = prefs.getLevelProgress(level.id + 1)
            if (!nextProg.isUnlocked) {
              prefs.saveLevelProgress(nextProg.copy(isUnlocked = true))
              if (level.id + 1 > prefs.currentLevel) {
                prefs.currentLevel = level.id + 1
              }
            }
          }
        }

        refreshAchievements()

        _uiState.update {
          it.copy(
            isLevelCompleted = true,
            showWinDialog = true,
            winStars = stars,
            winCoinsEarned = totalEarned,
            coins = prefs.coins
          )
        }
      }
    }
  }

  fun undoMove() {
    if (moveHistory.isEmpty()) return
    val lastMove = moveHistory.removeAt(moveHistory.size - 1)

    // Restore arrow
    _uiState.update { state ->
      val restoredList = state.activeArrows.map { a ->
        if (a.id == lastMove.arrow.id) lastMove.arrow.copy(isExiting = false, isBlockedShaking = false, isHighlighted = false) else a
      }
      state.copy(
        activeArrows = restoredList,
        movesCount = lastMove.movesBefore,
        mistakesCount = lastMove.mistakesBefore,
        canUndo = moveHistory.isNotEmpty(),
        highlightedArrowId = null,
        trajectoryArrow = null,
        activeHintType = null
      )
    }
    soundManager.playTap()
  }

  fun restartCurrentLevel() {
    val level = _uiState.value.activeLevel ?: return
    startLevel(level.id)
  }

  fun nextLevel() {
    val current = _uiState.value.activeLevel?.id ?: 1
    val nextId = current + 1
    if (nextId <= LevelRepository.getTotalLevels()) {
      startLevel(nextId)
    } else {
      navigateTo(Screen.LEVEL_SELECT)
    }
  }

  fun requestHint(type: HintType) {
    val level = _uiState.value.activeLevel ?: return
    val active = _uiState.value.activeArrows.filter { !it.isExiting }
    val freeArrows = PuzzleSolver.findFreeArrows(active, level.gridWidth, level.gridHeight)

    if (freeArrows.isEmpty()) {
      soundManager.playBlocked()
      return
    }

    // Check cost or free hint
    if (prefs.freeHints > 0) {
      prefs.freeHints -= 1
    } else if (prefs.coins >= type.costCoins) {
      prefs.coins -= type.costCoins
    } else {
      soundManager.playBlocked()
      hapticManager.error()
      return
    }

    soundManager.playHint()
    hapticManager.tap()

    val targetArrow = freeArrows.first()

    when (type) {
      HintType.HIGHLIGHT -> {
        _uiState.update {
          it.copy(
            activeHintType = type,
            highlightedArrowId = targetArrow.id,
            trajectoryArrow = null,
            coins = prefs.coins,
            freeHints = prefs.freeHints
          )
        }
      }
      HintType.PATH -> {
        _uiState.update {
          it.copy(
            activeHintType = type,
            highlightedArrowId = targetArrow.id,
            trajectoryArrow = targetArrow,
            coins = prefs.coins,
            freeHints = prefs.freeHints
          )
        }
      }
      HintType.AUTO_MOVE -> {
        _uiState.update {
          it.copy(
            coins = prefs.coins,
            freeHints = prefs.freeHints
          )
        }
        onArrowClicked(targetArrow)
      }
    }
  }

  fun refillHeartsWithCoins() {
    val cost = 30
    if (prefs.coins >= cost) {
      prefs.coins -= cost
      prefs.hearts = 3
      soundManager.playCoin()
      hapticManager.success()
      _uiState.update {
        it.copy(
          coins = prefs.coins,
          hearts = 3,
          showOutOfHeartsDialog = false,
          isGameOver = false
        )
      }
    } else {
      soundManager.playBlocked()
      hapticManager.error()
    }
  }

  fun watchAdForHeart() {
    // Simulated rewarded ad gives full heart recovery!
    prefs.hearts = minOf(3, prefs.hearts + 1)
    soundManager.playCoin()
    hapticManager.success()
    _uiState.update {
      it.copy(
        hearts = prefs.hearts,
        showOutOfHeartsDialog = false,
        isGameOver = false
      )
    }
  }

  fun watchAdForCoins() {
    prefs.coins += 50
    soundManager.playCoin()
    hapticManager.success()
    _uiState.update { it.copy(coins = prefs.coins) }
  }

  fun buyCoins(amount: Int) {
    prefs.coins += amount
    soundManager.playCoin()
    hapticManager.success()
    _uiState.update { it.copy(coins = prefs.coins) }
  }

  fun claimDailyReward(day: DailyRewardDay) {
    if (!day.isToday || day.isClaimed) return

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    prefs.lastDailyClaimDate = todayStr
    prefs.dailyStreak = prefs.dailyStreak + 1
    prefs.coins += day.coins
    if (day.freeHint) {
      prefs.freeHints += 1
    }

    soundManager.playCoin()
    hapticManager.success()
    refreshPlayerStats()
    refreshDailyRewards()
    refreshAchievements()
  }

  private fun refreshDailyRewards() {
    val streak = prefs.dailyStreak
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val lastClaim = prefs.lastDailyClaimDate
    val isClaimedToday = lastClaim == todayStr

    val currentDayCycle = (streak % 7) + 1
    val rewardsList = mutableListOf<DailyRewardDay>()

    val coinAmounts = listOf(20, 30, 40, 50, 60, 80, 150)
    for (i in 1..7) {
      val isClaimed = if (isClaimedToday) i <= currentDayCycle else i < currentDayCycle
      val isToday = if (isClaimedToday) i == currentDayCycle else i == currentDayCycle
      rewardsList.add(
        DailyRewardDay(
          dayNumber = i,
          coins = coinAmounts[i - 1],
          freeHint = (i == 4 || i == 7),
          isClaimed = isClaimed,
          isToday = isToday
        )
      )
    }

    _uiState.update {
      it.copy(
        dailyRewards = rewardsList,
        isDailyRewardClaimable = !isClaimedToday
      )
    }
  }

  fun refreshAchievements() {
    val completedSet = prefs.getCompletedAchievements().toMutableSet()
    val claimedSet = prefs.getClaimedAchievements()

    val currentLvl = prefs.currentLevel
    val perfects = prefs.totalPerfectLevels
    val streak = prefs.dailyStreak
    val totalMoves = prefs.totalMovesMade

    val list = listOf(
      Achievement("first_clear", "First Escape", "Complete your first level", "🎯", 1, currentLvl - 1, currentLvl > 1, claimedSet.contains("first_clear"), 50),
      Achievement("clear_10", "Puzzle Apprentice", "Clear 10 levels", "🧩", 10, (currentLvl - 1).coerceAtMost(10), currentLvl > 10, claimedSet.contains("clear_10"), 60),
      Achievement("clear_25", "Escape Specialist", "Clear 25 levels", "🚀", 25, (currentLvl - 1).coerceAtMost(25), currentLvl > 25, claimedSet.contains("clear_25"), 80),
      Achievement("clear_50", "Maze Master", "Clear 50 levels", "👑", 50, (currentLvl - 1).coerceAtMost(50), currentLvl > 50, claimedSet.contains("clear_50"), 100),
      Achievement("clear_100", "Grand Master", "Clear all 100 levels", "🏆", 100, (currentLvl - 1).coerceAtMost(100), currentLvl > 100, claimedSet.contains("clear_100"), 250),
      Achievement("perfect_5", "Precision Thinker", "Get 3 stars on 5 levels", "⭐", 5, perfects.coerceAtMost(5), perfects >= 5, claimedSet.contains("perfect_5"), 75),
      Achievement("perfect_20", "Flawless Mind", "Get 3 stars on 20 levels", "🌟", 20, perfects.coerceAtMost(20), perfects >= 20, claimedSet.contains("perfect_20"), 150),
      Achievement("streak_3", "Committed Solver", "Reach a 3-day login streak", "🔥", 3, streak.coerceAtMost(3), streak >= 3, claimedSet.contains("streak_3"), 50),
      Achievement("streak_7", "Weekly Champion", "Complete a full 7-day streak", "⚡", 7, streak.coerceAtMost(7), streak >= 7, claimedSet.contains("streak_7"), 120),
      Achievement("moves_100", "Arrow Navigator", "Make 100 successful arrow moves", "🏹", 100, totalMoves.coerceAtMost(100), totalMoves >= 100, claimedSet.contains("moves_100"), 50),
      Achievement("moves_500", "Master Strategist", "Make 500 arrow moves", "🔮", 500, totalMoves.coerceAtMost(500), totalMoves >= 500, claimedSet.contains("moves_500"), 150)
    )

    _uiState.update { it.copy(achievements = list) }
  }

  fun claimAchievementReward(achievement: Achievement) {
    if (!achievement.isCompleted || achievement.isClaimed) return

    prefs.markAchievementClaimed(achievement.id)
    prefs.coins += achievement.coinReward
    soundManager.playCoin()
    hapticManager.success()

    refreshPlayerStats()
    refreshAchievements()
  }

  fun toggleSound() {
    val nv = !prefs.soundEnabled
    prefs.soundEnabled = nv
    soundManager.isSoundEnabled = nv
    _uiState.update { it.copy(soundEnabled = nv) }
  }

  fun toggleMusic() {
    val nv = !prefs.musicEnabled
    prefs.musicEnabled = nv
    _uiState.update { it.copy(musicEnabled = nv) }
  }

  fun toggleVibration() {
    val nv = !prefs.vibrationEnabled
    prefs.vibrationEnabled = nv
    hapticManager.isVibrationEnabled = nv
    _uiState.update { it.copy(vibrationEnabled = nv) }
  }

  fun setDialogVisibility(
    dailyReward: Boolean? = null,
    settings: Boolean? = null,
    howToPlay: Boolean? = null,
    outOfHearts: Boolean? = null,
    win: Boolean? = null,
    shop: Boolean? = null,
    feedback: Boolean? = null
  ) {
    if (feedback == true || settings == true || shop == true || dailyReward == true) {
      soundManager.playTap()
    }
    _uiState.update {
      it.copy(
        showDailyRewardDialog = dailyReward ?: it.showDailyRewardDialog,
        showSettingsDialog = settings ?: it.showSettingsDialog,
        showHowToPlayDialog = howToPlay ?: it.showHowToPlayDialog,
        showOutOfHeartsDialog = outOfHearts ?: it.showOutOfHeartsDialog,
        showWinDialog = win ?: it.showWinDialog,
        showShopDialog = shop ?: it.showShopDialog,
        showFeedbackDialog = feedback ?: it.showFeedbackDialog
      )
    }
  }

  fun submitFeedback(rating: Int, category: String, comment: String) {
    prefs.saveFeedback(rating, category, comment)
    soundManager.playFeedbackSuccess()
    hapticManager.victory()
  }

  fun onZoomTick() {
    soundManager.playZoom()
    hapticManager.zoomTick()
  }

  fun skipLevel() {
    val cost = 100
    if (prefs.coins >= cost) {
      prefs.coins -= cost
      soundManager.playCoin()
      
      val level = _uiState.value.activeLevel ?: return
      if (level.tier != com.example.model.DifficultyTier.DAILY) {
        val existingProgress = prefs.getLevelProgress(level.id)
        prefs.saveLevelProgress(
          existingProgress.copy(
            isUnlocked = true,
            isCompleted = true,
            stars = 0 // Skipped, so maybe 0 stars
          )
        )
        // Unlock next level
        if (level.id + 1 <= com.example.engine.LevelRepository.getTotalLevels()) {
          val nextProg = prefs.getLevelProgress(level.id + 1)
          if (!nextProg.isUnlocked) {
            prefs.saveLevelProgress(nextProg.copy(isUnlocked = true))
            if (level.id + 1 > prefs.currentLevel) {
              prefs.currentLevel = level.id + 1
            }
          }
        }
      }
      
      _uiState.update { it.copy(isLevelCompleted = true) }
      // Immediately go to the next level
      nextLevel()
    } else {
      soundManager.playBlocked()
    }
  }

  fun buyHearts() {
    val cost = 30
    if (prefs.coins >= cost) {
      prefs.coins -= cost
      prefs.hearts = 5 // Max hearts
      soundManager.playCoin()
      refreshPlayerStats()
    } else {
      soundManager.playBlocked()
    }
  }

  fun resetAllData() {
    prefs.resetAllData()
    loadSettings()
    loadTheme()
    refreshPlayerStats()
    refreshDailyRewards()
    refreshAchievements()
    _uiState.update {
      it.copy(
        currentScreen = Screen.HOME,
        showSettingsDialog = false
      )
    }
  }
}
