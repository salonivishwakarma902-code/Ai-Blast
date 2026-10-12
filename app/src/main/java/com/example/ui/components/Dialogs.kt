package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.components.ArrowRenderer.drawMinimalistArrow
import androidx.compose.ui.window.DialogProperties
import com.example.model.DailyRewardDay
import com.example.model.GameTheme
import com.example.model.HintType
import kotlinx.coroutines.delay

@Composable
fun WinDialog(
  theme: GameTheme,
  level: com.example.model.PuzzleLevel,
  onNextLevel: () -> Unit,
  onMain: () -> Unit
) {
  Dialog(
    onDismissRequest = {},
    properties = androidx.compose.ui.window.DialogProperties(
      dismissOnBackPress = false, 
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.radialGradient(
            colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
            center = androidx.compose.ui.geometry.Offset.Infinite,
            radius = 2000f
          )
        )
    ) {
      // Rays background can be achieved with a simple rotating canvas
      val infiniteTransition = rememberInfiniteTransition()
      val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(20000, easing = androidx.compose.animation.core.LinearEasing)
        )
      )
      
      Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.width * 1.5f
        rotate(rotation, Offset(cx, cy)) {
          for (i in 0 until 12) {
            rotate(i * 30f, Offset(cx, cy)) {
              drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                  moveTo(cx, cy)
                  lineTo(cx - radius * 0.2f, cy - radius)
                  lineTo(cx + radius * 0.2f, cy - radius)
                  close()
                },
                color = Color.White.copy(alpha = 0.08f)
              )
            }
          }
        }
      }

      ConfettiView(modifier = Modifier.fillMaxSize(), particleCount = 120)

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 80.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Level Completed!",
          fontSize = 36.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          modifier = Modifier.shadow(8.dp)
        )

        // White card with solved puzzle
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
          elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 16.dp),
          modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
        ) {
          Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val gridWidth = level.gridWidth
              val gridHeight = level.gridHeight
              val cellW = size.width / gridWidth
              val cellH = size.height / gridHeight
              
              level.arrows.forEach { arrow ->



                com.example.ui.components.ArrowRenderer.apply {
                  drawMinimalistArrow(
                    arrow = arrow,
                    cellW = cellW,
                    cellH = cellH,
                    theme = GameTheme.CLASSIC_WHITE, // use white theme for dark arrows on white card
                    isHighlighted = false,
                    shimmerProgress = 0f
                  )
                }
              }
            }
          }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          androidx.compose.material3.Button(
            onClick = onNextLevel,
            shape = RoundedCornerShape(24.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
              .fillMaxWidth(0.7f)
              .height(56.dp)
              .shadow(8.dp, RoundedCornerShape(24.dp))
          ) {
            Text(
              text = "Next Game",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0284C7)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Level ${level.id + 1}",
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(start = 8.dp)
            )
          }
          
          Spacer(modifier = Modifier.height(16.dp))
          
          Text(
            text = "Main",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
              .clickable { onMain() }
              .padding(8.dp)
          )
        }
      }
    }
  }
}

