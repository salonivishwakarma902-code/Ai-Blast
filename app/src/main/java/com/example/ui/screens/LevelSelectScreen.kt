package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GamePreferences
import com.example.model.DifficultyTier
import com.example.model.GameTheme
import com.example.model.LevelProgress
import com.example.ui.GameUiState
import com.example.ui.components.CommonHeader

@Composable
fun LevelSelectScreen(
  uiState: GameUiState,
  onBackClick: () -> Unit,
  onLevelClick: (Int) -> Unit,
  onDailyChallengeClick: () -> Unit,
  onThemesClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  val theme = uiState.currentTheme
  val context = LocalContext.current
  val prefs = remember { GamePreferences(context) }

  val tabs = listOf("Easy (1-5)", "Normal (6-20)", "Hard (21-50)", "Harder (51-75)", "Impossible (76-100)")
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  val levelRange = when (selectedTabIndex) {
    0 -> 1..5
    1 -> 6..20
    2 -> 21..50
    3 -> 51..75
    else -> 76..100
  }

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
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      CommonHeader(
        theme = theme,
        coins = uiState.coins,
        hearts = uiState.hearts,
        secondsUntilNextHeart = uiState.secondsUntilNextHeart,
        showBack = true,
        onBackClick = onBackClick,
        onThemeClick = onThemesClick,
        onSettingsClick = onSettingsClick
      )

      Text(
        text = "SELECT LEVEL",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = theme.textPrimary,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 6.dp)
      )

      // Tabs
      ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = theme.primaryAccent,
        edgePadding = 16.dp,
        divider = {}
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (selectedTabIndex == index) theme.primaryAccent else theme.textSecondary
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Level Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(levelRange.toList()) { levelId ->
          val prog = prefs.getLevelProgress(levelId)
          LevelGridCard(
            levelId = levelId,
            progress = prog,
            currentPlayingLevel = uiState.currentLevelId,
            theme = theme,
            onClick = {
              if (prog.isUnlocked) {
                onLevelClick(levelId)
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun LevelGridCard(
  levelId: Int,
  progress: LevelProgress,
  currentPlayingLevel: Int,
  theme: GameTheme,
  onClick: () -> Unit
) {
  val isCurrent = levelId == currentPlayingLevel
  val isUnlocked = progress.isUnlocked

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = when {
        isCurrent -> theme.primaryAccent.copy(alpha = 0.2f)
        progress.isCompleted -> theme.cardBackground
        isUnlocked -> theme.cardBackground
        else -> theme.gridLineColor.copy(alpha = 0.25f)
      }
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isCurrent) 2.dp else 1.dp,
      color = when {
        isCurrent -> theme.primaryAccent
        progress.isCompleted -> theme.primaryAccent.copy(alpha = 0.5f)
        else -> theme.boardBorder
      }
    ),
    modifier = Modifier
      .height(84.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(enabled = isUnlocked) { onClick() }
      .testTag("level_card_$levelId")
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      if (!isUnlocked) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Locked",
          tint = theme.textSecondary.copy(alpha = 0.6f),
          modifier = Modifier.size(22.dp)
        )
      } else {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "$levelId",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = theme.textPrimary
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Stars
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (s in 1..3) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (s <= progress.stars) Color(0xFFFBBF24) else theme.gridLineColor.copy(alpha = 0.6f),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }
  }
}
