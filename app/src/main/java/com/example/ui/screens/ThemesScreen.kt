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
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.model.GameTheme
import com.example.ui.GameUiState
import com.example.ui.components.CommonHeader

@Composable
fun ThemesScreen(
  uiState: GameUiState,
  onBackClick: () -> Unit,
  onSelectTheme: (GameTheme) -> Unit,
  onWatchAdForCoins: () -> Unit,
  onSettingsClick: () -> Unit
) {
  val currentTheme = uiState.currentTheme

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(currentTheme.backgroundGradientStart, currentTheme.backgroundGradientEnd)
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
        theme = currentTheme,
        coins = uiState.coins,
        hearts = uiState.hearts,
        secondsUntilNextHeart = uiState.secondsUntilNextHeart,
        showBack = true,
        onBackClick = onBackClick,
        onThemeClick = {},
        onSettingsClick = onSettingsClick
      )

      Text(
        text = "VISUAL THEMES",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = currentTheme.textPrimary,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 6.dp)
      )

      // Watch Ad bonus card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = currentTheme.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .padding(bottom = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Watch Video Ad", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = currentTheme.textPrimary)
              Text("Get +50 Free Coins instantly!", fontSize = 11.sp, color = currentTheme.textSecondary)
            }
          }

          Button(
            onClick = onWatchAdForCoins,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
            modifier = Modifier.height(36.dp)
          ) {
            Text("+50", fontWeight = FontWeight.Black, color = Color.White)
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(GameTheme.ALL_THEMES) { themeItem ->
          val isUnlocked = uiState.unlockedThemeIds.contains(themeItem.id)
          val isEquipped = currentTheme.id == themeItem.id

          ThemeCard(
            themeItem = themeItem,
            isUnlocked = isUnlocked,
            isEquipped = isEquipped,
            userCoins = uiState.coins,
            activeTheme = currentTheme,
            onSelect = { onSelectTheme(themeItem) }
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
private fun ThemeCard(
  themeItem: GameTheme,
  isUnlocked: Boolean,
  isEquipped: Boolean,
  userCoins: Int,
  activeTheme: GameTheme,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = themeItem.cardBackground),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isEquipped) 2.5.dp else 1.dp,
      color = if (isEquipped) themeItem.primaryAccent else themeItem.boardBorder
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("theme_card_${themeItem.id}")
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Color swatches & Title
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Palette preview circles
        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(themeItem.backgroundGradientStart)
              .border(1.dp, Color.White, CircleShape)
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(themeItem.primaryAccent)
              .border(1.dp, Color.White, CircleShape)
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(themeItem.arrowBodyColor)
              .border(1.dp, Color.White, CircleShape)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(themeItem.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = themeItem.textPrimary)
          Text(
            text = if (themeItem.isDark) "Dark OLED" else "Light & Clean",
            fontSize = 11.sp,
            color = themeItem.textSecondary
          )
        }
      }

      // Action Button
      if (isEquipped) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = themeItem.primaryAccent
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("EQUIPPED", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
          }
        }
      } else if (isUnlocked) {
        OutlinedButton(
          onClick = onSelect,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(38.dp)
        ) {
          Text("Select", color = activeTheme.textPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      } else {
        Button(
          onClick = onSelect,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (userCoins >= themeItem.priceCoins) themeItem.primaryAccent else Color(0xFF94A3B8)
          ),
          modifier = Modifier.height(38.dp)
        ) {
          Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("${themeItem.priceCoins}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
      }
    }
  }
}
