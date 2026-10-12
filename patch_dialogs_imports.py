with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.components.ArrowRenderer.drawMinimalistArrow
"""

content = content.replace("import androidx.compose.ui.window.DialogProperties", imports + "import androidx.compose.ui.window.DialogProperties")

with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
    f.write(content)
