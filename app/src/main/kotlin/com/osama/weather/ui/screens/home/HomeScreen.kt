package com.osama.weather.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osama.weather.R
import com.osama.weather.domain.model.WeatherCodeMapper
import com.osama.weather.domain.model.WeatherCondition
import com.osama.weather.ui.components.CurrentWeatherHero
import com.osama.weather.ui.components.DailyForecastList
import com.osama.weather.ui.components.DetailStatCard
import com.osama.weather.ui.components.AirQualitySummaryCard
import com.osama.weather.ui.components.GlassCard
import com.osama.weather.ui.components.HourlyForecastRow
import com.osama.weather.ui.components.MyLocationIconButton
import com.osama.weather.ui.components.NowcastPrecipChart
import com.osama.weather.ui.components.SearchIconButton
import com.osama.weather.ui.components.StatGrid
import com.osama.weather.ui.components.SunMoonCard
import com.osama.weather.ui.components.UpdateDialog
import com.osama.weather.ui.components.background.WeatherBackground
import com.osama.weather.ui.theme.Spacing
import com.osama.weather.ui.theme.WeatherColors
import com.osama.weather.util.DateTimeUtils
import com.osama.weather.util.UnitConverters
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSearchClick: () -> Unit,
    onAirQualityClick: () -> Unit,
    onAdvancedDetailsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.useCurrentLocation()
        } else {
            viewModel.markLocationPermissionPermanentlyDenied()
        }
    }

    val installSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.onReturnedFromInstallSettings() }

    // On very first launch with no cached location at all, proactively ask —
    // the brief requires the current location's weather to appear automatically.
    LaunchedEffect(Unit) {
        if (uiState.location == null && !uiState.locationPermissionPermanentlyUnavailable) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val weather = uiState.weather
    val condition = weather?.let { WeatherCodeMapper.conditionFor(it.current.weatherCode) } ?: WeatherCondition.CLEAR
    val timeOfDay = weather?.let { w ->
        val today = w.today
        if (today != null) {
            val sunriseEpoch = DateTimeUtils.parseEpochSeconds(today.sunrise, w.utcOffsetSeconds)
            val sunsetEpoch = DateTimeUtils.parseEpochSeconds(today.sunset, w.utcOffsetSeconds)
            val nowEpoch = DateTimeUtils.parseEpochSeconds(w.current.time, w.utcOffsetSeconds)
            WeatherCodeMapper.timeOfDay(nowEpoch, sunriseEpoch, sunsetEpoch)
        } else {
            WeatherCodeMapper.timeOfDay(w.current.isDay)
        }
    } ?: com.osama.weather.domain.model.TimeOfDay.DAY

    val moonPhase = weather?.today?.moonPhase?.toFloat() ?: 0.5f
    val tempUnit = uiState.temperatureUnit
    val windUnit = uiState.windUnit
    val unitSuffix = UnitConverters.temperatureSuffix(tempUnit)

    Box(modifier = Modifier.fillMaxSize()) {
        WeatherBackground(
            condition = condition,
            timeOfDay = timeOfDay,
            moonPhaseFraction = moonPhase,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.sm, bottom = Spacing.xxl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = WeatherColors.OnBgSecondary)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.cd_settings), tint = WeatherColors.OnBgSecondary)
                }
                SearchIconButton(onClick = onSearchClick)
                Spacer(modifier = Modifier.width(Spacing.xs))
                MyLocationIconButton(onClick = { viewModel.useCurrentLocation() })
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            when {
                weather != null -> {
                    val temperature = UnitConverters.temperature(weather.current.temperature, tempUnit)
                    val apparent = UnitConverters.temperature(weather.current.apparentTemperature, tempUnit)
                    val todayConverted = weather.today

                    CurrentWeatherHero(
                        locationDisplayName = uiState.location?.displayName ?: "",
                        temperature = temperature,
                        apparentTemperature = apparent,
                        weatherCode = weather.current.weatherCode,
                        today = todayConverted?.copy(
                            temperatureMax = UnitConverters.temperature(todayConverted.temperatureMax, tempUnit),
                            temperatureMin = UnitConverters.temperature(todayConverted.temperatureMin, tempUnit)
                        ),
                        unitSuffix = unitSuffix
                    )

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    NowcastPrecipChart(
                        steps = weather.minutely15,
                        utcOffsetSeconds = weather.utcOffsetSeconds
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    HourlyForecastRow(
                        hours = weather.hourly.take(24),
                        utcOffsetSeconds = weather.utcOffsetSeconds,
                        unitSuffix = unitSuffix
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    DailyForecastList(
                        days = weather.daily,
                        utcOffsetSeconds = weather.utcOffsetSeconds,
                        unitSuffix = unitSuffix
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    weather.today?.let { today ->
                        SunMoonCard(
                            sunriseIso = today.sunrise,
                            sunsetIso = today.sunset,
                            nowEpochSeconds = DateTimeUtils.parseEpochSeconds(weather.current.time, weather.utcOffsetSeconds),
                            sunriseEpochSeconds = DateTimeUtils.parseEpochSeconds(today.sunrise, weather.utcOffsetSeconds),
                            sunsetEpochSeconds = DateTimeUtils.parseEpochSeconds(today.sunset, weather.utcOffsetSeconds),
                            moonPhaseFraction = today.moonPhase,
                            moonriseIso = today.moonrise,
                            moonsetIso = today.moonset
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    AirQualitySummaryCard(
                        usAqi = uiState.airQuality?.current?.usAqi,
                        onClick = onAirQualityClick
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    val windValue = UnitConverters.windSpeed(weather.current.windSpeed, windUnit)
                    val windLabel = UnitConverters.windUnitLabel(windUnit)

                    StatGrid(
                        content = listOf<@Composable () -> Unit>(
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.WaterDrop,
                                    label = stringResource(R.string.humidity),
                                    value = "${weather.current.relativeHumidity}%"
                                )
                            },
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.Air,
                                    label = stringResource(R.string.wind),
                                    value = "${windValue.roundToInt()} $windLabel",
                                    secondaryLine = stringResource(R.string.wind_gusts) + ": ${UnitConverters.windSpeed(weather.current.windGusts, windUnit).roundToInt()} $windLabel"
                                )
                            },
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.Thermostat,
                                    label = stringResource(R.string.uv_index),
                                    value = "${(weather.hourly.firstOrNull()?.uvIndex ?: 0.0).roundToInt()}"
                                )
                            },
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.Compress,
                                    label = stringResource(R.string.pressure),
                                    value = "${weather.current.pressureMsl.roundToInt()} hPa"
                                )
                            },
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.Visibility,
                                    label = stringResource(R.string.visibility),
                                    value = "${(((weather.hourly.firstOrNull()?.visibility ?: 10000.0) / 1000.0)).roundToInt()} كم"
                                )
                            },
                            {
                                DetailStatCard(
                                    icon = Icons.Filled.Opacity,
                                    label = stringResource(R.string.dew_point),
                                    value = "${UnitConverters.temperature(weather.hourly.firstOrNull()?.dewPoint ?: 0.0, tempUnit).roundToInt()}$unitSuffix"
                                )
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableRow(onAdvancedDetailsClick),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.view_all_details),
                                style = MaterialTheme.typography.titleMedium,
                                color = WeatherColors.OnBgPrimary
                            )
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = WeatherColors.OnBgSecondary)
                        }
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

                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = WeatherColors.Accent)
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = stringResource(R.string.locating),
                                color = WeatherColors.OnBgSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                else -> {
                    EmptyStateMessage(
                        permanentlyDenied = uiState.locationPermissionPermanentlyUnavailable,
                        errorRes = uiState.errorMessageRes,
                        onSearchClick = onSearchClick,
                        onRetryLocation = { viewModel.useCurrentLocation() }
                    )
                }
            }
        }

        updateState?.let { state ->
            UpdateDialog(
                state = state,
                onUpdateNowClick = { viewModel.onUpdateNowClick() },
                onOpenInstallSettings = { installSettingsLauncher.launch(viewModel.installSettingsIntent()) },
                onRetryClick = { viewModel.onRetryDownload() }
            )
        }
    }
}

@Composable
private fun EmptyStateMessage(
    permanentlyDenied: Boolean,
    errorRes: Int?,
    onSearchClick: () -> Unit,
    onRetryLocation: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(420.dp), contentAlignment = Alignment.Center) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                val message = when {
                    permanentlyDenied -> stringResource(R.string.location_permission_denied)
                    errorRes != null -> stringResource(errorRes)
                    else -> stringResource(R.string.location_permission_message)
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WeatherColors.OnBgPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    androidx.compose.material3.Button(onClick = onSearchClick) {
                        Text(stringResource(R.string.search_title))
                    }
                    if (!permanentlyDenied) {
                        androidx.compose.material3.OutlinedButton(onClick = onRetryLocation) {
                            Text(stringResource(R.string.use_my_location))
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
