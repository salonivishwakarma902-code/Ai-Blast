import re

# 1. MainActivity.kt
with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

if "onToggleRuler =" not in content:
    content = content.replace("onSkipLevelClick = { viewModel.skipLevel() },", "onSkipLevelClick = { viewModel.skipLevel() },\n                onToggleRuler = { viewModel.toggleRuler() },")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

# 2. Dialogs.kt
with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()

content = content.replace("@Composable\n@Composable", "@Composable")
content = content.replace("import androidx.compose.animation.core.tween", "import androidx.compose.animation.core.tween\nimport androidx.compose.animation.core.animateFloat\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember")

with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
    f.write(content)

# 3. PuzzleBoard.kt
with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

content = content.replace("androidx.compose.ui.geometry.Offset", "Offset")

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
