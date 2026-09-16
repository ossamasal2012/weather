package com.osama.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.domain.model.DailyEntry
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import kotlin.math.roundToInt

@Composable
fun DailyForecastList(
    days: List<DailyEntry>,
    utcOffsetSeconds: Int,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
    val globalMin = days.minOfOrNull { it.temperatureMin } ?: 0.0
    val globalMax = days.maxOfOrNull { it.temperatureMax } ?: 1.0
    val range = (globalMax - globalMin).coerceAtLeast(1.0)

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.daily_forecast),
            style = MaterialTheme.typography.titleMedium,
            color = WeatherColors.OnBgSecondary
        )
        Spacer(Modifier.height(Spacing.sm))

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            days.forEachIndexed { index, day ->
                DailyRow(
                    day = day,
                    isToday = index == 0,
                    utcOffsetSeconds = utcOffsetSeconds,
                    unitSuffix = unitSuffix,
                    globalMin = globalMin,
                    range = range
                )
            }
        }
    }
}

@Composable
private fun DailyRow(
    day: DailyEntry,
    isToday: Boolean,
    utcOffsetSeconds: Int,
    unitSuffix: String,
    globalMin: Double,
    range: Double
) {
    val epoch = DateTimeUtils.parseEpochSeconds(day.date + "T12:00", utcOffsetSeconds)
    val dayLabel = if (isToday) stringResource(R.string.today) else DateTimeUtils.dayName(epoch, utcOffsetSeconds)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = WeatherColors.OnBgPrimary,
            modifier = Modifier.width(64.dp)
        )

        if ((day.precipitationProbabilityMax ?: 0) >= 15) {
            Text(
                text = "${day.precipitationProbabilityMax}%",
                style = MaterialTheme.typography.labelSmall,
                color = WeatherColors.Accent,
                modifier = Modifier.width(34.dp)
            )
        } else {
            Spacer(Modifier.width(34.dp))
        }

        WeatherIcon(
            condition = WeatherCodeMapper.conditionFor(day.weatherCode),
            isDay = true,
            size = 26.dp,
            modifier = Modifier.width(38.dp)
        )

        Text(
            text = "${day.temperatureMin.roundToInt()}$unitSuffix",
            style = MaterialTheme.typography.bodyMedium,
            color = WeatherColors.OnBgTertiary,
            modifier = Modifier.width(36.dp)
        )

        val startFrac = ((day.temperatureMin - globalMin) / range).coerceIn(0.0, 1.0).toFloat()
        val endFrac = ((day.temperatureMax - globalMin) / range).coerceIn(0.0, 1.0).toFloat()

        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
            ) {
                val trackWidth = maxWidth
                val segmentWidth = (trackWidth * (endFrac - startFrac)).coerceAtLeast(6.dp)
                val segmentOffset = trackWidth * startFrac

                // Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(WeatherColors.GlassSurfaceStrong)
                )
                // Floating min→max segment for this day: blue on the cool
                // (min) side, yellow on the warm (max) side — the segment's
                // own frame is forced LTR above, so left is always min and
                // right is always max, matching this gradient direction.
                Box(
                    modifier = Modifier
                        .offset(x = segmentOffset)
                        .width(segmentWidth)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4FC3F7), Color(0xFFFFC857))
                            )
                        )
                )
            }
        }

        Text(
            text = "${day.temperatureMax.roundToInt()}$unitSuffix",
            style = MaterialTheme.typography.bodyLarge,
            color = WeatherColors.OnBgPrimary,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}
