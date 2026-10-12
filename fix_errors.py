# 1. Dialogs.kt - put back @Composable for OutOfHeartsDialog
with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()
content = content.replace("fun OutOfHeartsDialog(", "@Composable\nfun OutOfHeartsDialog(")
with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
    f.write(content)

# 2. PuzzleBoard.kt - fix import
with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

# I need to add import androidx.compose.ui.geometry.Offset
if "import androidx.compose.ui.geometry.Offset" not in content:
    content = content.replace("import Offset", "import androidx.compose.ui.geometry.Offset")
    # if it wasn't there at all:
    if "import androidx.compose.ui.geometry.Offset" not in content:
        content = content.replace("import androidx.compose.runtime.remember", "import androidx.compose.runtime.remember\nimport androidx.compose.ui.geometry.Offset")

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
