package com.osama.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.osama.weather.domain.model.WeatherCondition
import kotlin.math.cos
import kotlin.math.sin

/**
 * A small, self-contained glyph for one weather condition — used anywhere a
 * full animated background isn't appropriate (hourly strip, daily list,
 * search results). Every shape uses layered gradients, soft shadows and
 * proper silhouettes (real teardrops, a real 6-point snowflake, a real
 * crescent moon) rather than flat single-tone primitives, so it reads as a
 * polished glyph at a glance rather than a placeholder icon.
 */
@Composable
fun WeatherIcon(condition: WeatherCondition, isDay: Boolean, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        when (condition) {
            WeatherCondition.CLEAR, WeatherCondition.MOSTLY_CLEAR ->
                if (isDay) drawSun(s, 0.30f) else drawMoonIcon(s, 0.30f)

            WeatherCondition.PARTLY_CLOUDY ->
                if (isDay) {
                    drawSun(s, 0.22f, offsetX = -s * 0.17f, offsetY = -s * 0.15f)
                    drawCloud(s, offsetX = s * 0.09f, offsetY = s * 0.12f, tone = CloudTone.Bright)
                } else {
                    drawMoonIcon(s, 0.20f, offsetX = -s * 0.17f, offsetY = -s * 0.15f)
                    drawCloud(s, offsetX = s * 0.09f, offsetY = s * 0.12f, tone = CloudTone.Night)
                }

            WeatherCondition.OVERCAST -> {
                drawCloud(s, offsetX = -s * 0.14f, offsetY = -s * 0.06f, scale = 0.72f, tone = CloudTone.Muted)
                drawCloud(s, offsetX = s * 0.11f, offsetY = s * 0.11f, tone = CloudTone.Bright)
            }

            WeatherCondition.FOG -> drawFogGlyph(s)

            WeatherCondition.DRIZZLE -> {
                drawCloud(s, offsetY = -s * 0.1f, tone = CloudTone.Muted)
                drawRainDrops(s, count = 3, offsetY = s * 0.2f, short = true)
            }

            WeatherCondition.RAIN, WeatherCondition.RAIN_SHOWERS -> {
                drawCloud(s, offsetY = -s * 0.12f, tone = CloudTone.Muted)
                drawRainDrops(s, count = 4, offsetY = s * 0.22f, short = false)
            }

            WeatherCondition.FREEZING_RAIN -> {
                drawCloud(s, offsetY = -s * 0.12f, tone = CloudTone.Icy)
                drawRainDrops(s, count = 3, offsetY = s * 0.22f, short = false, icy = true)
            }

            WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> {
                drawCloud(s, offsetY = -s * 0.12f, tone = CloudTone.Bright)
                drawSnowflakes(s, offsetY = s * 0.24f)
            }

            WeatherCondition.THUNDERSTORM, WeatherCondition.THUNDERSTORM_HAIL -> {
                drawCloud(s, offsetY = -s * 0.14f, tone = CloudTone.Storm)
                drawBolt(s)
            }
        }
    }
}

private enum class CloudTone { Bright, Muted, Night, Icy, Storm }

private fun DrawScope.drawSun(s: Float, radiusFrac: Float, offsetX: Float = 0f, offsetY: Float = 0f) {
    val center = Offset(this.size.width / 2f + offsetX, this.size.height / 2f + offsetY)
    val r = s * radiusFrac

    // Soft outer glow, two layers for a richer falloff than a single radial gradient.
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFFFE9A8).copy(alpha = 0.30f), Color.Transparent),
            center = center, radius = r * 2.6f
        ),
        radius = r * 2.6f, center = center
    )

    // Tapered rays: small kite-shaped paths, not flat lines, so they catch light like real rays.
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f)
        val dir = Offset(cos(angle).toFloat(), sin(angle).toFloat())
        val perp = Offset(-dir.y, dir.x)
        val inner = r * 1.18f
        val outer = r * 1.68f
        val halfWidth = r * 0.09f
        val path = Path().apply {
            moveTo(center.x + dir.x * inner - perp.x * halfWidth, center.y + dir.y * inner - perp.y * halfWidth)
            lineTo(center.x + dir.x * outer, center.y + dir.y * outer)
            lineTo(center.x + dir.x * inner + perp.x * halfWidth, center.y + dir.y * inner + perp.y * halfWidth)
            close()
        }
        drawPath(path, color = Color(0xFFFFDA7A).copy(alpha = 0.8f))
    }

    // Core disc with a bright highlight offset toward the upper-left, like real sunlit spheres.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFBEF), Color(0xFFFFE28A), Color(0xFFFFC857)),
            center = Offset(center.x - r * 0.3f, center.y - r * 0.3f),
            radius = r * 1.7f
        ),
        radius = r, center = center
    )
}

