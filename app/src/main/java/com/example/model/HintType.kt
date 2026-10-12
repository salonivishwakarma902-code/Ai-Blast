package com.example.model

enum class HintType(val title: String, val costCoins: Int, val description: String) {
  HIGHLIGHT("Highlight Arrow", 50, "Glows a free arrow that can safely escape right now"),
  PATH("Show Escape Path", 50, "Draws the escape trajectory line for the best arrow"),
  AUTO_MOVE("Auto Move", 50, "Automatically clears the next best arrow for you")
}
