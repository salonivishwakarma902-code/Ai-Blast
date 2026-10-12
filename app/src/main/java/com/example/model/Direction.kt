package com.example.model

enum class Direction(val dx: Int, val dy: Int, val angle: Float, val label: String) {
  UP(0, -1, 0f, "Up"),
  RIGHT(1, 0, 90f, "Right"),
  DOWN(0, 1, 180f, "Down"),
  LEFT(-1, 0, 270f, "Left")
}
