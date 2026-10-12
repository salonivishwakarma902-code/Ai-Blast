package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.LevelRepository
import com.example.engine.PuzzleSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Arrow Escape", appName)
  }

  @Test
  fun `verify level solvability`() {
    for (i in 1..20) {
      val level = LevelRepository.getLevel(i)
      val solution = PuzzleSolver.solveSequence(level.arrows, level.gridWidth, level.gridHeight)
      assertNotNull("Level $i must be solvable", solution)
      assertEquals("Solution length must match arrow count", level.arrows.size, solution!!.size)
    }
  }
}

