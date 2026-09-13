package com.osama.weather.ui.screens.airquality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osama.weather.R
import com.osama.weather.domain.model.EuropeanAqiCategory
import com.osama.weather.domain.model.HourlyAirQualityEntry
import com.osama.weather.domain.model.UsAqiCategory
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.screens.home.HomeViewModel
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import kotlin.math.roundToInt

@Composable
fun AirQualityScreen(homeViewModel: HomeViewModel, onBack: () -> Unit) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val airQuality = uiState.airQuality
    val current = airQuality?.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherColors.BrandDeepBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.sm, bottom = Spacing.xxl)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = WeatherColors.OnBgPrimary)
                }
                Text(
                    text = "${stringResource(R.string.air_quality)} — ${uiState.location?.displayName ?: ""}",
                    style = MaterialTheme.typography.titleLarge,
                    color = WeatherColors.OnBgPrimary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            if (current == null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.loading),
                        color = WeatherColors.OnBgSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                return@Column
            }

            // ---- US AQI hero ----
            current.usAqi?.let { usAqi ->
                val category = UsAqiCategory.fromValue(usAqi)
                GlassCard(modifier = Modifier.fillMaxWidth(), strong = true) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.aqi_us_index), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text("$usAqi", style = MaterialTheme.typography.displayMedium, color = category.color)
                        Text(stringResource(category.labelRes), style = MaterialTheme.typography.titleLarge, color = WeatherColors.OnBgPrimary)
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        AqiBar(value = usAqi, maxValue = 500)
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(category.adviceRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WeatherColors.OnBgSecondary,
                            textAlign = TextAlign.Center
                        )
                        current.dominantUsPollutant?.let { (name, value) ->
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "${stringResource(R.string.dominant_pollutant)}: $name",
                                style = MaterialTheme.typography.labelMedium,
                                color = WeatherColors.OnBgTertiary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // ---- European AQI ----
            current.europeanAqi?.let { euAqi ->
                val category = EuropeanAqiCategory.fromValue(euAqi)
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.aqi_eu_index), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                            Text(stringResource(category.labelRes), style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgPrimary)
                        }
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(category.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$euAqi", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // ---- Pollutant breakdown ----
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dominant_pollutant),
                    style = MaterialTheme.typography.titleMedium,
                    color = WeatherColors.OnBgSecondary
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                PollutantRow(stringResource(R.string.pollutant_pm25), current.pm2_5, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_pm10), current.pm10, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_o3), current.ozone, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_no2), current.nitrogenDioxide, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_so2), current.sulphurDioxide, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_co), current.carbonMonoxide, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_dust), current.dust, "µg/m³")
                PollutantRow(stringResource(R.string.pollutant_co2), current.carbonDioxide, "ppm")
                PollutantRow(stringResource(R.string.pollutant_methane), current.methane, "µg/m³")
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // ---- 7-day hourly AQI forecast strip ----
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.aqi_forecast_7d),
                    style = MaterialTheme.typography.titleMedium,
                    color = WeatherColors.OnBgSecondary
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    items(airQuality.hourly.filterIndexed { i, _ -> i % 6 == 0 }.take(28)) { entry ->
                        ForecastAqiItem(entry, airQuality.timezone)
                    }
                }
            }
        }
    }
}

@Composable
private fun PollutantRow(label: String, value: Double?, unit: String) {
    if (value == null) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgSecondary)
        Text(text = "${(value * 10).roundToInt() / 10.0} $unit", style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgPrimary)
    }
    HorizontalDivider(color = WeatherColors.GlassBorder, thickness = 0.5.dp)
}

@Composable
private fun AqiBar(value: Int, maxValue: Int) {
    val fraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFF4CAF50),
        androidx.compose.ui.graphics.Color(0xFFFFC107),
        androidx.compose.ui.graphics.Color(0xFFFF9800),
        androidx.compose.ui.graphics.Color(0xFFF4511E),
        androidx.compose.ui.graphics.Color(0xFF8E24AA),
        androidx.compose.ui.graphics.Color(0xFF7E0023)
    )
    // Forced LTR: the gradient is always painted low(left)->high(right) in raw
    // drawing coordinates, so the marker's offset must use the same fixed
    // frame — otherwise the two would move in opposite directions under RTL.
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
    ) {
        androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(14.dp)) {
            val markerX = maxWidth * fraction
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                    .background(androidx.compose.ui.graphics.Brush.horizontalGradient(colors))
            )
            Box(
                modifier = Modifier
                    .offset(x = (markerX - 1.5.dp).coerceAtLeast(0.dp))
                    .width(3.dp)
                    .height(14.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                    .background(androidx.compose.ui.graphics.Color.White)
            )
        }
    }
}

@Composable
private fun ForecastAqiItem(entry: HourlyAirQualityEntry, timezone: String) {
    val aqi = entry.usAqi ?: return
    val category = UsAqiCategory.fromValue(aqi)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(56.dp)) {
        Text(
            text = DateTimeUtils.hourLabel(entry.epochSeconds, 0),
            style = MaterialTheme.typography.labelSmall,
            color = WeatherColors.OnBgTertiary
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(category.color),
            contentAlignment = Alignment.Center
        ) {
            Text("$aqi", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}
