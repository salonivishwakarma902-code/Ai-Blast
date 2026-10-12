package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameTheme

@Composable
fun ShopDialog(
  theme: GameTheme,
  userCoins: Int,
  onClose: () -> Unit,
  onBuyCoins: (Int) -> Unit
) {
  Dialog(onDismissRequest = onClose) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = theme.cardBackground,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, theme.boardBorder, RoundedCornerShape(24.dp))
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Coin Shop",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = theme.textPrimary
          )
          IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = theme.textSecondary
            )
          }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Current Coins
        Surface(
          shape = CircleShape,
          color = Color(0xFFFDE047).copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Coins",
              tint = Color(0xFFEAB308),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "$userCoins Coins",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
          text = "Purchase Coins",
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = theme.textSecondary,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Pack 1
        CoinPackItem(
          theme = theme,
          coinsAmount = 500,
          priceText = "₹49",
          onClick = { onBuyCoins(500) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Pack 2
        CoinPackItem(
          theme = theme,
          coinsAmount = 1500,
          priceText = "₹99",
          isPopular = true,
          onClick = { onBuyCoins(1500) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Pack 3
        CoinPackItem(
          theme = theme,
          coinsAmount = 5000,
          priceText = "₹249",
          onClick = { onBuyCoins(5000) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
          text = "Note: PhonePe / UPI integration coming in next update. Coins will be granted for free in testing.",
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          color = theme.textSecondary.copy(alpha = 0.8f)
        )
      }
    }
  }
}



@Composable
private fun CoinPackItem(
  theme: GameTheme,
  coinsAmount: Int,
  priceText: String,
  isPopular: Boolean = false,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(70.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isPopular) Brush.horizontalGradient(listOf(Color(0xFFFEF08A), Color(0xFFFDE047)))
        else SolidColor(theme.boardBackground)
      )
      .border(
        1.dp,
        if (isPopular) Color(0xFFEAB308) else theme.boardBorder,
        RoundedCornerShape(16.dp)
      )
      .clickable { onClick() }
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "Coins",
          tint = Color(0xFFEAB308),
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "$coinsAmount Coins",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPopular) Color.Black else theme.textPrimary
          )
          if (isPopular) {
            Text(
              text = "MOST POPULAR",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFB45309)
            )
          }
        }
      }
      
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPopular) Color(0xFFEAB308) else Color(0xFF0284C7)
      ) {
        Text(
          text = priceText,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }
  }
}
