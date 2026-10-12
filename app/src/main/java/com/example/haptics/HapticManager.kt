package com.example.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(private val context: Context) {

  var isVibrationEnabled: Boolean = true

  private val vibrator: Vibrator? by lazy {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Crisp, subtle tactile tick for tapping arrows, buttons, or rating stars.
   */
  fun tap() {
    if (!isVibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
      } else {
        vibrate(18, 90)
      }
    } catch (_: Exception) {
      vibrate(18, 90)
    }
  }

  /**
   * Satisfying release click when an arrow escapes successfully.
   */
  fun success() {
    if (!isVibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
      } else {
        vibrate(28, 170)
      }
    } catch (_: Exception) {
      vibrate(28, 170)
    }
  }

  /**
   * Sharp double-pulse collision feedback when an arrow is blocked.
   */
  fun error() {
    if (!isVibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(
          longArrayOf(0, 35, 45, 35),
          intArrayOf(0, 210, 0, 240),
          -1
        )
        vibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(90)
      }
    } catch (_: Exception) {
      vibrate(90, 200)
    }
  }

  /**
   * Grand triumphant haptic fanfare when a level is cleared.
   */
  fun victory() {
    if (!isVibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(
          longArrayOf(0, 40, 50, 45, 60, 90),
          intArrayOf(0, 160, 0, 210, 0, 255),
          -1
        )
        vibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(160)
      }
    } catch (_: Exception) {
      vibrate(160, 220)
    }
  }

  /**
   * Ultra-light micro-haptic for pinch-to-zoom step feedback.
   */
  fun zoomTick() {
    if (!isVibrationEnabled) return
    vibrate(8, 60)
  }

  private fun vibrate(durationMs: Long, amplitude: Int) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }
}
