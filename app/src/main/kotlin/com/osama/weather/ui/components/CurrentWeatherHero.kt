package com.osama.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import com.osama.weather.R
import com.osama.weather.domain.model.DailyEntry
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.ui.theme.ExtraTypography
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import com.osama.weather.util.NumberFormatters
import kotlinx.coroutines.delay

@Composable
fun CurrentWeatherHero(
    locationDisplayName: String,
    temperature: Double,
    apparentTemperature: Double,
    weatherCode: Int,
    today: DailyEntry?,
    unitSuffix: String,
    utcOffsetSeconds: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locationDisplayName,
            style = MaterialTheme.typography.headlineMedium,
            color = WeatherColors.OnBgPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Spacer(Spacing.xxs)

        LiveLocationClock(utcOffsetSeconds = utcOffsetSeconds)

        Spacer(Spacing.xs)

        Text(
            // A bare "-2°" has no strong-direction character (just a sign,
            // digits, a symbol), so under this app's RTL layout it resolves
            // against the ambient right-to-left paragraph and the minus
            // sign lands on the wrong side unless it's isolated — see
            // NumberFormatters.signedTemp. textDirection = Ltr on top is a
            // second, redundant guard: this line is pure numerals/symbols
            // with no Arabic mixed in, so forcing it costs nothing.
            text = NumberFormatters.signedTemp(temperature, unitSuffix),
            style = ExtraTypography.heroTemperature.copy(textDirection = TextDirection.Ltr),
            color = WeatherColors.OnBgPrimary,
            maxLines = 1
        )

        Text(
            text = stringResource(WeatherCodeMapper.stringResFor(weatherCode)),
            style = MaterialTheme.typography.titleLarge,
            color = WeatherColors.OnBgSecondary
        )

        Spacer(Spacing.xxs)

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feels_like) + " " + NumberFormatters.signedTemp(apparentTemperature, unitSuffix),
                style = MaterialTheme.typography.bodyMedium,
                color = WeatherColors.OnBgSecondary
            )
            if (today != null) {
                Text("•", color = WeatherColors.OnBgTertiary)
                Text(
                    text = NumberFormatters.signedTemp(today.temperatureMax, unitSuffix) +
                        " / " + NumberFormatters.signedTemp(today.temperatureMin, unitSuffix),
                    style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
                    color = WeatherColors.OnBgSecondary
                )
            }
        }
    }
}

/**
 * A small live clock + date for the *viewed location* (using its own UTC
 * offset, never the device's local timezone), ticking on its own so it
 * always reflects "now" there without needing a full weather refresh.
 */
@Composable
private fun LiveLocationClock(utcOffsetSeconds: Int) {
    var nowEpoch by remember(utcOffsetSeconds) { mutableStateOf(DateTimeUtils.nowEpochSeconds()) }

    LaunchedEffect(utcOffsetSeconds) {
        while (true) {
            nowEpoch = DateTimeUtils.nowEpochSeconds()
            delay(30_000L) // a clock display only needs minute-level freshness
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xxs), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = DateTimeUtils.liveClock(nowEpoch, utcOffsetSeconds),
            style = MaterialTheme.typography.titleMedium,
            color = WeatherColors.OnBgSecondary
        )
        Text("•", color = WeatherColors.OnBgTertiary, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = DateTimeUtils.liveDate(nowEpoch, utcOffsetSeconds),
            style = MaterialTheme.typography.bodyMedium,
            color = WeatherColors.OnBgTertiary
        )
    }
}

@Composable
private fun Spacer(size: Dp) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(size))
}
