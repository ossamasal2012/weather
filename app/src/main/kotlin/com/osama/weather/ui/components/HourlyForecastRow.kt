package com.osama.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.data.local.TemperatureUnit
import com.osama.weather.domain.model.HourlyEntry
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import com.osama.weather.util.NumberFormatters
import com.osama.weather.util.UnitConverters

@Composable
fun HourlyForecastRow(
    hours: List<HourlyEntry>,
    utcOffsetSeconds: Int,
    tempUnit: TemperatureUnit,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            style = MaterialTheme.typography.titleMedium,
            color = WeatherColors.OnBgSecondary
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.sm))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            contentPadding = PaddingValues(vertical = Spacing.xxs)
        ) {
            items(hours) { hour ->
                HourlyItem(hour, utcOffsetSeconds, tempUnit, unitSuffix)
            }
        }
    }
}

@Composable
private fun HourlyItem(hour: HourlyEntry, utcOffsetSeconds: Int, tempUnit: TemperatureUnit, unitSuffix: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(52.dp)
    ) {
        Text(
            text = DateTimeUtils.hourLabel(hour.epochSeconds, utcOffsetSeconds),
            style = MaterialTheme.typography.labelMedium,
            color = WeatherColors.OnBgSecondary
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.xs))
        WeatherIcon(
            condition = WeatherCodeMapper.conditionFor(hour.weatherCode),
            isDay = hour.isDay,
            size = 30.dp
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = NumberFormatters.signedTemp(UnitConverters.temperature(hour.temperature, tempUnit), unitSuffix),
            style = MaterialTheme.typography.titleSmall.copy(textDirection = TextDirection.Ltr),
            color = WeatherColors.OnBgPrimary
        )
        if ((hour.precipitationProbability ?: 0) >= 15) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${hour.precipitationProbability}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = WeatherColors.Accent
                )
            }
        }
    }
}
