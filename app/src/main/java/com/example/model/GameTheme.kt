package com.example.model

import androidx.compose.ui.graphics.Color

data class GameTheme(
  val id: String,
  val name: String,
  val priceCoins: Int = 0,
  val isDark: Boolean = false,
  val backgroundGradientStart: Color,
  val backgroundGradientEnd: Color,
  val boardBackground: Color,
  val boardBorder: Color,
  val gridLineColor: Color,
  val arrowBodyColor: Color,
  val arrowHeadColor: Color,
  val arrowHighlightColor: Color,
  val primaryAccent: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val cardBackground: Color
) {
  companion object {
    val CLASSIC_WHITE = GameTheme(
      id = "classic_white",
      name = "Classic Clean",
      priceCoins = 0,
      isDark = false,
      backgroundGradientStart = Color(0xFFF8FAFC),
      backgroundGradientEnd = Color(0xFFEFF6FF),
      boardBackground = Color(0xFFFFFFFF),
      boardBorder = Color(0xFFE2E8F0),
      gridLineColor = Color(0xFFE2E8F0),
      arrowBodyColor = Color(0xFF1E293B),
      arrowHeadColor = Color(0xFF0284C7),
      arrowHighlightColor = Color(0xFFFBBF24),
      primaryAccent = Color(0xFF0284C7),
      textPrimary = Color(0xFF0F172A),
      textSecondary = Color(0xFF64748B),
      cardBackground = Color(0xFFFFFFFF)
    )

    val MIDNIGHT_NAVY = GameTheme(
      id = "midnight_navy",
      name = "Midnight OLED",
      priceCoins = 150,
      isDark = true,
      backgroundGradientStart = Color(0xFF090D16),
      backgroundGradientEnd = Color(0xFF0F172A),
      boardBackground = Color(0xFF131D33),
      boardBorder = Color(0xFF1E293B),
      gridLineColor = Color(0xFF1E293B),
      arrowBodyColor = Color(0xFF38BDF8),
      arrowHeadColor = Color(0xFF00F2FE),
      arrowHighlightColor = Color(0xFFFACC15),
      primaryAccent = Color(0xFF38BDF8),
      textPrimary = Color(0xFFF8FAFC),
      textSecondary = Color(0xFF94A3B8),
      cardBackground = Color(0xFF182238)
    )

    val OCEAN_BREEZE = GameTheme(
      id = "ocean_breeze",
      name = "Ocean Turquoise",
      priceCoins = 200,
      isDark = false,
      backgroundGradientStart = Color(0xFFF0FDFA),
      backgroundGradientEnd = Color(0xFFCCFBF1),
      boardBackground = Color(0xFFFFFFFF),
      boardBorder = Color(0xFF99F6E4),
      gridLineColor = Color(0xFFCCFBF1),
      arrowBodyColor = Color(0xFF0F766E),
      arrowHeadColor = Color(0xFF0D9488),
      arrowHighlightColor = Color(0xFFF59E0B),
      primaryAccent = Color(0xFF0D9488),
      textPrimary = Color(0xFF134E4A),
      textSecondary = Color(0xFF5EEAD4),
      cardBackground = Color(0xFFFFFFFF)
    )

    val LAVENDER_ZEN = GameTheme(
      id = "lavender_zen",
      name = "Lavender Zen",
      priceCoins = 250,
      isDark = false,
      backgroundGradientStart = Color(0xFFFAF5FF),
      backgroundGradientEnd = Color(0xFFF3E8FF),
      boardBackground = Color(0xFFFFFFFF),
      boardBorder = Color(0xFFE9D5FF),
      gridLineColor = Color(0xFFF3E8FF),
      arrowBodyColor = Color(0xFF6B21A8),
      arrowHeadColor = Color(0xFF9333EA),
      arrowHighlightColor = Color(0xFFFBBF24),
      primaryAccent = Color(0xFF9333EA),
      textPrimary = Color(0xFF3B0764),
      textSecondary = Color(0xFF7E22CE),
      cardBackground = Color(0xFFFFFFFF)
    )

    val SUNSET_GLOW = GameTheme(
      id = "sunset_glow",
      name = "Sunset Amber",
      priceCoins = 300,
      isDark = false,
      backgroundGradientStart = Color(0xFFFFFBEB),
      backgroundGradientEnd = Color(0xFFFEF3C7),
      boardBackground = Color(0xFFFFFFFF),
      boardBorder = Color(0xFFFDE68A),
      gridLineColor = Color(0xFFFEF3C7),
      arrowBodyColor = Color(0xFFB45309),
      arrowHeadColor = Color(0xFFEA580C),
      arrowHighlightColor = Color(0xFF10B981),
      primaryAccent = Color(0xFFEA580C),
      textPrimary = Color(0xFF78350F),
      textSecondary = Color(0xFFD97706),
      cardBackground = Color(0xFFFFFFFF)
    )

    val CYBER_NEON = GameTheme(
      id = "cyber_neon",
      name = "Cyber Neon",
      priceCoins = 400,
      isDark = true,
      backgroundGradientStart = Color(0xFF030712),
      backgroundGradientEnd = Color(0xFF111827),
      boardBackground = Color(0xFF000000),
      boardBorder = Color(0xFF22C55E),
      gridLineColor = Color(0xFF1F2937),
      arrowBodyColor = Color(0xFF22C55E),
      arrowHeadColor = Color(0xFFA855F7),
      arrowHighlightColor = Color(0xFFE11D48),
      primaryAccent = Color(0xFF22C55E),
      textPrimary = Color(0xFFF9FAFB),
      textSecondary = Color(0xFF86EFAC),
      cardBackground = Color(0xFF1F2937)
    )

    val ALL_THEMES = listOf(
      CLASSIC_WHITE,
      MIDNIGHT_NAVY,
      OCEAN_BREEZE,
      LAVENDER_ZEN,
      SUNSET_GLOW,
      CYBER_NEON
    )
  }
}
