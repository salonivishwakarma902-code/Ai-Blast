with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("  onWatchAdForCoins: () -> Unit,\n  onToggleSound: () -> Unit,", "  onWatchAdForCoins: () -> Unit,\n  onToggleRuler: () -> Unit,\n  onToggleSound: () -> Unit,")
content = content.replace("clickable { onSkipLevelClick() /* Will map this to ViewModel toggle ruler externally, but for now we'll do it manually */ }", "clickable { onToggleRuler() }")

with open('app/src/main/java/com/example/ui/screens/GameplayScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace("                  onWatchAdForCoins = { viewModel.watchAdForCoins() },\n                  onToggleSound = { viewModel.toggleSound() },", "                  onWatchAdForCoins = { viewModel.watchAdForCoins() },\n                  onToggleRuler = { viewModel.toggleRuler() },\n                  onToggleSound = { viewModel.toggleSound() },")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
