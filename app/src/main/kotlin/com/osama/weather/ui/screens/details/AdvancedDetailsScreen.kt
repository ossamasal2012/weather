package com.osama.weather.ui.screens.details

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osama.weather.R
import com.osama.weather.domain.model.HourlyEntry
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.screens.home.HomeViewModel
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.UnitConverters

@Composable
fun AdvancedDetailsScreen(homeViewModel: HomeViewModel, onBack: () -> Unit) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val weather = uiState.weather
    val now: HourlyEntry? = weather?.hourly?.firstOrNull()
    val tempUnit = uiState.temperatureUnit
    val unitSuffix = UnitConverters.temperatureSuffix(tempUnit)

    Box(modifier = Modifier.fillMaxSize().background(WeatherColors.BrandDeepBlue)) {
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
                Column {
                    Text(stringResource(R.string.advanced_details_title), style = MaterialTheme.typography.titleLarge, color = WeatherColors.OnBgPrimary)
                }
            }
            Text(
                text = stringResource(R.string.advanced_details_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = WeatherColors.OnBgTertiary,
                modifier = Modifier.padding(top = Spacing.xxs, bottom = Spacing.md)
            )

            if (now == null || weather == null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.loading), color = WeatherColors.OnBgSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                return@Column
            }

            SectionCard(title = stringResource(R.string.section_atmosphere)) {
                DetailLine(stringResource(R.string.dew_point), "${UnitConverters.temperature(now.dewPoint, tempUnit).let { kotlin.math.round(it).toInt() }}$unitSuffix")
                DetailLine(stringResource(R.string.vapour_pressure_deficit), now.vapourPressureDeficit?.let { "%.2f kPa".format(it) } ?: "—")
                DetailLine(stringResource(R.string.wet_bulb_temp), now.wetBulbTemperature?.let { "${UnitConverters.temperature(it, tempUnit).let { v -> kotlin.math.round(v).toInt() }}$unitSuffix" } ?: "—")
                DetailLine(stringResource(R.string.total_water_vapour), now.totalColumnWaterVapour?.let { "%.1f kg/m²".format(it) } ?: "—")
                DetailLine(stringResource(R.string.boundary_layer_height), now.boundaryLayerHeight?.let { "${it.toInt()} m" } ?: "—")
                DetailLine(stringResource(R.string.freezing_level_height), now.freezingLevelHeight?.let { "${it.toInt()} m" } ?: "—")
                DetailLine(stringResource(R.string.cloud_cover) + " (منخفض/متوسط/عالٍ)", "${now.cloudCoverLow}% / ${now.cloudCoverMid}% / ${now.cloudCoverHigh}%")
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            SectionCard(title = stringResource(R.string.section_storm_indices)) {
                DetailLine(stringResource(R.string.cape_index), now.cape?.let { "${it.toInt()} J/kg" } ?: "—")
                DetailLine(stringResource(R.string.lifted_index), now.liftedIndex?.let { "%.1f".format(it) } ?: "—")
                DetailLine(stringResource(R.string.convective_inhibition), now.convectiveInhibition?.let { "${it.toInt()} J/kg" } ?: "—")
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            SectionCard(title = stringResource(R.string.section_wind_altitude)) {
                DetailLine("10 م", "${now.windSpeed10m.toInt()} كم/س")
                now.windSpeed80m?.let { DetailLine("80 م", "${it.toInt()} كم/س") }
                now.windSpeed120m?.let { DetailLine("120 م", "${it.toInt()} كم/س") }
                now.windSpeed180m?.let { DetailLine("180 م", "${it.toInt()} كم/س") }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            SectionCard(title = stringResource(R.string.section_soil)) {
                Text(stringResource(R.string.soil_temperature), style = MaterialTheme.typography.labelLarge, color = WeatherColors.OnBgTertiary)
                now.soilTemperature0cm?.let { DetailLine(stringResource(R.string.surface), "${UnitConverters.temperature(it, tempUnit).toInt()}$unitSuffix") }
                now.soilTemperature6cm?.let { DetailLine(stringResource(R.string.depth_cm, 6), "${UnitConverters.temperature(it, tempUnit).toInt()}$unitSuffix") }
                now.soilTemperature18cm?.let { DetailLine(stringResource(R.string.depth_cm, 18), "${UnitConverters.temperature(it, tempUnit).toInt()}$unitSuffix") }
                now.soilTemperature54cm?.let { DetailLine(stringResource(R.string.depth_cm, 54), "${UnitConverters.temperature(it, tempUnit).toInt()}$unitSuffix") }
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(stringResource(R.string.soil_moisture), style = MaterialTheme.typography.labelLarge, color = WeatherColors.OnBgTertiary)
                now.soilMoisture0to1cm?.let { DetailLine("0-1 سم", "%.2f m³/m³".format(it)) }
                now.soilMoisture1to3cm?.let { DetailLine("1-3 سم", "%.2f m³/m³".format(it)) }
                now.soilMoisture3to9cm?.let { DetailLine("3-9 سم", "%.2f m³/m³".format(it)) }
                now.soilMoisture9to27cm?.let { DetailLine("9-27 سم", "%.2f m³/m³".format(it)) }
                now.soilMoisture27to81cm?.let { DetailLine("27-81 سم", "%.2f m³/m³".format(it)) }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            SectionCard(title = stringResource(R.string.section_radiation)) {
                weather.today?.shortwaveRadiationSum?.let { DetailLine(stringResource(R.string.shortwave_radiation), "%.1f MJ/m²".format(it)) }
                weather.today?.sunshineDuration?.let { DetailLine(stringResource(R.string.sunshine_duration), "${(it / 3600).toInt()} ساعة") }
                weather.today?.et0FaoEvapotranspiration?.let { DetailLine(stringResource(R.string.evapotranspiration), "%.2f ملم".format(it)) }
                now.evapotranspiration?.let { DetailLine(stringResource(R.string.evapotranspiration_actual), "%.2f ملم".format(it)) }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
            Text(
                text = stringResource(R.string.copyright),
                style = MaterialTheme.typography.labelSmall,
                color = WeatherColors.OnBgTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
        Spacer(modifier = Modifier.height(Spacing.sm))
        content()
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgPrimary)
    }
    HorizontalDivider(color = WeatherColors.GlassBorder, thickness = 0.5.dp)
}
