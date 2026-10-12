with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

replacement = """      }

      androidx.compose.animation.AnimatedVisibility(
        visible = arrows.isEmpty(),
        enter = androidx.compose.animation.scaleIn() + androidx.compose.animation.fadeIn(),
        modifier = Modifier.align(Alignment.Center)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("😊", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Impressive!", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, color = Color(0xFF0284C7))
        }
      }

      // Render animated exit & shake overlays"""

if "Impressive!" not in content:
    content = content.replace("      }\n\n      // Render animated exit & shake overlays", replacement)

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
