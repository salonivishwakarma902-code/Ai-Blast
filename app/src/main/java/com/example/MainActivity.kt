package com.example

// Force emulator refresh again
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GameViewModel
import com.example.ui.Screen
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.OutOfHeartsDialog
import com.example.ui.components.ShopDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: GameViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsState()

      MyApplicationTheme(darkTheme = uiState.currentTheme.isDark) {
        Surface(modifier = Modifier.fillMaxSize()) {
          // Android Back gesture handler (Triggers UI refresh)
          BackHandler(enabled = uiState.currentScreen != Screen.HOME) {
            viewModel.navigateTo(Screen.HOME)
          }

          when (uiState.currentScreen) {
            Screen.HOME -> {
              HomeScreen(
                uiState = uiState,
                onPlayClick = { viewModel.startLevel(uiState.currentLevelId) },
                onLevelSelectClick = { viewModel.navigateTo(Screen.LEVEL_SELECT) },
                onDailyChallengeClick = { viewModel.startDailyChallenge() },
                onDailyRewardClick = { viewModel.setDialogVisibility(dailyReward = true) },
                onAchievementsClick = { viewModel.navigateTo(Screen.ACHIEVEMENTS) },
                onThemesClick = { viewModel.navigateTo(Screen.THEMES) },
                onSettingsClick = { viewModel.setDialogVisibility(settings = true) },
                onHowToPlayClick = { viewModel.setDialogVisibility(howToPlay = true) },
                onHeartsClick = { viewModel.setDialogVisibility(outOfHearts = true) },
                onCoinsClick = { viewModel.setDialogVisibility(shop = true) },
                onFeedbackClick = { viewModel.setDialogVisibility(feedback = true) }
              )
            }

            Screen.GAMEPLAY -> {
              GameplayScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateTo(Screen.HOME) },
                onArrowClick = { arrow -> viewModel.onArrowClicked(arrow) },
                onUndoClick = { viewModel.undoMove() },
                onRestartClick = { viewModel.restartCurrentLevel() },
                onHintSelect = { hintType -> viewModel.requestHint(hintType) },
                onNextLevel = { viewModel.nextLevel() },
                onLevelSelectClick = { viewModel.navigateTo(Screen.LEVEL_SELECT) },
                onRefillHearts = { viewModel.refillHeartsWithCoins() },
                onSkipLevelClick = { viewModel.skipLevel() },
                onToggleRuler = { viewModel.toggleRuler() },
                onWatchAdForHeart = { viewModel.watchAdForHeart() },
                onWatchAdForCoins = { viewModel.setDialogVisibility(shop = true) },
                onToggleSound = { viewModel.toggleSound() },
                onToggleMusic = { viewModel.toggleMusic() },
                onToggleVibration = { viewModel.toggleVibration() },
                onThemesClick = { viewModel.navigateTo(Screen.THEMES) },
                onResetData = { viewModel.resetAllData() },
                onCloseSettings = { viewModel.setDialogVisibility(settings = false) },
                onOpenSettings = { viewModel.setDialogVisibility(settings = true) },
                onCloseHowToPlay = { viewModel.setDialogVisibility(howToPlay = false) },
                onOpenHowToPlay = { viewModel.setDialogVisibility(howToPlay = true) },
                onFeedbackClick = { viewModel.setDialogVisibility(feedback = true) },
                onZoomTick = { viewModel.onZoomTick() },
                onSubmitFeedback = { r, cat, com -> viewModel.submitFeedback(r, cat, com) },
                onCloseFeedback = { viewModel.setDialogVisibility(feedback = false) }
              )
            }

            Screen.LEVEL_SELECT -> {
              LevelSelectScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateTo(Screen.HOME) },
                onLevelClick = { levelId -> viewModel.startLevel(levelId) },
                onDailyChallengeClick = { viewModel.startDailyChallenge() },
                onThemesClick = { viewModel.navigateTo(Screen.THEMES) },
                onSettingsClick = { viewModel.setDialogVisibility(settings = true) }
              )
            }

            Screen.ACHIEVEMENTS -> {
              AchievementsScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateTo(Screen.HOME) },
                onClaimAchievement = { ach -> viewModel.claimAchievementReward(ach) },
                onThemesClick = { viewModel.navigateTo(Screen.THEMES) },
                onSettingsClick = { viewModel.setDialogVisibility(settings = true) }
              )
            }

            
            Screen.THEMES -> {
              ThemesScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateTo(Screen.HOME) },
                onSelectTheme = { th -> viewModel.selectTheme(th) },
                onWatchAdForCoins = { viewModel.setDialogVisibility(shop = true) },
                onSettingsClick = { viewModel.setDialogVisibility(settings = true) }
              )
            }
          }

          // Global Dialogs accessible from Home / LevelSelect
          if (uiState.showShopDialog) {
            ShopDialog(
              theme = uiState.currentTheme,
              userCoins = uiState.coins,
              onClose = { viewModel.setDialogVisibility(shop = false) },
              onBuyCoins = { amount -> 
                viewModel.buyCoins(amount)
                viewModel.setDialogVisibility(shop = false) 
              }
            )
          }

          if (uiState.showDailyRewardDialog) {
            DailyRewardDialog(
              theme = uiState.currentTheme,
              streak = uiState.dailyStreak,
              rewards = uiState.dailyRewards,
              onClaim = { day -> viewModel.claimDailyReward(day) },
              onClose = { viewModel.setDialogVisibility(dailyReward = false) }
            )
          }

          if (uiState.showSettingsDialog && uiState.currentScreen != Screen.GAMEPLAY) {
            SettingsDialog(
              theme = uiState.currentTheme,
              soundEnabled = uiState.soundEnabled,
              musicEnabled = uiState.musicEnabled,
              vibrationEnabled = uiState.vibrationEnabled,
              onToggleSound = { viewModel.toggleSound() },
              onToggleMusic = { viewModel.toggleMusic() },
              onToggleVibration = { viewModel.toggleVibration() },
              onThemesClick = {
                viewModel.setDialogVisibility(settings = false)
                viewModel.navigateTo(Screen.THEMES)
              },
              onHowToPlayClick = {
                viewModel.setDialogVisibility(settings = false, howToPlay = true)
              },
              onFeedbackClick = {
                viewModel.setDialogVisibility(settings = false, feedback = true)
              },
              onResetData = { viewModel.resetAllData() },
              onClose = { viewModel.setDialogVisibility(settings = false) }
            )
          }

          if (uiState.showFeedbackDialog && uiState.currentScreen != Screen.GAMEPLAY) {
            FeedbackDialog(
              theme = uiState.currentTheme,
              onDismiss = { viewModel.setDialogVisibility(feedback = false) },
              onSubmit = { rating, cat, comment -> viewModel.submitFeedback(rating, cat, comment) }
            )
          }

          if (uiState.showHowToPlayDialog && uiState.currentScreen != Screen.GAMEPLAY) {
            HowToPlayDialog(
              theme = uiState.currentTheme,
              onClose = { viewModel.setDialogVisibility(howToPlay = false) }
            )
          }
        }
      }
    }
  }
}

