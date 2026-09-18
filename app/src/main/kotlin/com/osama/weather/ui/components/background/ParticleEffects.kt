package com.osama.weather.ui.components.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.osama.weather.ui.components.drawMoonPhase
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// =============================================================== RAIN =====

private data class Drop(val x: Float, val yStart: Float, val length: Float, val speed: Float, val alpha: Float, val thickness: Float)

@Composable
fun RainEffect(modifier: Modifier = Modifier, dropCount: Int = 110, speedFactor: Float = 1f) {
    val drops = remember(dropCount) {
        List(dropCount) {
            Drop(
                x = Random.nextFloat(),
                yStart = Random.nextFloat(),
                length = Random.nextFloat() * 14f + 10f,
                speed = Random.nextFloat() * 0.5f + 0.7f,
                alpha = Random.nextFloat() * 0.35f + 0.2f,
                thickness = Random.nextFloat() * 1.1f + 0.8f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "rain")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650, easing = LinearEasing)),
        label = "rainProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        drops.forEach { d ->
            val travel = (d.yStart + progress * d.speed * speedFactor) % 1.05f
            val y = travel * (h + 60f) - 30f
            val x = d.x * w
            drawLine(
                color = Color.White.copy(alpha = d.alpha),
                start = Offset(x, y),
                end = Offset(x - 3f, y + d.length),
                strokeWidth = d.thickness,
                cap = StrokeCap.Round
            )
        }
    }
}

// =============================================================== SNOW =====

private data class Flake(val x: Float, val yStart: Float, val radius: Float, val speed: Float, val sway: Float, val alpha: Float)

@Composable
fun SnowEffect(modifier: Modifier = Modifier, flakeCount: Int = 80, speedFactor: Float = 1f) {
    val flakes = remember(flakeCount) {
        List(flakeCount) {
            Flake(
                x = Random.nextFloat(),
                yStart = Random.nextFloat(),
                radius = Random.nextFloat() * 2.6f + 1.4f,
                speed = Random.nextFloat() * 0.35f + 0.18f,
                sway = Random.nextFloat() * 20f + 8f,
                alpha = Random.nextFloat() * 0.5f + 0.4f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "snow")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
        label = "snowProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        flakes.forEach { f ->
            val travel = (f.yStart + progress * f.speed * speedFactor) % 1.05f
            val y = travel * (h + 40f) - 20f
            val x = f.x * w + sin(travel * 2f * Math.PI.toFloat() * 2f) * f.sway
            drawCircle(color = Color.White.copy(alpha = f.alpha), radius = f.radius, center = Offset(x, y))
        }
    }
}

// ============================================================== HAIL ======

@Composable
fun HailEffect(modifier: Modifier = Modifier, count: Int = 55) {
    val stones = remember(count) {
        List(count) {
            Drop(
                x = Random.nextFloat(),
                yStart = Random.nextFloat(),
                length = Random.nextFloat() * 5f + 4f,
                speed = Random.nextFloat() * 0.6f + 0.9f,
                alpha = Random.nextFloat() * 0.4f + 0.5f,
                thickness = Random.nextFloat() * 1.6f + 1.4f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "hail")
    val progress by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing)),
        label = "hailProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        stones.forEach { d ->
            val travel = (d.yStart + progress * d.speed) % 1.05f
            val y = travel * (h + 40f) - 20f
            val x = d.x * w
            drawCircle(color = Color.White.copy(alpha = d.alpha), radius = d.thickness, center = Offset(x, y))
        }
    }
}

// =============================================================== SUN ======

@Composable
fun SunGlow(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sun")
    val rayRotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
        label = "sunRayRotation"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "sunPulse"
    )
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        rotate(rayRotation, pivot = center) {
            for (i in 0 until 12) {
                val angle = (i * 30f) * (Math.PI / 180f)
                val inner = radius * 1.15f
                val outer = radius * 1.55f
                val sx = center.x + cos(angle).toFloat() * inner
                val sy = center.y + sin(angle).toFloat() * inner
                val ex = center.x + cos(angle).toFloat() * outer
                val ey = center.y + sin(angle).toFloat() * outer
                drawLine(
                    color = Color(0xFFFFE9A8).copy(alpha = 0.35f),
                    start = Offset(sx, sy),
                    end = Offset(ex, ey),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF6D8), Color(0xFFFFD873).copy(alpha = 0.55f), Color.Transparent),
                center = center,
                radius = radius * 2.1f
            ),
            radius = radius * 2.1f * pulse,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFCF0), Color(0xFFFFE082)),
                center = center,
                radius = radius
            ),
            radius = radius * pulse,
            center = center
        )
    }
}

// ============================================================ MOON/STARS ==

private data class Star(val x: Float, val y: Float, val radius: Float, val twinkleOffset: Float)

@Composable
fun MoonWithStars(modifier: Modifier = Modifier, phaseFraction: Float, starCount: Int = 70) {
    val stars = remember(starCount) {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.75f,
                radius = Random.nextFloat() * 1.6f + 0.6f,
                twinkleOffset = Random.nextFloat()
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "night")
    val twinkle by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "twinkle"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { s ->
            val phase = ((twinkle + s.twinkleOffset) % 1f)
            val alpha = 0.35f + 0.5f * abs(sin(phase * Math.PI).toFloat())
            drawCircle(Color.White.copy(alpha = alpha), radius = s.radius, center = Offset(s.x * size.width, s.y * size.height))
        }

        // Real crescent/gibbous moon disc, positioned by the actual moon_phase fraction.
        // Kept slightly smaller/thinner than a "full-weight" disc so it reads as
        // delicate against the night sky rather than a bold, heavy circle.
        val moonRadius = size.minDimension * 0.095f
        val moonCenter = Offset(size.width * 0.78f, size.height * 0.16f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFDF5).copy(alpha = 0.28f), Color.Transparent),
                center = moonCenter, radius = moonRadius * 2.6f
            ),
            radius = moonRadius * 2.6f, center = moonCenter
        )
        drawMoonPhase(center = moonCenter, radius = moonRadius, phaseFraction = phaseFraction)
    }
}

