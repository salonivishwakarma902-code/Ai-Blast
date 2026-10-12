with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'r') as f:
    content = f.read()

import re

old = r"""      PuzzleBoard\(
        theme = theme,
        gridWidth = level.gridWidth,
        gridHeight = level.gridHeight,
        arrows = uiState.activeArrows,
        highlightedArrowId = uiState.highlightedArrowId,
        trajectoryArrow = uiState.trajectoryArrow,
        onArrowClick = onArrowClick
      \)"""

new = """      PuzzleBoard(
        theme = theme,
        gridWidth = level.gridWidth,
        gridHeight = level.gridHeight,
        arrows = uiState.activeArrows,
        highlightedArrowId = uiState.highlightedArrowId,
        trajectoryArrow = uiState.trajectoryArrow,
        showRuler = uiState.showRuler,
        onArrowClick = onArrowClick
      )"""

content = re.sub(old, new, content)

with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'w') as f:
    f.write(content)
