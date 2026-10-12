package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTheme

@Composable
fun CommonHeader(
  theme: GameTheme,
  coins: Int,
  hearts: Int,
  secondsUntilNextHeart: Int,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  onHeartsClick: () -> Unit = {},
  onCoinsClick: () -> Unit = {},
  onThemeClick: () -> Unit = {},
  onSettingsClick: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left side: Back button or Theme shortcut
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .testTag("header_back_button")
            .size(42.dp)
            .clip(CircleShape)
            .background(theme.cardBackground.copy(alpha = 0.9f))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = theme.textPrimary
          )
        }
      } else {
        IconButton(
          onClick = onThemeClick,
          modifier = Modifier
            .testTag("header_theme_button")
            .size(42.dp)
            .clip(CircleShape)
            .background(theme.cardBackground.copy(alpha = 0.9f))
        ) {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = "Themes",
            tint = theme.primaryAccent
          )
        }
      }
    }

    // Right side: Hearts pill + Coins pill + Settings
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Hearts indicator
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = theme.cardBackground.copy(alpha = 0.95f),
        shadowElevation = 2.dp,
        modifier = Modifier
          .testTag("header_hearts_pill")
          .clip(RoundedCornerShape(20.dp))
          .clickable { onHeartsClick() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Hearts",
            tint = if (hearts > 0) Color(0xFFEF4444) else Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$hearts/3",
            color = theme.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          if (hearts < 3 && secondsUntilNextHeart > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            val mins = secondsUntilNextHeart / 60
            val secs = secondsUntilNextHeart % 60
            Text(
              text = String.format("%02d:%02d", mins, secs),
              color = theme.textSecondary,
              fontSize = 11.sp
            )
          }
        }
      }

      // Coins indicator
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = theme.cardBackground.copy(alpha = 0.95f),
        shadowElevation = 2.dp,
        modifier = Modifier
          .testTag("header_coins_pill")
          .clip(RoundedCornerShape(20.dp))
          .clickable { onCoinsClick() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "Coins",
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$coins",
            color = theme.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }

      // Settings button
      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier
          .testTag("header_settings_button")
          .size(42.dp)
          .clip(CircleShape)
          .background(theme.cardBackground.copy(alpha = 0.9f))
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = "Settings",
          tint = theme.textSecondary
        )
      }
    }
  }
}