// ============================================================== CLOUDS ====

private data class CloudPuff(val xStart: Float, val y: Float, val scale: Float, val speed: Float, val alpha: Float)

@Composable
fun CloudLayer(modifier: Modifier = Modifier, cloudCount: Int = 4, baseAlpha: Float = 0.55f, tint: Color = Color.White) {
    val puffs = remember(cloudCount) {
        List(cloudCount) {
            CloudPuff(
                xStart = Random.nextFloat(),
                y = Random.nextFloat() * 0.55f + 0.05f,
                scale = Random.nextFloat() * 0.6f + 0.7f,
                speed = Random.nextFloat() * 0.4f + 0.25f,
                alpha = Random.nextFloat() * 0.25f + baseAlpha
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "clouds")
    val drift by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(42000, easing = LinearEasing)),
        label = "cloudDrift"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        puffs.forEach { c ->
            val x = ((c.xStart + drift * c.speed) % 1.3f - 0.15f) * w
            val y = c.y * h
            val baseR = w * 0.09f * c.scale
            drawCloudPuff(Offset(x, y), baseR, c.alpha, tint)
        }
    }
}

private fun DrawScope.drawCloudPuff(
    center: Offset, r: Float, alpha: Float, tint: Color
) {
    val offsets = listOf(
        Offset(center.x - r * 1.1f, center.y + r * 0.25f) to r * 0.75f,
        Offset(center.x - r * 0.35f, center.y - r * 0.25f) to r * 0.95f,
        Offset(center.x + r * 0.45f, center.y - r * 0.1f) to r * 0.85f,
        Offset(center.x + r * 1.15f, center.y + r * 0.28f) to r * 0.68f,
        Offset(center.x + r * 0.1f, center.y + r * 0.35f) to r * 1.05f
    )

    // Soft cast shadow beneath the whole cloud, so it reads as floating above
    // the sky rather than painted flat onto it.
    offsets.forEach { (pos, radius) ->
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = alpha * 0.16f), Color.Transparent),
                center = Offset(pos.x, pos.y + radius * 0.35f), radius = radius * 1.5f
            ),
            radius = radius * 1.5f,
            center = Offset(pos.x, pos.y + radius * 0.35f)
        )
    }

    // Base tint.
    offsets.forEach { (pos, radius) ->
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = alpha), tint.copy(alpha = 0f)),
                center = pos, radius = radius * 1.4f
            ),
            radius = radius * 1.4f,
            center = pos
        )
    }

    // Cooler, slightly darker underside for volume (light source from above).
    offsets.forEach { (pos, radius) ->
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6B7C93).copy(alpha = alpha * 0.30f), Color.Transparent),
                center = Offset(pos.x, pos.y + radius * 0.45f), radius = radius * 1.1f
            ),
            radius = radius * 1.1f,
            center = Offset(pos.x, pos.y + radius * 0.45f)
        )
    }

    // Bright highlight along the top, as if lit from above.
    offsets.forEach { (pos, radius) ->
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = alpha * 0.35f), Color.Transparent),
                center = Offset(pos.x - radius * 0.15f, pos.y - radius * 0.4f), radius = radius * 0.9f
            ),
            radius = radius * 0.9f,
            center = Offset(pos.x - radius * 0.15f, pos.y - radius * 0.4f)
        )
    }
}

// ================================================================ FOG =====

@Composable
fun FogLayer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "fog")
    val drift by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing)),
        label = "fogDrift"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val bands = listOf(0.30f, 0.48f, 0.66f, 0.82f)
        bands.forEachIndexed { i, yFrac ->
            val dir = if (i % 2 == 0) 1f else -1f
            val x = ((drift * dir + i * 0.25f) % 1.4f - 0.2f) * w
            val bandHeight = h * 0.14f
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.22f), Color.Transparent),
                    startX = x, endX = x + w * 0.9f
                ),
                topLeft = Offset(0f, yFrac * h - bandHeight / 2f),
                size = Size(w, bandHeight)
            )
        }
    }
}

// ============================================================ LIGHTNING ===

@Composable
fun LightningFlash(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "lightning")
    // One saw-tooth cycle every ~3.8s; two short spikes are carved out of it
    // below so consecutive flashes read as an irregular flicker-flicker-dark
    // pattern rather than a perfectly metronomic pulse.
    val cycle by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3800, easing = LinearEasing)),
        label = "lightningCycle"
    )
    val spike1 = (1f - abs(cycle - 0.18f) * 22f).coerceIn(0f, 1f)
    val spike2 = (1f - abs(cycle - 0.24f) * 30f).coerceIn(0f, 1f)
    val alpha = (spike1 + spike2).coerceIn(0f, 1f) * 0.55f

    Canvas(modifier = modifier.fillMaxSize()) {
        if (alpha > 0.01f) {
            drawRect(Color.White.copy(alpha = alpha))
        }
    }
}
