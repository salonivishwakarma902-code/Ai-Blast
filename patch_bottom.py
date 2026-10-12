with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'r') as f:
    content = f.read()

import re

old_bottom = r"""      // 4. Bottom Controls: Floating Circular Action Buttons \(Hint, Restart, Undo\)
      Row\(
        modifier = Modifier
          .fillMaxWidth\(\)
          .padding\(bottom = 24.dp, start = 32.dp, end = 32.dp\),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      \) \{[\s\S]*?\}
    \}"""

new_bottom = """      // 4. Bottom Controls: Bulb and Ruler
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
            .clickable { onSkipLevelClick() /* Will map this to ViewModel toggle ruler externally, but for now we'll do it manually */ }
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
    }"""

content = re.sub(old_bottom, new_bottom, content)

with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'w') as f:
    f.write(content)
