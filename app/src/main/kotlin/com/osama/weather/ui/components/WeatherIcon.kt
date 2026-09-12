package com.osama.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.osama.weather.domain.model.WeatherCondition

/**
 * A small, self-contained glyph for one weather condition — used anywhere a
 * full animated background isn't appropriate (hourly strip, daily list,
 * search results). Deliberately drawn in the same visual language as the
 * full-screen background/particle system for a coherent identity throughout
 * the app, rather than pulling in a separate icon font or image set.
 */
@Composable
fun WeatherIcon(condition: WeatherCondition, isDay: Boolean, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        when (condition) {
            WeatherCondition.CLEAR, WeatherCondition.MOSTLY_CLEAR ->
                if (isDay) drawSun(s, 0.34f) else drawMoon(s)

            WeatherCondition.PARTLY_CLOUDY ->
                if (isDay) {
                    drawSun(s, 0.24f, offsetX = -s * 0.16f, offsetY = -s * 0.12f)
                    drawCloud(s, Color.White, offsetX = s * 0.08f, offsetY = s * 0.10f)
                } else {
                    drawMoon(s, scale = 0.85f, offsetX = -s * 0.14f, offsetY = -s * 0.14f)
                    drawCloud(s, Color(0xFFAEB7CC), offsetX = s * 0.08f, offsetY = s * 0.10f)
                }

            WeatherCondition.OVERCAST -> {
                drawCloud(s, Color(0xFFD7DEE5), offsetX = -s * 0.13f, offsetY = -s * 0.05f, scale = 0.75f)
                drawCloud(s, Color(0xFFF2F5F7), offsetX = s * 0.10f, offsetY = s * 0.10f)
            }

            WeatherCondition.FOG -> drawFogGlyph(s)

            WeatherCondition.DRIZZLE -> {
                drawCloud(s, Color(0xFFDCE3E9), offsetY = -s * 0.08f)
                drawRainLines(s, count = 3, offsetY = s * 0.22f, short = true)
            }

            WeatherCondition.RAIN, WeatherCondition.RAIN_SHOWERS -> {
                drawCloud(s, Color(0xFFE3E9EE), offsetY = -s * 0.1f)
                drawRainLines(s, count = 4, offsetY = s * 0.24f, short = false)
            }

            WeatherCondition.FREEZING_RAIN -> {
                drawCloud(s, Color(0xFFCFE3EC), offsetY = -s * 0.1f)
                drawRainLines(s, count = 3, offsetY = s * 0.24f, short = false, color = Color(0xFFBEEBFF))
            }

            WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> {
                drawCloud(s, Color(0xFFEFF4F8), offsetY = -s * 0.1f)
                drawSnowDots(s, offsetY = s * 0.26f)
            }

            WeatherCondition.THUNDERSTORM, WeatherCondition.THUNDERSTORM_HAIL -> {
                drawCloud(s, Color(0xFF6B7280), offsetY = -s * 0.12f)
                drawBolt(s)
            }
        }
    }
}

private fun DrawScope.drawSun(s: Float, radiusFrac: Float, offsetX: Float = 0f, offsetY: Float = 0f) {
    val center = Offset(this.size.width / 2f + offsetX, this.size.height / 2f + offsetY)
    val r = s * radiusFrac
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f)
        val inner = r * 1.35f
        val outer = r * 1.75f
        drawLine(
            color = Color(0xFFFFC857),
            start = Offset(center.x + (kotlin.math.cos(angle) * inner).toFloat(), center.y + (kotlin.math.sin(angle) * inner).toFloat()),
            end = Offset(center.x + (kotlin.math.cos(angle) * outer).toFloat(), center.y + (kotlin.math.sin(angle) * outer).toFloat()),
            strokeWidth = s * 0.045f,
            cap = StrokeCap.Round
        )
    }
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFFFF6D8), Color(0xFFFFC857)), center = center, radius = r),
        radius = r, center = center
    )
}

