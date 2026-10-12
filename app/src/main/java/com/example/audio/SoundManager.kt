package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

class SoundManager {

  var isSoundEnabled: Boolean = true
    set(value) {
      field = value
      if (value) startBgm() else stopBgm()
    }

  private val scope = CoroutineScope(Dispatchers.Default)
  private var bgmTrack: AudioTrack? = null
  private var isBgmPlaying = false

  init {
    startBgm()
  }

  fun startBgm() {
    if (!isSoundEnabled || isBgmPlaying) return
    isBgmPlaying = true
    scope.launch(Dispatchers.IO) {
      try {
        val sampleRate = 44100
        val durationMs = 12000 // 12 seconds warm ambient chord loop
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val sample = ShortArray(numSamples)

        // Warm, lush pentatonic progression: Cmaj9 -> Am9 -> Fmaj7 -> Gsus4
        val chords = listOf(
          listOf(130.81, 196.00, 246.94, 293.66), // Cmaj9
          listOf(110.00, 164.81, 220.00, 261.63), // Am7
          listOf(87.31, 130.81, 174.61, 220.00),  // Fmaj7
          listOf(98.00, 146.83, 196.00, 261.63)   // Gsus4
        )

        for (i in 0 until numSamples) {
          val time = i.toDouble() / sampleRate
          val chordIndex = ((time / 3.0).toInt()) % chords.size
          val currentChord = chords[chordIndex]
          val chordTime = time % 3.0

          // Smooth cosine cross-fade envelope
          val env = sin(Math.PI * (chordTime / 3.0))

          var wave = 0.0
          for (freq in currentChord) {
            val shimmer = sin(2.0 * Math.PI * 0.25 * time) * 0.015
            wave += sin(2.0 * Math.PI * (freq + shimmer) * time)
            // Soft sub-harmonic octave for richness
            wave += sin(Math.PI * freq * time) * 0.35
          }
          wave = (wave / currentChord.size) * env

          sample[i] = (wave * Short.MAX_VALUE * 0.08f).toInt().toShort()
        }

        bgmTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(sample.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        bgmTrack?.write(sample, 0, sample.size)
        bgmTrack?.setLoopPoints(0, numSamples, -1)
        bgmTrack?.play()
      } catch (_: Exception) {
        isBgmPlaying = false
      }
    }
  }

  fun stopBgm() {
    isBgmPlaying = false
    try {
      bgmTrack?.stop()
      bgmTrack?.release()
      bgmTrack = null
    } catch (_: Exception) {}
  }

  /**
   * Crisp, tactile acoustic glass-marble tap click with rapid exponential decay.
   */
  fun playTap() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(
        durationMs = 35,
        volume = 0.45f
      ) { time ->
        // 800Hz transient click blending into 1200Hz resonance
        val env = exp(-time * 120.0)
        sin(2.0 * Math.PI * 880.0 * time) * env +
          sin(2.0 * Math.PI * 1760.0 * time) * (env * 0.4)
      }
    }
  }

  /**
   * Silky smooth aerodynamic whoosh glide with low-pass rise.
   */
  fun playEscape() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(
        durationMs = 180,
        volume = 0.55f
      ) { time ->
        val tNorm = (time / 0.18).coerceIn(0.0, 1.0)
        val curFreq = 340.0 + (tNorm * tNorm) * 680.0
        val env = sin(Math.PI * tNorm)
        sin(2.0 * Math.PI * curFreq * time) * env * 0.8 +
          sin(4.0 * Math.PI * curFreq * time) * env * 0.2
      }
    }
  }

  /**
   * Resonant low-frequency wooden block / elastic bumper thud for blocked arrows.
   */
  fun playBlocked() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(
        durationMs = 95,
        volume = 0.65f
      ) { time ->
        val env = exp(-time * 38.0)
        // 130Hz fundamental with 75Hz sub-thud
        sin(2.0 * Math.PI * 130.0 * time) * env +
          sin(2.0 * Math.PI * 75.0 * time) * (env * 0.7)
      }
    }
  }

  /**
   * Euphoric sparkling chime arpeggio: C5 -> E5 -> G5 -> C6
   */
  fun playVictory() {
    if (!isSoundEnabled) return
    scope.launch {
      val notes = listOf(523.25, 659.25, 783.99, 1046.50)
      for (n in notes) {
        playNaturalSample(durationMs = 110, volume = 0.6f) { time ->
          val env = exp(-time * 16.0)
          sin(2.0 * Math.PI * n * time) * env +
            sin(4.0 * Math.PI * n * time) * (env * 0.25)
        }
      }
    }
  }

  fun playCoin() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(durationMs = 60, volume = 0.5f) { time ->
        val env = exp(-time * 40.0)
        sin(2.0 * Math.PI * 987.77 * time) * env
      }
      playNaturalSample(durationMs = 100, volume = 0.55f) { time ->
        val env = exp(-time * 25.0)
        sin(2.0 * Math.PI * 1318.51 * time) * env
      }
    }
  }

  fun playHint() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(durationMs = 160, volume = 0.45f) { time ->
        val tNorm = (time / 0.16).coerceIn(0.0, 1.0)
        val curFreq = 520.0 + tNorm * 780.0
        val env = sin(Math.PI * tNorm)
        sin(2.0 * Math.PI * curFreq * time) * env
      }
    }
  }

  fun playHeartLost() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(durationMs = 150, volume = 0.5f) { time ->
        val tNorm = (time / 0.15).coerceIn(0.0, 1.0)
        val curFreq = 420.0 - tNorm * 220.0
        val env = sin(Math.PI * tNorm)
        sin(2.0 * Math.PI * curFreq * time) * env
      }
    }
  }

  fun playZoom() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(durationMs = 20, volume = 0.28f) { time ->
        val env = exp(-time * 180.0)
        sin(2.0 * Math.PI * 960.0 * time) * env
      }
    }
  }

  fun playFeedbackSuccess() {
    if (!isSoundEnabled) return
    scope.launch {
      playNaturalSample(durationMs = 80, volume = 0.5f) { time ->
        sin(2.0 * Math.PI * 523.25 * time) * exp(-time * 20.0)
      }
      playNaturalSample(durationMs = 120, volume = 0.55f) { time ->
        sin(2.0 * Math.PI * 783.99 * time) * exp(-time * 15.0)
      }
    }
  }

  private fun playNaturalSample(
    durationMs: Int,
    volume: Float,
    generator: (Double) -> Double
  ) {
    try {
      val sampleRate = 44100
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
      val sample = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val time = i.toDouble() / sampleRate
        val waveVal = generator(time).coerceIn(-1.0, 1.0)
        sample[i] = (waveVal * Short.MAX_VALUE * volume).toInt().toShort()
      }

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(sample.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(sample, 0, sample.size)
      track.play()
    } catch (_: Exception) {}
  }
}