private fun DrawScope.drawMoonIcon(s: Float, radiusFrac: Float, offsetX: Float = 0f, offsetY: Float = 0f) {
    val center = Offset(this.size.width / 2f + offsetX, this.size.height / 2f + offsetY)
    val r = s * radiusFrac
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFEFE6C8).copy(alpha = 0.25f), Color.Transparent), center = center, radius = r * 2.2f),
        radius = r * 2.2f, center = center
    )
    // A gentle waxing-gibbous glyph reads clearly at small icon sizes.
    drawMoonPhase(center = center, radius = r, phaseFraction = 0.68f)
}

private fun DrawScope.drawCloud(s: Float, offsetX: Float = 0f, offsetY: Float = 0f, scale: Float = 1f, tone: CloudTone) {
    val cx = this.size.width / 2f + offsetX
    val cy = this.size.height / 2f + offsetY
    val r = s * 0.20f * scale

    val (top, bottom) = when (tone) {
        CloudTone.Bright -> Color(0xFFFFFFFF) to Color(0xFFDCE6EF)
        CloudTone.Muted -> Color(0xFFEDF2F6) to Color(0xFFB9C6D2)
        CloudTone.Night -> Color(0xFFB7C0D6) to Color(0xFF7C88A3)
        CloudTone.Icy -> Color(0xFFE4F3FA) to Color(0xFFAED7E8)
        CloudTone.Storm -> Color(0xFF7B8291) to Color(0xFF454B58)
    }

    val puffs = listOf(
        Offset(cx - r * 1.15f, cy + r * 0.32f) to r * 0.58f,
        Offset(cx - r * 0.55f, cy + r * 0.38f) to r * 0.62f,
        Offset(cx - r * 0.3f, cy - r * 0.25f) to r * 0.82f,
        Offset(cx + r * 0.5f, cy - r * 0.05f) to r * 0.7f,
        Offset(cx + 0.65f * r, cy + r * 0.36f) to r * 0.6f,
        Offset(cx + r * 1.1f, cy + r * 0.32f) to r * 0.5f
    )
    val path = Path().apply {
        puffs.forEach { (pos, radius) -> addOval(Rect(pos, radius)) }
    }

    // Soft drop shadow first, so the cloud reads as sitting above it.
    drawPath(
        path = path,
        brush = Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent))
    )
    drawPath(
        path = path,
        brush = Brush.verticalGradient(listOf(top, bottom), startY = cy - r, endY = cy + r * 0.6f)
    )
    // Subtle top-left highlight for volume.
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), center = Offset(cx - r * 0.2f, cy - r * 0.3f), radius = r * 0.9f),
        radius = r * 0.9f, center = Offset(cx - r * 0.2f, cy - r * 0.3f)
    )
}

private fun Rect(center: Offset, radius: Float) = androidx.compose.ui.geometry.Rect(
    center.x - radius, center.y - radius, center.x + radius, center.y + radius
)