private fun DrawScope.drawMoon(s: Float, scale: Float = 1f, offsetX: Float = 0f, offsetY: Float = 0f) {
    val center = Offset(this.size.width / 2f + offsetX, this.size.height / 2f + offsetY)
    val r = s * 0.30f * scale
    drawCircle(Color(0xFFF6F1DE), radius = r, center = center)
    drawCircle(Color(0xFF16224A), radius = r * 0.98f, center = Offset(center.x + r * 0.55f, center.y - r * 0.35f))
}

private fun DrawScope.drawCloud(s: Float, color: Color, offsetX: Float = 0f, offsetY: Float = 0f, scale: Float = 1f) {
    val cx = this.size.width / 2f + offsetX
    val cy = this.size.height / 2f + offsetY
    val r = s * 0.20f * scale
    val puffs = listOf(
        Offset(cx - r * 1.1f, cy + r * 0.3f) to r * 0.62f,
        Offset(cx - r * 0.3f, cy - r * 0.25f) to r * 0.82f,
        Offset(cx + r * 0.5f, cy - r * 0.05f) to r * 0.7f,
        Offset(cx + r * 1.0f, cy + r * 0.28f) to r * 0.55f
    )
    val path = Path().apply {
        puffs.forEachIndexed { i, (pos, radius) ->
            addOval(androidx.compose.ui.geometry.Rect(center = pos, radius = radius))
        }
    }
    drawPath(path, color = color)
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - r * 1.15f, cy),
        size = androidx.compose.ui.geometry.Size(r * 2.3f, r * 0.6f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.3f, r * 0.3f)
    )
}

private fun DrawScope.drawRainLines(s: Float, count: Int, offsetY: Float, short: Boolean, color: Color = Color(0xFFB9E1FF)) {
    val cy = this.size.height / 2f + offsetY
    val len = if (short) s * 0.10f else s * 0.16f
    val spacing = s * 0.16f
    val startX = this.size.width / 2f - spacing * (count - 1) / 2f
    for (i in 0 until count) {
        val x = startX + i * spacing
        drawLine(
            color = color,
            start = Offset(x, cy),
            end = Offset(x - len * 0.3f, cy + len),
            strokeWidth = s * 0.035f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSnowDots(s: Float, offsetY: Float) {
    val cy = this.size.height / 2f + offsetY
    val spacing = s * 0.18f
    val startX = this.size.width / 2f - spacing
    for (i in 0..2) {
        val x = startX + i * spacing
        val y = cy + if (i % 2 == 0) 0f else s * 0.08f
        drawCircle(Color.White, radius = s * 0.035f, center = Offset(x, y))
    }
}

private fun DrawScope.drawBolt(s: Float) {
    val cx = this.size.width / 2f
    val cy = this.size.height / 2f + s * 0.16f
    val path = Path().apply {
        moveTo(cx + s * 0.06f, cy - s * 0.05f)
        lineTo(cx - s * 0.08f, cy + s * 0.12f)
        lineTo(cx + s * 0.01f, cy + s * 0.12f)
        lineTo(cx - s * 0.06f, cy + s * 0.30f)
        lineTo(cx + s * 0.10f, cy + s * 0.08f)
        lineTo(cx + s * 0.02f, cy + s * 0.08f)
        close()
    }
    drawPath(path, color = Color(0xFFFFD873))
}

private fun DrawScope.drawFogGlyph(s: Float) {
    val cy = this.size.height / 2f
    val widths = listOf(0.62f, 0.78f, 0.5f)
    widths.forEachIndexed { i, wFrac ->
        val y = cy + (i - 1) * s * 0.14f
        drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(this.size.width / 2f - s * wFrac / 2f, y),
            end = Offset(this.size.width / 2f + s * wFrac / 2f, y),
            strokeWidth = s * 0.06f,
            cap = StrokeCap.Round
        )
    }
}
