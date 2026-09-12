package com.osama.weather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection

private val WeatherColorScheme = darkColorScheme(
    primary = WeatherColors.Accent,
    onPrimary = WeatherColors.OnAccent,
    secondary = WeatherColors.Accent,
    onSecondary = WeatherColors.OnAccent,
    background = WeatherColors.BrandDeepBlue,
    onBackground = WeatherColors.OnBgPrimary,
    surface = WeatherColors.GlassSurface,
    onSurface = WeatherColors.OnBgPrimary,
    surfaceVariant = WeatherColors.GlassSurfaceStrong,
    onSurfaceVariant = WeatherColors.OnBgSecondary,
    outline = WeatherColors.GlassBorder,
    error = WeatherColors.Error,
    onError = WeatherColors.OnBgPrimary
)

/**
 * The app always renders right-to-left (the entire UI is Arabic by design,
 * regardless of the device's system language), and always uses the single
 * "glass over dynamic sky" palette rather than following system light/dark —
 * the weather background itself already carries the light/dark story.
 */
@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    // isSystemInDarkTheme is intentionally unused for color decisions — kept
    // only so this composable stays a normal, extensible entry point.
    isSystemInDarkTheme()

    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = WeatherColorScheme,
            typography = WeatherAppTypography,
            content = content
        )
    }
}
