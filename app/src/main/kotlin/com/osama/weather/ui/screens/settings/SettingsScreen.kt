package com.osama.weather.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.osama.weather.BuildConfig
import com.osama.weather.R
import com.osama.weather.data.local.PrecipitationUnit
import com.osama.weather.data.local.TemperatureUnit
import com.osama.weather.data.local.WindUnit
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.screens.home.HomeViewModel
import com.osama.weather.ui.theme.Radius
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors

@Composable
fun SettingsScreen(homeViewModel: HomeViewModel, onBack: () -> Unit) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

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
                Text(stringResource(R.string.nav_settings), style = MaterialTheme.typography.titleLarge, color = WeatherColors.OnBgPrimary)
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_temp_unit), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                Spacer(modifier = Modifier.height(Spacing.xs))
                OptionRow(stringResource(R.string.unit_celsius), uiState.temperatureUnit == TemperatureUnit.CELSIUS) { homeViewModel.setTemperatureUnit(TemperatureUnit.CELSIUS) }
                OptionRow(stringResource(R.string.unit_fahrenheit), uiState.temperatureUnit == TemperatureUnit.FAHRENHEIT) { homeViewModel.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_wind_unit), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                Spacer(modifier = Modifier.height(Spacing.xs))
                OptionRow(stringResource(R.string.unit_kmh), uiState.windUnit == WindUnit.KMH) { homeViewModel.setWindUnit(WindUnit.KMH) }
                OptionRow(stringResource(R.string.unit_ms), uiState.windUnit == WindUnit.MS) { homeViewModel.setWindUnit(WindUnit.MS) }
                OptionRow(stringResource(R.string.unit_mph), uiState.windUnit == WindUnit.MPH) { homeViewModel.setWindUnit(WindUnit.MPH) }
                OptionRow(stringResource(R.string.unit_knots), uiState.windUnit == WindUnit.KNOTS) { homeViewModel.setWindUnit(WindUnit.KNOTS) }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_precip_unit), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                Spacer(modifier = Modifier.height(Spacing.xs))
                OptionRow(stringResource(R.string.unit_mm), uiState.precipitationUnit == PrecipitationUnit.MM) { homeViewModel.setPrecipitationUnit(PrecipitationUnit.MM) }
                OptionRow(stringResource(R.string.unit_inch), uiState.precipitationUnit == PrecipitationUnit.INCH) { homeViewModel.setPrecipitationUnit(PrecipitationUnit.INCH) }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_about), style = MaterialTheme.typography.titleMedium, color = WeatherColors.OnBgSecondary)
                Spacer(modifier = Modifier.height(Spacing.sm))
                InfoRow(stringResource(R.string.settings_version), BuildConfig.VERSION_NAME)
                InfoRow(stringResource(R.string.settings_data_source), stringResource(R.string.settings_data_source_value))
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = stringResource(R.string.settings_check_updates),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.Accent,
                    modifier = Modifier.clickable { homeViewModel.checkForUpdate() }
                )
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
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.sm))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = WeatherColors.Accent, unselectedColor = WeatherColors.OnBgTertiary)
        )
        Text(label, style = MaterialTheme.typography.bodyLarge, color = WeatherColors.OnBgPrimary)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = WeatherColors.OnBgPrimary)
    }
}
