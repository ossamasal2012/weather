package com.osama.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.domain.model.MinutelyEntry
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils

/**
 * "Rain starting soon" style strip: the next ~16 fifteen-minute steps (4
 * hours) as a small bar chart, with a one-line human summary above it.
 * Renders nothing at all when no precipitation is expected soon — there is
 * nothing useful to show, so the card is omitted rather than displaying an
 * empty "no rain" placeholder.
 */
@Composable
fun NowcastPrecipChart(
    steps: List<MinutelyEntry>,
    utcOffsetSeconds: Int,
    modifier: Modifier = Modifier
) {
    val nextSteps = steps.take(8) // next 2 hours at 15-min resolution
    val anyPrecip = nextSteps.any { it.precipitation > 0.05 }
    if (!anyPrecip) return

    val maxPrecip = (nextSteps.maxOfOrNull { it.precipitation } ?: 0.0).coerceAtLeast(0.5)

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.next_hours_precip),
            style = MaterialTheme.typography.titleMedium,
            color = WeatherColors.OnBgSecondary
        )
        Spacer(Modifier.height(Spacing.xs))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (nextSteps.isEmpty()) return@Canvas
            val barWidth = size.width / nextSteps.size
            nextSteps.forEachIndexed { i, step ->
                val heightFrac = (step.precipitation / maxPrecip).coerceIn(0.06, 1.0).toFloat()
                val barHeight = size.height * heightFrac
                val x = i * barWidth + barWidth / 2f
                drawLine(
                    color = if (step.precipitation > 0.05) WeatherColors.Accent else Color.White.copy(alpha = 0.25f),
                    start = Offset(x, size.height),
                    end = Offset(x, size.height - barHeight),
                    strokeWidth = barWidth * 0.5f,
                    cap = StrokeCap.Round
                )
            }
        }
        Spacer(Modifier.height(Spacing.xxs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.now),
                style = MaterialTheme.typography.labelSmall,
                color = WeatherColors.OnBgTertiary
            )
            nextSteps.lastOrNull()?.let {
                Text(
                    text = DateTimeUtils.hourLabel(it.epochSeconds, utcOffsetSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = WeatherColors.OnBgTertiary
                )
            }
        }
    }
}
