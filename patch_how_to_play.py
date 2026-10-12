with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()

import re

# Find HowToPlayDialog
# Find "The objective is to clear all arrows"
# We will append the copyright text at the end of the dialog content.

# Search for the button at the bottom of HowToPlayDialog
old_button = """        Button(
          onClick = onClose,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Got It!")
        }"""

new_button = """        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "© 2026 Developed by AIRADAV (Mayank Vishwakarma)",
          fontSize = 12.sp,
          color = theme.textSecondary,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = onClose,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Got It!")
        }"""

content = content.replace(old_button, new_button)

with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
    f.write(content)
