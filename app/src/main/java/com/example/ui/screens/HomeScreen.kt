package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTheme
import com.example.ui.GameUiState
import com.example.ui.Screen
import com.example.ui.components.CommonHeader

@Composable
fun HomeScreen(
  uiState: GameUiState,
  onPlayClick: () -> Unit,
  onLevelSelectClick: () -> Unit,
  onDailyChallengeClick: () -> Unit,
  onDailyRewardClick: () -> Unit,
  onAchievementsClick: () -> Unit,
  onThemesClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onHowToPlayClick: () -> Unit,
  onHeartsClick: () -> Unit,
  onCoinsClick: () -> Unit,
  onFeedbackClick: () -> Unit
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
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation / Resource Bar
      CommonHeader(
        theme = theme,
        coins = uiState.coins,
        hearts = uiState.hearts,
        secondsUntilNextHeart = uiState.secondsUntilNextHeart,
        showBack = false,
        onHeartsClick = onHeartsClick,
        onCoinsClick = onCoinsClick,
        onThemeClick = onThemesClick,
        onSettingsClick = onSettingsClick
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Game Hero Logo & Floating Animated Arrows
      AnimatedHeroLogo(theme = theme)

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "ARROW ESCAPE",
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = theme.textPrimary,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center
      )

      Text(
        text = "Mind Escape Puzzle",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = theme.textSecondary,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Main CTA: PLAY LEVEL X
      val infiniteTransition = rememberInfiniteTransition(label = "pulse")
      val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
          animation = tween(900, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
      )

      Button(
        onClick = onPlayClick,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = theme.primaryAccent),
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(64.dp)
          .scale(pulseScale)
          .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = theme.primaryAccent)
          .testTag("play_button")
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "PLAY LEVEL ${uiState.currentLevelId}",
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Level Select Button
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = theme.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.boardBorder),
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(54.dp)
          .clip(RoundedCornerShape(18.dp))
          .clickable { onLevelSelectClick() }
          .testTag("level_select_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 20.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(imageVector = Icons.Default.GridOn, contentDescription = null, tint = theme.primaryAccent, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "SELECT LEVEL (100 Levels)",
            color = theme.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Daily Challenge Card
      val infinitePulseTransition = rememberInfiniteTransition()
      val dailyPulseScale by infinitePulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (uiState.isDailyChallengeDoneToday) 1f else 1.03f,
        animationSpec = infiniteRepeatable(
          animation = tween(1000, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
      
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
        border = androidx.compose.foundation.BorderStroke(
          1.5.dp,
          if (uiState.isDailyChallengeDoneToday) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFEC4899).copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .scale(dailyPulseScale)
          .clip(RoundedCornerShape(20.dp))
          .clickable { onDailyChallengeClick() }
          .testTag("daily_challenge_card")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (uiState.isDailyChallengeDoneToday) Color(0xFFD1FAE5) else Color(0xFFFCE7F3)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = if (uiState.isDailyChallengeDoneToday) Color(0xFF10B981) else Color(0xFFEC4899),
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Daily Challenge", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = theme.textPrimary)
              Text(
                text = if (uiState.isDailyChallengeDoneToday) "Completed Today! ✓" else "Earn +25 Bonus Coins!",
                fontSize = 12.sp,
                color = if (uiState.isDailyChallengeDoneToday) Color(0xFF10B981) else theme.textSecondary
              )
            }
          }

          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = theme.primaryAccent,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      
      Spacer(modifier = Modifier.height(20.dp))

      // Bottom Action Icons Grid
      Row(
        modifier = Modifier
          .fillMaxWidth(0.88f)
          .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        QuickActionButton(
          title = "Daily Gift",
          icon = Icons.Default.CardGiftcard,
          badge = uiState.isDailyRewardClaimable,
          theme = theme,
          onClick = onDailyRewardClick,
          tag = "home_daily_reward"
        )

        QuickActionButton(
          title = "Badges",
          icon = Icons.Default.EmojiEvents,
          badge = false,
          theme = theme,
          onClick = onAchievementsClick,
          tag = "home_achievements"
        )

        QuickActionButton(
          title = "Themes",
          icon = Icons.Default.Palette,
          badge = false,
          theme = theme,
          onClick = onThemesClick,
          tag = "home_themes"
        )

        QuickActionButton(
          title = "Rules",
          icon = Icons.Default.HelpOutline,
          badge = false,
          theme = theme,
          onClick = onHowToPlayClick,
          tag = "home_rules"
        )

        QuickActionButton(
          title = "Feedback",
          icon = Icons.Default.RateReview,
          badge = false,
          theme = theme,
          onClick = onFeedbackClick,
          tag = "home_feedback"
        )
      }
    }
  }
}