@Composable
fun OutOfHeartsDialog(
  theme: GameTheme,
  secondsUntilNextHeart: Int,
  userCoins: Int,
  onRefillWithCoins: () -> Unit,
  onWatchAd: () -> Unit,
  onClose: () -> Unit
) {
  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      modifier = Modifier
        .fillMaxWidth()
        .border(2.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(28.dp))
        .padding(4.dp)
        .testTag("out_of_hearts_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0xFFFEE2E2)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Out of Hearts!",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = theme.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "You made too many collisions. Refill hearts to continue solving!",
          fontSize = 14.sp,
          color = theme.textSecondary,
          textAlign = TextAlign.Center
        )

        if (secondsUntilNextHeart > 0) {
          Spacer(modifier = Modifier.height(10.dp))
          val mins = secondsUntilNextHeart / 60
          val secs = secondsUntilNextHeart % 60
          Text(
            text = String.format("Free heart in: %02d:%02d", mins, secs),
            fontSize = 13.sp,
            color = theme.primaryAccent,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Refill 3 Hearts for 30 Coins
        Button(
          onClick = onRefillWithCoins,
          colors = ButtonDefaults.buttonColors(containerColor = if (userCoins >= 30) Color(0xFF10B981) else Color(0xFF94A3B8)),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("refill_hearts_coins_button")
        ) {
          Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Refill Full (30 Coins)", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Watch Ad for +1 Heart
        OutlinedButton(
          onClick = onWatchAd,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("watch_ad_heart_button")
        ) {
          Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = theme.primaryAccent, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Watch Ad (+1 Heart)", color = theme.primaryAccent, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = onClose,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Text("Back to Menu", color = theme.textSecondary, fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
fun DailyRewardDialog(
  theme: GameTheme,
  streak: Int,
  rewards: List<DailyRewardDay>,
  onClaim: (DailyRewardDay) -> Unit,
  onClose: () -> Unit
) {
  Dialog(onDismissRequest = onClose) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("daily_reward_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("DAILY REWARDS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = theme.textPrimary)
            Text("Current Streak: $streak Days 🔥", fontSize = 13.sp, color = theme.primaryAccent, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = theme.textSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7-day grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(4),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(rewards) { day ->
            val isToday = day.isToday
            val isClaimed = day.isClaimed

            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = when {
                  isClaimed -> theme.gridLineColor.copy(alpha = 0.3f)
                  isToday -> theme.primaryAccent.copy(alpha = 0.15f)
                  else -> theme.gridLineColor.copy(alpha = 0.15f)
                }
              ),
              modifier = Modifier
                .border(
                  width = if (isToday && !isClaimed) 2.dp else 1.dp,
                  color = if (isToday && !isClaimed) theme.primaryAccent else theme.gridLineColor,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable(enabled = isToday && !isClaimed) {
                  onClaim(day)
                }
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("Day ${day.dayNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                  imageVector = if (day.freeHint) Icons.Default.Lightbulb else Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = if (day.freeHint) Color(0xFFFBBF24) else Color(0xFFF59E0B),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text("+${day.coins}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = theme.textPrimary)

                if (isClaimed) {
                  Spacer(modifier = Modifier.height(2.dp))
                  Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        val todayReward = rewards.find { it.isToday }
        if (todayReward != null && !todayReward.isClaimed) {
          Button(
            onClick = { onClaim(todayReward) },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primaryAccent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("claim_daily_reward_button")
          ) {
            Text("CLAIM TODAY'S REWARD", fontWeight = FontWeight.Bold, color = Color.White)
          }
        } else {
          Text(
            text = "Come back tomorrow for your next reward!",
            fontSize = 13.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

@Composable
fun SettingsDialog(
  theme: GameTheme,
  soundEnabled: Boolean,
  musicEnabled: Boolean,
  vibrationEnabled: Boolean,
  onToggleSound: () -> Unit,
  onToggleMusic: () -> Unit,
  onToggleVibration: () -> Unit,
  onThemesClick: () -> Unit,
  onHowToPlayClick: () -> Unit,
  onResetData: () -> Unit,
  onFeedbackClick: (() -> Unit)? = null,
  onClose: () -> Unit
) {
  var showResetConfirm by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onClose) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("SETTINGS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = theme.textPrimary)
          IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = theme.textSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sound Toggle
        SettingToggleRow(
          title = "Sound FX",
          icon = Icons.Default.VolumeUp,
          isChecked = soundEnabled,
          onToggle = onToggleSound,
          theme = theme
        )

        // Music Toggle
        SettingToggleRow(
          title = "Music",
          icon = Icons.Default.MusicNote,
          isChecked = musicEnabled,
          onToggle = onToggleMusic,
          theme = theme
        )

        // Vibration Toggle
        SettingToggleRow(
          title = "Vibration & Haptics",
          icon = Icons.Default.Vibration,
          isChecked = vibrationEnabled,
          onToggle = onToggleVibration,
          theme = theme
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Feedback / Rating option
        if (onFeedbackClick != null) {
          SettingActionRow(
            title = "Feedback & Rating",
            icon = Icons.Default.RateReview,
            onClick = {
              onClose()
              onFeedbackClick()
            },
            theme = theme
          )
        }

        // Themes option
        SettingActionRow(
          title = "Visual Themes",
          icon = Icons.Default.Palette,
          onClick = {
            onClose()
            onThemesClick()
          },
          theme = theme
        )

        // How to Play
        SettingActionRow(
          title = "How to Play Guide",
          icon = Icons.Default.HelpOutline,
          onClick = {
            onClose()
            onHowToPlayClick()
          },
          theme = theme
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (!showResetConfirm) {
          OutlinedButton(
            onClick = { showResetConfirm = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Game Progress", color = Color(0xFFEF4444), fontSize = 13.sp)
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFFEE2E2), RoundedCornerShape(14.dp))
              .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("Are you sure? All progress, coins, and stars will be reset.", color = Color(0xFF991B1B), fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = {
                  showResetConfirm = false
                  onResetData()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Yes, Reset", color = Color.White, fontSize = 12.sp)
              }
              OutlinedButton(
                onClick = { showResetConfirm = false },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Cancel", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SettingToggleRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isChecked: Boolean,
  onToggle: () -> Unit,
  theme: GameTheme
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(imageVector = icon, contentDescription = null, tint = theme.primaryAccent, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Text(title, color = theme.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
    Switch(
      checked = isChecked,
      onCheckedChange = { onToggle() },
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = theme.primaryAccent
      )
    )
  }
}

@Composable
private fun SettingActionRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  theme: GameTheme
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = theme.gridLineColor.copy(alpha = 0.2f),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = theme.primaryAccent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, color = theme.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      }
      Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = theme.textSecondary, modifier = Modifier.size(16.dp))
    }
  }
}

@Composable
fun HowToPlayDialog(
  theme: GameTheme,
  onClose: () -> Unit
) {
  Dialog(onDismissRequest = onClose) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("how_to_play_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("HOW TO PLAY", fontSize = 20.sp, fontWeight = FontWeight.Black, color = theme.textPrimary)
          IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = theme.textSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        
        // About & Creator Info
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = theme.boardBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Developed By",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = theme.primaryAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "AIRADAV",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = theme.textPrimary
            )
            Text(
              text = "Mayank Vishwakarma",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = theme.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "© 2026 AIRADAV. All Rights Reserved. Protected by Copyright Act. Unauthorized copying, distribution, or reproduction of this game or its elements without explicit permission may result in legal action.",
              fontSize = 10.sp,
              lineHeight = 14.sp,
              color = theme.textSecondary.copy(alpha = 0.8f)
            )
          }
        }
        
        Spacer(modifier = Modifier.height(20.dp))

        RuleItem(
          stepNumber = "1",
          title = "Tap An Arrow",
          description = "Every arrow points in a direction: Up, Down, Left, or Right.",
          theme = theme
        )

        RuleItem(
          stepNumber = "2",
          title = "Path Must Be Clear",
          description = "If no other arrow blocks its escape route, the arrow flies out safely!",
          theme = theme
        )

        RuleItem(
          stepNumber = "3",
          title = "Watch Your Hearts",
          description = "Tapping a blocked arrow causes a collision and costs 1 Heart. You have 3 Hearts.",
          theme = theme
        )

        RuleItem(
          stepNumber = "4",
          title = "Clear The Grid",
          description = "Escape all arrows off the board in the correct sequence to earn up to 3 Stars & Coins!",
          theme = theme
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onClose,
          colors = ButtonDefaults.buttonColors(containerColor = theme.primaryAccent),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text("GOT IT! LET'S PLAY", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

@Composable
private fun RuleItem(
  stepNumber: String,
  title: String,
  description: String,
  theme: GameTheme
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(theme.primaryAccent),
      contentAlignment = Alignment.Center
    ) {
      Text(stepNumber, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(title, color = theme.textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Text(description, color = theme.textSecondary, fontSize = 12.sp)
    }
  }
}

@Composable
fun HintOptionsDialog(
  theme: GameTheme,
  userCoins: Int,
  freeHints: Int,
  onSelectHint: (HintType) -> Unit,
  onClose: () -> Unit
) {
  Dialog(onDismissRequest = onClose) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("hint_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("SOLVER HINTS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = theme.textPrimary)
            if (freeHints > 0) {
              Text("$freeHints Free Hint(s) Available!", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            }
          }
          IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = theme.textSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        HintOptionCard(
          type = HintType.HIGHLIGHT,
          freeHints = freeHints,
          userCoins = userCoins,
          theme = theme,
          onClick = {
            onClose()
            onSelectHint(HintType.HIGHLIGHT)
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        HintOptionCard(
          type = HintType.PATH,
          freeHints = freeHints,
          userCoins = userCoins,
          theme = theme,
          onClick = {
            onClose()
            onSelectHint(HintType.PATH)
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        HintOptionCard(
          type = HintType.AUTO_MOVE,
          freeHints = freeHints,
          userCoins = userCoins,
          theme = theme,
          onClick = {
            onClose()
            onSelectHint(HintType.AUTO_MOVE)
          }
        )
      }
    }
  }
}

@Composable
private fun HintOptionCard(
  type: HintType,
  freeHints: Int,
  userCoins: Int,
  theme: GameTheme,
  onClick: () -> Unit
) {
  val canAfford = freeHints > 0 || userCoins >= type.costCoins

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (canAfford) theme.gridLineColor.copy(alpha = 0.25f) else theme.gridLineColor.copy(alpha = 0.1f),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (canAfford) theme.primaryAccent.copy(alpha = 0.4f) else theme.gridLineColor),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(enabled = canAfford) { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(type.title, fontWeight = FontWeight.Bold, color = if (canAfford) theme.textPrimary else theme.textSecondary, fontSize = 14.sp)
        Text(type.description, fontSize = 11.sp, color = theme.textSecondary)
      }

      Spacer(modifier = Modifier.width(8.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (canAfford) theme.primaryAccent else theme.gridLineColor
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (freeHints > 0) {
            Text("FREE", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
          } else {
            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("${type.costCoins}", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
