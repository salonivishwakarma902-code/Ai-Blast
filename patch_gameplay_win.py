with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'r') as f:
    content = f.read()

import re

old_call = r"""      WinDialog\(
        theme = theme,
        stars = uiState\.winStars,
        coinsEarned = uiState\.winCoinsEarned,
        movesCount = uiState\.movesCount,
        onNextLevel = onNextLevel,
        onReplay = onRestartClick,
        onLevelSelect = onLevelSelectClick
      \)"""

new_call = """      WinDialog(
        theme = theme,
        level = level,
        onNextLevel = onNextLevel,
        onMain = onLevelSelectClick
      )"""

content = re.sub(old_call, new_call, content)

with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'w') as f:
    f.write(content)