private fun DrawScope.drawRainDrops(s: Float, count: Int, offsetY: Float, short: Boolean, icy: Boolean = false) {
    val cy = this.size.height / 2f + offsetY
    val len = if (short) s * 0.14f else s * 0.19f
    val spacing = s * 0.17f
    val startX = this.size.width / 2f - spacing * (count - 1) / 2f
    val topColor = if (icy) Color(0xFFCDEBFA) else Color(0xFF9AD4F5)
    val bottomColor = if (icy) Color(0xFF7FC4E8) else Color(0xFF3E9BDC)

    for (i in 0 until count) {
        val x = startX + i * spacing
        val tipX = x - len * 0.22f
        val path = Path().apply {
            moveTo(x, cy)
            quadraticTo(x + len * 0.32f, cy + len * 0.55f, tipX, cy + len)
            quadraticTo(x - len * 0.32f, cy + len * 0.55f, x, cy)
            close()
        }
        drawPath(path, brush = Brush.verticalGradient(listOf(topColor, bottomColor), startY = cy, endY = cy + len))
    }
}

private fun DrawScope.drawSnowflakes(s: Float, offsetY: Float) {
    val cy = this.size.height / 2f + offsetY
    val spacing = s * 0.19f
    val startX = this.size.width / 2f - spacing

    // One detailed 6-point flake in the center, soft dots flanking it — mixing
    // scale/detail reads as more natural than a uniform row of identical dots.
    drawSixPointFlake(Offset(startX + spacing, cy - s * 0.02f), s * 0.075f)
    drawCircle(Color.White.copy(alpha = 0.92f), radius = s * 0.032f, center = Offset(startX, cy + s * 0.05f))
    drawCircle(Color.White.copy(alpha = 0.92f), radius = s * 0.032f, center = Offset(startX + spacing * 2, cy + s * 0.05f))
}

private fun DrawScope.drawSixPointFlake(center: Offset, armLength: Float) {
    for (i in 0 until 6) {
        val angle = (i * 60f) * (Math.PI / 180f)
        val dir = Offset(cos(angle).toFloat(), sin(angle).toFloat())
        val end = Offset(center.x + dir.x * armLength, center.y + dir.y * armLength)
        drawLine(Color.White, start = center, end = end, strokeWidth = armLength * 0.16f, cap = StrokeCap.Round)
        // small side branches for a snowflake silhouette instead of a plain asterisk
        val branchPoint = Offset(center.x + dir.x * armLength * 0.6f, center.y + dir.y * armLength * 0.6f)
        val perp = Offset(-dir.y, dir.x)
        val branchLen = armLength * 0.32f
        drawLine(Color.White, branchPoint, Offset(branchPoint.x + perp.x * branchLen, branchPoint.y + perp.y * branchLen), strokeWidth = armLength * 0.12f, cap = StrokeCap.Round)
        drawLine(Color.White, branchPoint, Offset(branchPoint.x - perp.x * branchLen, branchPoint.y - perp.y * branchLen), strokeWidth = armLength * 0.12f, cap = StrokeCap.Round)
    }
    drawCircle(Color.White, radius = armLength * 0.18f, center = center)
}

private fun DrawScope.drawBolt(s: Float) {
    val cx = this.size.width / 2f
    val cy = this.size.height / 2f + s * 0.16f
    val path = Path().apply {
        moveTo(cx + s * 0.07f, cy - s * 0.06f)
        lineTo(cx - s * 0.09f, cy + s * 0.13f)
        lineTo(cx + s * 0.01f, cy + s * 0.13f)
        lineTo(cx - s * 0.07f, cy + s * 0.32f)
        lineTo(cx + s * 0.11f, cy + s * 0.09f)
        lineTo(cx + s * 0.02f, cy + s * 0.09f)
        close()
    }
    // Glow first, then the crisp bolt on top.
    drawPath(path, color = Color(0xFFFFD873).copy(alpha = 0.45f), style = Stroke(width = s * 0.09f, join = StrokeJoin.Round))
    drawPath(path, color = Color(0xFFFFD873))
}

private fun DrawScope.drawFogGlyph(s: Float) {
    val cy = this.size.height / 2f
    val widths = listOf(0.62f, 0.78f, 0.5f)
    widths.forEachIndexed { i, wFrac ->
        val y = cy + (i - 1) * s * 0.16f
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0f))
            ),
            start = Offset(this.size.width / 2f - s * wFrac / 2f, y),
            end = Offset(this.size.width / 2f + s * wFrac / 2f, y),
            strokeWidth = s * 0.065f,
            cap = StrokeCap.Round
        )
    }
}
