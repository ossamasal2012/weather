package com.osama.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.osama.weather.R
import com.osama.weather.domain.model.DailyEntry
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.ui.theme.ExtraTypography
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherHero(
    locationDisplayName: String,
    temperature: Double,
    apparentTemperature: Double,
    weatherCode: Int,
    today: DailyEntry?,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locationDisplayName,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = WeatherColors.OnBgPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Spacer(Spacing.xs)

        Text(
            text = "${temperature.roundToInt()}$unitSuffix",
            style = ExtraTypography.heroTemperature,
            color = WeatherColors.OnBgPrimary,
            maxLines = 1
        )

        Text(
            text = stringResource(WeatherCodeMapper.stringResFor(weatherCode)),
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            color = WeatherColors.OnBgSecondary
        )

        Spacer(Spacing.xxs)

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feels_like) + " ${apparentTemperature.roundToInt()}$unitSuffix",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = WeatherColors.OnBgSecondary
            )
            if (today != null) {
                Text("•", color = WeatherColors.OnBgTertiary)
                Text(
                    text = "${today.temperatureMax.roundToInt()}$unitSuffix / ${today.temperatureMin.roundToInt()}$unitSuffix",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.OnBgSecondary
                )
            }
        }
    }
}

@Composable
private fun Spacer(size: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(size))
}
