package com.osama.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SunMoonCard(
    sunriseIso: String,
    sunsetIso: String,
    nowEpochSeconds: Long,
    sunriseEpochSeconds: Long,
    sunsetEpochSeconds: Long,
    moonPhaseFraction: Double?,
    moonriseIso: String?,
    moonsetIso: String?,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sun_and_moon),
            style = MaterialTheme.typography.titleMedium,
            color = WeatherColors.OnBgSecondary
        )
        Spacer(Modifier.height(Spacing.sm))

        SunArc(
            nowEpochSeconds = nowEpochSeconds,
            sunriseEpochSeconds = sunriseEpochSeconds,
            sunsetEpochSeconds = sunsetEpochSeconds,
            modifier = Modifier.fillMaxWidth().height(80.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            LabeledTime(stringResource(R.string.sunrise), DateTimeUtils.clockTime(sunriseIso), Modifier.weight(1f))
            LabeledTime(stringResource(R.string.sunset), DateTimeUtils.clockTime(sunsetIso), Modifier.weight(1f))
        }

        if (moonPhaseFraction != null) {
            Spacer(Modifier.height(Spacing.md))
            HorizontalDivider(color = WeatherColors.GlassBorder)
            Spacer(Modifier.height(Spacing.md))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                MoonPhaseGlyph(phaseFraction = moonPhaseFraction.toFloat(), size = 34.dp)
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = moonPhaseLabel(moonPhaseFraction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.OnBgPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (moonriseIso != null) LabeledTime(stringResource(R.string.moonrise), DateTimeUtils.clockTime(moonriseIso), Modifier.weight(1f))
                if (moonsetIso != null) LabeledTime(stringResource(R.string.moonset), DateTimeUtils.clockTime(moonsetIso), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LabeledTime(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = WeatherColors.OnBgTertiary)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgPrimary)
    }
}

@Composable
private fun SunArc(
    nowEpochSeconds: Long,
    sunriseEpochSeconds: Long,
    sunsetEpochSeconds: Long,
    modifier: Modifier = Modifier
) {
    val dayLength = (sunsetEpochSeconds - sunriseEpochSeconds).coerceAtLeast(1L)
    val progress = ((nowEpochSeconds - sunriseEpochSeconds).toFloat() / dayLength.toFloat()).coerceIn(0f, 1f)
    val isOutsideDaylight = nowEpochSeconds < sunriseEpochSeconds || nowEpochSeconds > sunsetEpochSeconds

    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val arcTop = size.height * 0.15f
        val arcWidth = size.width * 0.9f
        val arcHeight = size.height * 1.5f
        val left = (size.width - arcWidth) / 2f

        drawArc(
            color = Color.White.copy(alpha = 0.22f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(left, arcTop),
            size = Size(arcWidth, arcHeight),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        if (!isOutsideDaylight) {
            drawArc(
                brush = Brush.horizontalGradient(listOf(WeatherColors.Accent, Color(0xFFFFF3C4))),
                startAngle = 180f,
                sweepAngle = 180f * progress,
                useCenter = false,
                topLeft = Offset(left, arcTop),
                size = Size(arcWidth, arcHeight),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val angleRad = Math.toRadians((180f + 180f * progress).toDouble())
            val cx = left + arcWidth / 2f
            val cy = arcTop + arcHeight / 2f
            val rx = arcWidth / 2f
            val ry = arcHeight / 2f
            val sunX = cx + rx * cos(angleRad).toFloat()
            val sunY = cy + ry * sin(angleRad).toFloat()
            drawCircle(Color(0xFFFFF6D8), radius = strokeWidth * 1.8f, center = Offset(sunX, sunY))
        }
    }
}

@Composable
private fun MoonPhaseGlyph(phaseFraction: Float, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val r = this.size.minDimension / 2.2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(Color(0xFFF6F1DE), radius = r, center = center)
        val shift = ((phaseFraction - 0.5f) * 2f).coerceIn(-1f, 1f)
        if (abs(shift) > 0.02f) {
            drawCircle(
                Color(0xFF16224A),
                radius = r * 1.02f,
                center = Offset(center.x + shift * r * 1.9f, center.y)
            )
        }
    }
}

@Composable
private fun moonPhaseLabel(phase: Double): String = stringResource(
    when {
        phase < 0.03 || phase > 0.97 -> R.string.moon_new
        phase < 0.22 -> R.string.moon_waxing_crescent
        phase < 0.28 -> R.string.moon_first_quarter
        phase < 0.47 -> R.string.moon_waxing_gibbous
        phase < 0.53 -> R.string.moon_full
        phase < 0.72 -> R.string.moon_waning_gibbous
        phase < 0.78 -> R.string.moon_last_quarter
        else -> R.string.moon_waning_crescent
    }
)
