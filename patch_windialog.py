with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()

# We need to replace fun WinDialog(...) { ... } entirely.
# Let's find the start of WinDialog
start_idx = content.find('fun WinDialog(')
# Find the next fun
end_idx = content.find('fun OutOfHeartsDialog(', start_idx)

new_win_dialog = """fun WinDialog(
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
                import com.example.ui.components.ArrowRenderer.drawMinimalistArrow
                // We'll draw them manually using simple paths to avoid import issues inside here, or we can just use drawMinimalistArrow if imported.
                // Actually ArrowRenderer is in the same package so we can use it.
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

"""

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_win_dialog + content[end_idx:]
    with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
        f.write(content)
else:
    print("Could not find WinDialog boundaries")
