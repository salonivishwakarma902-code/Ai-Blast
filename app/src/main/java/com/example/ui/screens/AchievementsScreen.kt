package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.ui.GameUiState
import com.example.ui.components.CommonHeader

@Composable
fun AchievementsScreen(
  uiState: GameUiState,
  onBackClick: () -> Unit,
  onClaimAchievement: (Achievement) -> Unit,
  onThemesClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  val theme = uiState.currentTheme

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
        text = "ACHIEVEMENTS & BADGES",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = theme.textPrimary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 6.dp)
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(uiState.achievements) { achievement ->
          AchievementItemCard(
            achievement = achievement,
            theme = theme,
            onClaim = { onClaimAchievement(achievement) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
private fun AchievementItemCard(
  achievement: Achievement,
  theme: com.example.model.GameTheme,
  onClaim: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (achievement.isCompleted && !achievement.isClaimed) theme.primaryAccent else theme.boardBorder
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("achievement_card_${achievement.id}")
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Icon Box
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(theme.gridLineColor.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
      ) {
        Text(achievement.iconEmoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(achievement.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = theme.textPrimary)
        Text(achievement.description, fontSize = 12.sp, color = theme.textSecondary)

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar
        LinearProgressIndicator(
          progress = { achievement.progressFraction },
          color = if (achievement.isCompleted) Color(0xFF10B981) else theme.primaryAccent,
          trackColor = theme.gridLineColor.copy(alpha = 0.4f),
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
        )

        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${achievement.currentProgress}/${achievement.targetCount}",
          fontSize = 10.sp,
          color = theme.textSecondary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Action / Reward Button
      if (achievement.isClaimed) {
        Surface(
          shape = CircleShape,
          color = Color(0xFFD1FAE5),
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Claimed", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
          }
        }
      } else if (achievement.isCompleted) {
        Button(
          onClick = onClaim,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          modifier = Modifier
            .height(38.dp)
            .testTag("claim_achievement_${achievement.id}")
        ) {
          Text("+${achievement.coinReward}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
      } else {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = theme.gridLineColor.copy(alpha = 0.3f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("${achievement.coinReward}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.textSecondary)
          }
        }
      }
    }
  }
}
