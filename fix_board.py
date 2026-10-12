import re

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'r') as f:
    content = f.read()

# Make sure canvas is correct
content = content.replace("      Canvas(\n      Canvas(\n        modifier = Modifier", "      Canvas(\n        modifier = Modifier")

# Insert Impressive text after the main Canvas block if it doesn't exist
if "Impressive" not in content:
    # find where main canvas ends
    # The main canvas is inside a Box. The box ends at line 138 originally.
    pass

with open('app/src/main/java/com/example/ui/components/PuzzleBoard.kt', 'w') as f:
    f.write(content)
