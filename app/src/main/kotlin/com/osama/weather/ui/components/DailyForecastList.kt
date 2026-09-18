package com.osama.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.data.local.TemperatureUnit
import com.osama.weather.domain.model.DailyEntry
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import com.osama.weather.util.NumberFormatters
import com.osama.weather.util.UnitConverters

@Composable
fun DailyForecastList(
    days: List<DailyEntry>,
    utcOffsetSeconds: Int,
    tempUnit: TemperatureUnit,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
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
                    tempUnit = tempUnit,
                    unitSuffix = unitSuffix
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
    tempUnit: TemperatureUnit,
    unitSuffix: String
) {
    val epoch = DateTimeUtils.parseEpochSeconds(day.date + "T12:00", utcOffsetSeconds)
    val dayLabel = if (isToday) stringResource(R.string.today) else DateTimeUtils.dayName(epoch, utcOffsetSeconds)
    val dateLabel = DateTimeUtils.shortDayMonth(epoch, utcOffsetSeconds)
    val minTemp = UnitConverters.temperature(day.temperatureMin, tempUnit)
    val maxTemp = UnitConverters.temperature(day.temperatureMax, tempUnit)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day name + date stacked in the same fixed-width column, so adding
        // the date never touches the width or visibility of anything else in
        // the row (precipitation chance, icon, temperatures, the range bar)
        // — only this column gets a little taller.
        Column(modifier = Modifier.width(72.dp)) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = WeatherColors.OnBgPrimary,
                maxLines = 1
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = WeatherColors.OnBgTertiary,
                maxLines = 1
            )
        }

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
            text = NumberFormatters.signedTemp(minTemp, unitSuffix),
            style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
            color = WeatherColors.OnBgTertiary,
            modifier = Modifier.width(36.dp)
        )

        // Full-length min→max range line: a single continuous bar spanning
        // the row's complete width (never a partial/floating segment with
        // empty track showing on either side), two-toned red on the high
        // side and blue on the low side. The frame is forced LTR so red
        // always lands physically on the same side as the max-temperature
        // label and blue on the same side as the min-temperature label,
        // regardless of the app's RTL layout.
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            0.0f to Color(0xFFFF5252),
                            0.48f to Color(0xFFFF5252),
                            0.52f to Color(0xFF448AFF),
                            1.0f to Color(0xFF448AFF)
                        )
                    )
            )
        }

        Text(
            text = NumberFormatters.signedTemp(maxTemp, unitSuffix),
            style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr),
            color = WeatherColors.OnBgPrimary,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}
