package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowItem
import com.example.model.DifficultyTier
import com.example.model.HintType
import com.example.ui.GameUiState
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.HintOptionsDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.OutOfHeartsDialog
import com.example.ui.components.PuzzleBoard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.WinDialog

@Composable
fun GameplayScreen(
  uiState: GameUiState,
  onBackClick: () -> Unit,
  onArrowClick: (ArrowItem) -> Unit,
  onUndoClick: () -> Unit,
  onRestartClick: () -> Unit,
  onHintSelect: (HintType) -> Unit,
  onNextLevel: () -> Unit,
  onLevelSelectClick: () -> Unit,
  onRefillHearts: () -> Unit,
  onSkipLevelClick: () -> Unit,
  onWatchAdForHeart: () -> Unit,
  onWatchAdForCoins: () -> Unit,
  onToggleRuler: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleMusic: () -> Unit,
  onToggleVibration: () -> Unit,
  onThemesClick: () -> Unit,
  onResetData: () -> Unit,
  onCloseSettings: () -> Unit,
  onOpenSettings: () -> Unit,
  onCloseHowToPlay: () -> Unit,
  onOpenHowToPlay: () -> Unit,
  onFeedbackClick: () -> Unit,
  onZoomTick: () -> Unit,
  onSubmitFeedback: (rating: Int, category: String, comment: String) -> Unit,
  onCloseFeedback: () -> Unit
) {
  val theme = uiState.currentTheme
  val level = uiState.activeLevel ?: return
  var showHintSheet by remember { mutableStateOf(false) }

  val activeArrowsCount = uiState.activeArrows.count { !it.isExiting }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(theme.backgroundGradientStart, theme.backgroundGradientEnd)
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Navigation Bar (< on Left, ⚙ on Right)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Back chevron (<) in cyan/blue
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .testTag("gameplay_back_button")
            .size(44.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF0284C7),
            modifier = Modifier.size(26.dp)
          )
        }
        
        // Coins Pill
        Surface(
          shape = CircleShape,
          color = Color(0xFFFDE047).copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308)),
          modifier = Modifier.clickable { onWatchAdForCoins() } // Reusing this for ShopDialog trigger
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Star, // Coins Icon
              contentDescription = "Coins",
              tint = Color(0xFFEAB308),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${uiState.coins}",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }
        }

        // Settings gear (⚙) in cyan/blue
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier
            .testTag("gameplay_settings_button")
            .size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color(0xFF0284C7),
            modifier = Modifier.size(26.dp)
          )
        }
      }

      // 2. Big Title & Subtitle + Floating Pill Card (Exact match to Screenshots 1-4)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        // Title (e.g. "Tap Away Arrows", "Find the Free Path", "Clear the Board")
        Text(
          text = level.name,
          fontSize = 28.sp,
          fontWeight = FontWeight.Black,
          color = theme.textPrimary,
          letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Subtitle (e.g. "Level 1 • Heart • Easy")
        Text(
          text = "Level ${level.id} • ${level.shapeType.displayName}",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = theme.textSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Floating Pill: [ ↗ count ]  [ ❤️ ❤️ ❤️ ]  [ Difficulty ]
        val infiniteTransition = rememberInfiniteTransition()
        val pillFloatOffset by infiniteTransition.animateFloat(
          initialValue = -3f,
          targetValue = 3f,
          animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
          )
        )
        
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = theme.cardBackground.copy(alpha = 0.95f),
          border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
          shadowElevation = 8.dp,
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .offset(y = pillFloatOffset.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Arrow icon + active count
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NorthEast,
                contentDescription = "Arrows Remaining",
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$activeArrowsCount",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
              )
            }

            // 3 Hearts
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              for (h in 1..3) {
                if (h <= uiState.hearts) {
                  Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart $h",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Lost Heart $h",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }

            // Difficulty Tag Badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(level.tier.badgeColor).copy(alpha = 0.12f)
            ) {
              Text(
                text = level.tier.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(level.tier.badgeColor),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // 3. Central Puzzle Board with Zoom, Pan and Shape Silhouette
      PuzzleBoard(
        theme = theme,
        gridWidth = level.gridWidth,
        gridHeight = level.gridHeight,
        arrows = uiState.activeArrows,
        highlightedArrowId = uiState.highlightedArrowId,
        trajectoryArrow = uiState.trajectoryArrow,
        showRuler = uiState.showRuler,
        shapeType = level.shapeType,
        onArrowClick = onArrowClick,
        onZoomChange = onZoomTick
      )

      // 4. Bottom Controls: Bulb and Ruler
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 32.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Hint Button with badge count
        Box(contentAlignment = Alignment.TopEnd) {
          Surface(
            shape = CircleShape,
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0284C7)),
            shadowElevation = 8.dp,
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .clickable {
                if (uiState.freeHints > 0 || uiState.coins >= 50) {
                  onHintSelect(HintType.PATH)
                } else {
                  showHintSheet = true
                }
              }
              .testTag("gameplay_hint_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Hint",
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(30.dp)
              )
            }
          }

          // Badge on Hint bulb: Free count or "50"
          Surface(
            shape = CircleShape,
            color = Color(0xFF0284C7),
            shadowElevation = 3.dp,
            modifier = Modifier
              .offset(x = 4.dp, y = (-2).dp)
              .size(22.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = if (uiState.freeHints > 0) "${uiState.freeHints}" else "50",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Ruler Button
        Surface(
          shape = CircleShape,
          color = if (uiState.showRuler) Color(0xFF0284C7) else theme.cardBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
          shadowElevation = 6.dp,
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .clickable { onToggleRuler() }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Straighten,
              contentDescription = "Ruler",
              tint = if (uiState.showRuler) Color.White else Color(0xFF0284C7),
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    // Hint Dialog
    if (showHintSheet) {
      HintOptionsDialog(
        theme = theme,
        userCoins = uiState.coins,
        freeHints = uiState.freeHints,
        onSelectHint = { hintType ->
          onHintSelect(hintType)
          showHintSheet = false
        },
        onClose = { showHintSheet = false }
      )
    }

    // Win Dialog
    if (uiState.showWinDialog) {
      WinDialog(
        theme = theme,
        level = level,
        onNextLevel = onNextLevel,
        onMain = onLevelSelectClick
      )
    }

    // Out of Hearts Dialog
    if (uiState.showOutOfHeartsDialog) {
      OutOfHeartsDialog(
        theme = theme,
        secondsUntilNextHeart = uiState.secondsUntilNextHeart,
        userCoins = uiState.coins,
        onRefillWithCoins = onRefillHearts,
        onWatchAd = onWatchAdForHeart,
        onClose = onBackClick
      )
    }

    // Settings Dialog
    if (uiState.showSettingsDialog) {
      SettingsDialog(
        theme = theme,
        soundEnabled = uiState.soundEnabled,
        musicEnabled = uiState.musicEnabled,
        vibrationEnabled = uiState.vibrationEnabled,
        onToggleSound = onToggleSound,
        onToggleMusic = onToggleMusic,
        onToggleVibration = onToggleVibration,
        onThemesClick = onThemesClick,
        onHowToPlayClick = onOpenHowToPlay,
        onResetData = onResetData,
        onFeedbackClick = onFeedbackClick,
        onClose = onCloseSettings
      )
    }

    // Feedback Dialog
    if (uiState.showFeedbackDialog) {
      FeedbackDialog(
        theme = theme,
        onDismiss = onCloseFeedback,
        onSubmit = onSubmitFeedback
      )
    }

    // How to Play Dialog
    if (uiState.showHowToPlayDialog) {
      HowToPlayDialog(
        theme = theme,
        onClose = onCloseHowToPlay
      )
    }
  }
}
