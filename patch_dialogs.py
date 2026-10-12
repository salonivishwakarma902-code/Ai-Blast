with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'r') as f:
    content = f.read()

content = content.replace("fun WinDialog(", "@Composable\nfun WinDialog(")
content = content.replace("                import com.example.ui.components.ArrowRenderer.drawMinimalistArrow", "")
content = content.replace("                // We'll draw them manually using simple paths to avoid import issues inside here, or we can just use drawMinimalistArrow if imported.", "")
content = content.replace("                // Actually ArrowRenderer is in the same package so we can use it.", "")

with open('app/src/main/java/com/example/ui/components/Dialogs.kt', 'w') as f:
    f.write(content)
