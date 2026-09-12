package com.osama.weather.ui.components.background

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.osama.weather.domain.model.TimeOfDay
import com.osama.weather.domain.model.WeatherCondition

/**
 * Renders the full animated sky for one (condition, time-of-day) pair: the
 * hand-tuned gradient from [WeatherGradients], layered clouds/precipitation/
 * fog/lightning/sun/moon as appropriate for that exact condition, and a
 * bottom scrim that guarantees the white UI text sitting on top always has
 * enough contrast, no matter how pale or dark the sky itself is.
 *
 * Decorative elements (sun, moon, clouds) are positioned in a fixed LTR frame
 * so their placement never mirrors with the app's RTL text direction.
 */
@Composable
fun WeatherBackground(
    condition: WeatherCondition,
    timeOfDay: TimeOfDay,
    moonPhaseFraction: Float,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = condition to timeOfDay,
        animationSpec = tween(900),
        label = "weatherBackground"
    ) { (cond, time) ->
        Box(modifier = modifier.fillMaxSize()) {
            // Base sky gradient.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(WeatherGradients.colorsFor(cond, time)))
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val celestialSize = (maxWidth.value * 0.42f).dp

                    when (time) {
                        TimeOfDay.NIGHT -> {
                            MoonWithStars(
                                modifier = Modifier.fillMaxSize(),
                                phaseFraction = moonPhaseFraction
                            )
                        }
                        else -> {
                            if (cond == WeatherCondition.CLEAR || cond == WeatherCondition.MOSTLY_CLEAR || cond == WeatherCondition.PARTLY_CLOUDY) {
                                SunGlow(
                                    modifier = Modifier
                                        .size(celestialSize)
                                        .align(Alignment.TopEnd)
                                        .offset(x = celestialSize * 0.28f, y = -celestialSize * 0.22f)
                                )
                            }
                        }
                    }

                    CloudsForCondition(cond, time, Modifier.fillMaxSize())
                }
            }

            PrecipitationForCondition(cond, Modifier.fillMaxSize())

            if (cond == WeatherCondition.FOG) {
                FogLayer(Modifier.fillMaxSize())
            }

            if (cond == WeatherCondition.THUNDERSTORM || cond == WeatherCondition.THUNDERSTORM_HAIL) {
                LightningFlash(Modifier.fillMaxSize())
            }

            // Contrast scrim — always present, strongest at the bottom where the
            // scrollable content sits.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.45f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.42f)
                        )
                    )
            )
        }
    }
}

@Composable
private fun CloudsForCondition(condition: WeatherCondition, time: TimeOfDay, modifier: Modifier) {
    val nightTint = if (time == TimeOfDay.NIGHT) Color(0xFF8B93A8) else Color.White
    when (condition) {
        WeatherCondition.PARTLY_CLOUDY ->
            CloudLayer(modifier, cloudCount = 3, baseAlpha = 0.32f, tint = nightTint)
        WeatherCondition.OVERCAST ->
            CloudLayer(modifier, cloudCount = 6, baseAlpha = 0.55f, tint = nightTint)
        WeatherCondition.DRIZZLE ->
            CloudLayer(modifier, cloudCount = 5, baseAlpha = 0.48f, tint = nightTint)
        WeatherCondition.RAIN, WeatherCondition.FREEZING_RAIN ->
            CloudLayer(modifier, cloudCount = 6, baseAlpha = 0.55f, tint = nightTint)
        WeatherCondition.RAIN_SHOWERS ->
            CloudLayer(modifier, cloudCount = 4, baseAlpha = 0.38f, tint = nightTint)
        WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS ->
            CloudLayer(modifier, cloudCount = 5, baseAlpha = 0.5f, tint = Color(0xFFEFF4F8))
        WeatherCondition.THUNDERSTORM, WeatherCondition.THUNDERSTORM_HAIL ->
            CloudLayer(modifier, cloudCount = 7, baseAlpha = 0.62f, tint = Color(0xFF2E323D))
        else -> Unit
    }
}

@Composable
private fun PrecipitationForCondition(condition: WeatherCondition, modifier: Modifier) {
    when (condition) {
        WeatherCondition.DRIZZLE -> RainEffect(modifier, dropCount = 55, speedFactor = 0.55f)
        WeatherCondition.RAIN -> RainEffect(modifier, dropCount = 110, speedFactor = 1.0f)
        WeatherCondition.FREEZING_RAIN -> RainEffect(modifier, dropCount = 85, speedFactor = 0.8f)
        WeatherCondition.RAIN_SHOWERS -> RainEffect(modifier, dropCount = 100, speedFactor = 1.1f)
        WeatherCondition.SNOW -> SnowEffect(modifier, flakeCount = 80, speedFactor = 1.0f)
        WeatherCondition.SNOW_SHOWERS -> SnowEffect(modifier, flakeCount = 105, speedFactor = 1.2f)
        WeatherCondition.THUNDERSTORM -> RainEffect(modifier, dropCount = 150, speedFactor = 1.35f)
        WeatherCondition.THUNDERSTORM_HAIL -> {
            RainEffect(modifier, dropCount = 120, speedFactor = 1.3f)
            HailEffect(modifier, count = 45)
        }
        else -> Unit
    }
}