@Composable
private fun QuickActionButton(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  badge: Boolean,
  theme: GameTheme,
  onClick: () -> Unit,
  tag: String
) {
  val infiniteTransition = rememberInfiniteTransition()
  val shadowElevation by infiniteTransition.animateFloat(
    initialValue = if (badge) 4f else 4f,
    targetValue = if (badge) 12f else 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    )
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(8.dp)
      .testTag(tag)
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .shadow(shadowElevation.dp, CircleShape)
        .clip(CircleShape)
        .background(theme.cardBackground)
        .border(1.dp, theme.boardBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = title, tint = theme.primaryAccent, modifier = Modifier.size(26.dp))

      if (badge) {
        Box(
          modifier = Modifier
            .size(12.dp)
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(Color(0xFFEF4444))
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.textSecondary)
  }
}

@Composable
private fun AnimatedHeroLogo(theme: GameTheme) {
  val infiniteTransition = rememberInfiniteTransition(label = "heroLogo")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(24000, easing = androidx.compose.animation.core.LinearEasing)
    ),
    label = "logoRotation"
  )
  
  val floatOffset by infiniteTransition.animateFloat(
    initialValue = -8f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "logoFloat"
  )

  Box(
    modifier = Modifier
      .offset(y = floatOffset.dp)
      .size(110.dp)
      .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = theme.primaryAccent)
      .clip(RoundedCornerShape(32.dp))
      .background(theme.cardBackground)
      .border(1.5.dp, theme.primaryAccent.copy(alpha = 0.4f), RoundedCornerShape(32.dp)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(76.dp)) {
      val cx = size.width / 2
      val cy = size.height / 2

      // Central core dot
      drawCircle(
        color = theme.primaryAccent,
        radius = 5f,
        center = Offset(cx, cy)
      )

      rotate(degrees = rotation, pivot = Offset(cx, cy)) {
        // Draw 4 cardinal sleek escape arrows
        for (i in 0 until 4) {
          rotate(degrees = i * 90f, pivot = Offset(cx, cy)) {
            val color = if (i % 2 == 0) theme.primaryAccent else theme.arrowBodyColor
            
            // 3D Drop Shadow for the arrows inside the logo
            drawLine(
              color = Color.Black.copy(alpha = 0.2f),
              start = Offset(cx + 2f, cy - 6f),
              end = Offset(cx + 2f, cy - 26f),
              strokeWidth = 3.5f,
              cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Stem line
            drawLine(
              color = color,
              start = Offset(cx, cy - 8f),
              end = Offset(cx, cy - 28f),
              strokeWidth = 3.5f,
              cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            // Reflection shine
            drawLine(
              color = Color.White.copy(alpha = 0.5f),
              start = Offset(cx, cy - 8f),
              end = Offset(cx, cy - 28f),
              strokeWidth = 1.2f,
              cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            // Chevron tip
            val chevronPath = Path().apply {
              moveTo(cx - 7f, cy - 23f)
              lineTo(cx, cy - 31f)
              lineTo(cx + 7f, cy - 23f)
            }
            
            // 3D Drop Shadow for Chevron Tip
            drawPath(
              path = chevronPath,
              color = Color.Black.copy(alpha = 0.2f),
              style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 4.5f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
              )
            )

            drawPath(
              path = chevronPath,
              color = color,
              style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 3.5f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
              )
            )
          }
        }
      }
    }
  }
}
