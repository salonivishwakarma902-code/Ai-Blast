package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GameTheme

@Composable
fun FeedbackDialog(
  theme: GameTheme,
  onDismiss: () -> Unit,
  onSubmit: (rating: Int, category: String, comment: String) -> Unit
) {
  var rating by remember { mutableIntStateOf(5) }
  var selectedCategory by remember { mutableStateOf("🎮 Gameplay & Motion") }
  var commentText by remember { mutableStateOf("") }
  var isSubmitted by remember { mutableStateOf(false) }

  val categories = listOf(
    "🎮 Gameplay & Motion",
    "✨ 3D Graphics & Arrows",
    "🔊 Sound & Haptics",
    "🧩 Shapes & Puzzles",
    "💡 Ideas & Level Design",
    "🐛 Bug Report"
  )

  val sentimentText = when (rating) {
    5 -> "Mind Blowing! 😍"
    4 -> "Really Enjoying It! 😊"
    3 -> "Good, Can Be Better 👍"
    2 -> "Needs Improvement 😐"
    else -> "Disappointed 😕"
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.boardBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("feedback_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = Color(0xFF0284C7).copy(alpha = 0.12f),
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Player Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
              )
              Text(
                text = "Ratings & Suggestions",
                fontSize = 12.sp,
                color = theme.textSecondary
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = theme.textSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSubmitted) {
          // Thank you screen
          AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 24.dp)
            ) {
              Text("🌟", fontSize = 54.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Thank You for Your Feedback!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0284C7),
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Your response has been saved. We are continuously improving the puzzle physics, realistic aesthetics, and level designs!",
                fontSize = 13.sp,
                color = theme.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
              )
              Spacer(modifier = Modifier.height(24.dp))
              Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.height(44.dp)
              ) {
                Text(
                  text = "Back to Game",
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        } else {
          // 1. Star Rating with tactile animations
          Text(
            text = "How do you rate the experience?",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            for (star in 1..5) {
              val isSelected = star <= rating
              IconButton(
                onClick = { rating = star },
                modifier = Modifier
                  .size(44.dp)
                  .testTag("star_rate_$star")
              ) {
                Icon(
                  imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                  contentDescription = "$star stars",
                  tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                  modifier = Modifier.size(34.dp)
                )
              }
            }
          }

          Text(
            text = sentimentText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (rating >= 4) Color(0xFF059669) else if (rating == 3) Color(0xFFF59E0B) else Color(0xFFEF4444),
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(18.dp))

          // 2. Category selection chips
          Text(
            text = "What would you like to review?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary,
            modifier = Modifier.align(Alignment.Start)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            categories.forEach { cat ->
              val isCategorySelected = cat == selectedCategory
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isCategorySelected) Color(0xFF0284C7) else theme.boardBackground,
                border = androidx.compose.foundation.BorderStroke(
                  width = 1.dp,
                  color = if (isCategorySelected) Color(0xFF0284C7) else theme.boardBorder
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .clickable { selectedCategory = cat }
              ) {
                Text(
                  text = cat,
                  fontSize = 12.sp,
                  fontWeight = if (isCategorySelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isCategorySelected) Color.White else theme.textPrimary,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 3. Optional Comment Box
          Text(
            text = "Tell us what could be improved:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary,
            modifier = Modifier.align(Alignment.Start)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = {
              Text(
                text = "Share suggestions on slide motion, pinch zoom, shapes, or sounds...",
                fontSize = 12.sp,
                color = theme.textSecondary.copy(alpha = 0.7f)
              )
            },
            shape = RoundedCornerShape(16.dp),
            minLines = 3,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFF0284C7),
              unfocusedBorderColor = theme.boardBorder,
              focusedContainerColor = theme.boardBackground,
              unfocusedContainerColor = theme.boardBackground
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Button
          Button(
            onClick = {
              onSubmit(rating, selectedCategory, commentText)
              isSubmitted = true
            },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_feedback_button")
          ) {
            Text(
              text = "Submit Feedback",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
