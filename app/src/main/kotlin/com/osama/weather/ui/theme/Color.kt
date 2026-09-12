package com.osama.weather.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * The app deliberately does NOT theme itself the way a typical Material app
 * does (light card / dark text, or vice-versa). The weather background IS
 * the color story — it changes completely with every condition and time of
 * day — so the UI chrome on top of it stays a single, consistent "glass on a
 * photograph" language: white text at a few fixed opacities, and softly
 * translucent surfaces, exactly like premium weather apps. Every screen uses
 * these same tokens so nothing ever clashes with the artwork behind it.
 */
object WeatherColors {
    // ---- Text -----------------------------------------------------------
    val OnBgPrimary = Color.White
    val OnBgSecondary = Color.White.copy(alpha = 0.78f)
    val OnBgTertiary = Color.White.copy(alpha = 0.55f)
    val OnBgDisabled = Color.White.copy(alpha = 0.34f)

    // ---- Glass surfaces ---------------------------------------------------
    val GlassSurface = Color.White.copy(alpha = 0.13f)
    val GlassSurfaceStrong = Color.White.copy(alpha = 0.20f)
    val GlassSurfaceSubtle = Color.White.copy(alpha = 0.08f)
    val GlassBorder = Color.White.copy(alpha = 0.20f)
    val GlassBorderStrong = Color.White.copy(alpha = 0.34f)

    // ---- Scrim (applied over busy artwork to guarantee text contrast) ----
    val ScrimTop = Color.Black.copy(alpha = 0.0f)
    val ScrimBottom = Color.Black.copy(alpha = 0.38f)

    // ---- Accent: warm gold, echoing the sun in the app icon --------------
    val Accent = Color(0xFFFFC857)
    val AccentStrong = Color(0xFFFFB300)
    val OnAccent = Color(0xFF1B1035)

    // ---- Semantic ----------------------------------------------------------
    val Error = Color(0xFFFF6B6B)
    val Success = Color(0xFF4CAF50)

    // ---- Fixed brand color (icon + splash + adaptive-icon background) ----
    val BrandDeepBlue = Color(0xFF0A3B93)
}
