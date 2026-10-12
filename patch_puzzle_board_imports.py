with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.sp
"""

content = content.replace("import androidx.compose.foundation.layout.padding", imports + "import androidx.compose.foundation.layout.padding")

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
