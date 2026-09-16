package com.osama.weather.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.abs

/**
 * Draws a real, physically-shaped moon disc for any phase fraction (0/1 =
 * new, 0.25 = first quarter, 0.5 = full, 0.75 = last quarter), used
 * everywhere the app shows the moon (background sky, hourly/current
 * condition icon, the sun-and-moon card) so every occurrence looks
 * identical and correct.
 *
 * Earlier versions painted a flat, opaque "shadow" circle on top of the
 * moon disc — which only ever looks right if it happens to exactly match
 * whatever is directly behind it, and otherwise reads as two overlapping
 * circles rather than a crescent. This version instead punches a genuine
 * transparent bite out of the disc using canvas compositing
 * (saveLayer + BlendMode.Clear), so the result is a true crescent/gibbous
 * silhouette no matter what is behind it.
 */
fun DrawScope.drawMoonPhase(
    center: Offset,
    radius: Float,
    phaseFraction: Float,
    litColor: Color = Color(0xFFF6F1DE),
    litColorShaded: Color = Color(0xFFD8D0B0),
    craterColor: Color = Color(0x22536080)
) {
    // -1 = new moon, shadow covers from the left; 0 = full; +1 = new moon, shadow from the right.
    val shift = ((phaseFraction - 0.5f) * 2f).coerceIn(-1f, 1f)
    val bounds = Rect(center.x - radius * 1.3f, center.y - radius * 1.3f, center.x + radius * 1.3f, center.y + radius * 1.3f)

    fun moonDisc() {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(litColor, litColorShaded),
                center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                radius = radius * 1.6f
            ),
            radius = radius,
            center = center
        )
        // A few soft "craters" for texture, always subtle.
        drawCircle(craterColor, radius = radius * 0.14f, center = Offset(center.x - radius * 0.25f, center.y - radius * 0.15f))
        drawCircle(craterColor, radius = radius * 0.09f, center = Offset(center.x + radius * 0.2f, center.y + radius * 0.28f))
        drawCircle(craterColor, radius = radius * 0.06f, center = Offset(center.x + radius * 0.05f, center.y - radius * 0.35f))
    }

    if (abs(shift) < 0.03f) {
        // Full moon: no bite to punch, draw the disc directly.
        moonDisc()
        return
    }

    drawIntoCanvas { canvas ->
        canvas.saveLayer(bounds, Paint())
        moonDisc()
        drawCircle(
            color = Color.Black,
            radius = radius * 1.04f,
            center = Offset(center.x + shift * radius * 1.85f, center.y),
            blendMode = BlendMode.Clear
        )
        canvas.restore()
    }
}
