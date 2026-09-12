package com.osama.weather.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.osama.weather.R

/**
 * Cairo is a single variable font (weight axis 200–1000) bundled locally at
 * res/font/cairo.ttf — no network font-provider dependency, and full Arabic +
 * Latin coverage in one family (numerals, °C/°F, city names in either
 * script). Each weight below points at the SAME file with a different
 * FontVariation, which is the standard way to use a variable font in Compose.
 */
private fun cairo(weight: Int, base: FontWeight) = Font(
    resId = R.font.cairo,
    weight = base,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

val CairoFontFamily = FontFamily(
    cairo(200, FontWeight.ExtraLight),
    cairo(300, FontWeight.Light),
    cairo(400, FontWeight.Normal),
    cairo(500, FontWeight.Medium),
    cairo(600, FontWeight.SemiBold),
    cairo(700, FontWeight.Bold),
    cairo(800, FontWeight.ExtraBold),
    cairo(900, FontWeight.Black)
)

// A restrained, disciplined scale: most of the app leans on Regular/Medium/
// SemiBold, with Bold reserved for the few moments that should really stand
// out (the hero temperature, the location name).
val WeatherAppTypography = Typography(
    displayLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Light, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 33.sp),
    headlineSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 29.sp),
    titleLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, lineHeight = 25.sp),
    titleMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.3.sp)
)

/** Styles beyond Material3's standard scale — the giant hero temperature and its unit glyph. */
object ExtraTypography {
    val heroTemperature = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 108.sp,
        lineHeight = 108.sp,
        letterSpacing = (-2).sp
    )
    val heroTemperatureCompact = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 80.sp,
        lineHeight = 84.sp,
        letterSpacing = (-1.5).sp
    )
}
