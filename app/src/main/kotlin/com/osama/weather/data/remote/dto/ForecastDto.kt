package com.osama.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Mirrors the exact JSON envelope returned by
 * https://api.open-meteo.com/v1/forecast — one top-level object with a
 * `_units` sibling next to every time-series block. Field names match the
 * wire format one-for-one on purpose (see [com.osama.weather.data.repository.WeatherRepository]
 * for the translation into clean domain models).
 */
@Serializable
data class ForecastResponseDto(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Double? = null,
    val timezone: String? = null,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int = 0,
    val current: CurrentDto? = null,
    val hourly: HourlyDto? = null,
    val daily: DailyDto? = null,
    @SerialName("minutely_15") val minutely15: Minutely15Dto? = null
)

@Serializable
data class CurrentDto(
    val time: String = "",
    @SerialName("temperature_2m") val temperature2m: Double? = null,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int? = null,
    @SerialName("apparent_temperature") val apparentTemperature: Double? = null,
    @SerialName("is_day") val isDay: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed10m: Double? = null,
    @SerialName("wind_direction_10m") val windDirection10m: Int? = null,
    @SerialName("wind_gusts_10m") val windGusts10m: Double? = null,
    val snowfall: Double? = null,
    val showers: Double? = null,
    val rain: Double? = null,
    val precipitation: Double? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    @SerialName("cloud_cover") val cloudCover: Int? = null,
    @SerialName("pressure_msl") val pressureMsl: Double? = null,
    @SerialName("surface_pressure") val surfacePressure: Double? = null
)

@Serializable
data class HourlyDto(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature2m: List<Double?> = emptyList(),
    @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Int?> = emptyList(),
    @SerialName("dew_point_2m") val dewPoint2m: List<Double?> = emptyList(),
    @SerialName("apparent_temperature") val apparentTemperature: List<Double?> = emptyList(),
    @SerialName("precipitation_probability") val precipitationProbability: List<Int?> = emptyList(),
    val precipitation: List<Double?> = emptyList(),
    val rain: List<Double?> = emptyList(),
    val showers: List<Double?> = emptyList(),
    val snowfall: List<Double?> = emptyList(),
    @SerialName("snow_depth") val snowDepth: List<Double?> = emptyList(),
    @SerialName("vapour_pressure_deficit") val vapourPressureDeficit: List<Double?> = emptyList(),
    @SerialName("et0_fao_evapotranspiration") val et0FaoEvapotranspiration: List<Double?> = emptyList(),
    val evapotranspiration: List<Double?> = emptyList(),
    val visibility: List<Double?> = emptyList(),
    @SerialName("cloud_cover_high") val cloudCoverHigh: List<Int?> = emptyList(),
    @SerialName("cloud_cover_low") val cloudCoverLow: List<Int?> = emptyList(),
    @SerialName("cloud_cover_mid") val cloudCoverMid: List<Int?> = emptyList(),
    @SerialName("cloud_cover") val cloudCover: List<Int?> = emptyList(),
    @SerialName("surface_pressure") val surfacePressure: List<Double?> = emptyList(),
    @SerialName("pressure_msl") val pressureMsl: List<Double?> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int?> = emptyList(),
    @SerialName("wind_speed_10m") val windSpeed10m: List<Double?> = emptyList(),
    @SerialName("wind_speed_80m") val windSpeed80m: List<Double?> = emptyList(),
    @SerialName("wind_speed_120m") val windSpeed120m: List<Double?> = emptyList(),
    @SerialName("wind_speed_180m") val windSpeed180m: List<Double?> = emptyList(),
    @SerialName("wind_direction_10m") val windDirection10m: List<Int?> = emptyList(),
    @SerialName("wind_direction_80m") val windDirection80m: List<Int?> = emptyList(),
    @SerialName("wind_direction_120m") val windDirection120m: List<Int?> = emptyList(),
    @SerialName("wind_direction_180m") val windDirection180m: List<Int?> = emptyList(),
    @SerialName("wind_gusts_10m") val windGusts10m: List<Double?> = emptyList(),
    @SerialName("temperature_80m") val temperature80m: List<Double?> = emptyList(),
    @SerialName("temperature_120m") val temperature120m: List<Double?> = emptyList(),
    @SerialName("temperature_180m") val temperature180m: List<Double?> = emptyList(),
    @SerialName("soil_temperature_0cm") val soilTemperature0cm: List<Double?> = emptyList(),
    @SerialName("soil_temperature_6cm") val soilTemperature6cm: List<Double?> = emptyList(),
    @SerialName("soil_temperature_18cm") val soilTemperature18cm: List<Double?> = emptyList(),
    @SerialName("soil_temperature_54cm") val soilTemperature54cm: List<Double?> = emptyList(),
    @SerialName("soil_moisture_0_to_1cm") val soilMoisture0to1cm: List<Double?> = emptyList(),
    @SerialName("soil_moisture_1_to_3cm") val soilMoisture1to3cm: List<Double?> = emptyList(),
    @SerialName("soil_moisture_3_to_9cm") val soilMoisture3to9cm: List<Double?> = emptyList(),
    @SerialName("soil_moisture_9_to_27cm") val soilMoisture9to27cm: List<Double?> = emptyList(),
    @SerialName("soil_moisture_27_to_81cm") val soilMoisture27to81cm: List<Double?> = emptyList(),
    @SerialName("uv_index") val uvIndex: List<Double?> = emptyList(),
    @SerialName("is_day") val isDay: List<Int?> = emptyList(),
    @SerialName("uv_index_clear_sky") val uvIndexClearSky: List<Double?> = emptyList(),
    @SerialName("sunshine_duration") val sunshineDuration: List<Double?> = emptyList(),
    @SerialName("wet_bulb_temperature_2m") val wetBulbTemperature2m: List<Double?> = emptyList(),
    @SerialName("total_column_integrated_water_vapour") val totalColumnIntegratedWaterVapour: List<Double?> = emptyList(),
    @SerialName("boundary_layer_height") val boundaryLayerHeight: List<Double?> = emptyList(),
    @SerialName("freezing_level_height") val freezingLevelHeight: List<Double?> = emptyList(),
    @SerialName("convective_inhibition") val convectiveInhibition: List<Double?> = emptyList(),
    @SerialName("lifted_index") val liftedIndex: List<Double?> = emptyList(),
    val cape: List<Double?> = emptyList()
)

@Serializable
data class DailyDto(
    val time: List<String> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int?> = emptyList(),
    @SerialName("temperature_2m_max") val temperature2mMax: List<Double?> = emptyList(),
    @SerialName("temperature_2m_min") val temperature2mMin: List<Double?> = emptyList(),
    @SerialName("apparent_temperature_max") val apparentTemperatureMax: List<Double?> = emptyList(),
    @SerialName("apparent_temperature_min") val apparentTemperatureMin: List<Double?> = emptyList(),
    @SerialName("uv_index_max") val uvIndexMax: List<Double?> = emptyList(),
    @SerialName("uv_index_clear_sky_max") val uvIndexClearSkyMax: List<Double?> = emptyList(),
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: List<Double?> = emptyList(),
    @SerialName("wind_gusts_10m_max") val windGusts10mMax: List<Double?> = emptyList(),
    @SerialName("wind_direction_10m_dominant") val windDirection10mDominant: List<Int?> = emptyList(),
    @SerialName("shortwave_radiation_sum") val shortwaveRadiationSum: List<Double?> = emptyList(),
    @SerialName("et0_fao_evapotranspiration") val et0FaoEvapotranspiration: List<Double?> = emptyList(),
    @SerialName("moon_phase") val moonPhase: List<Double?> = emptyList(),
    val moonset: List<String?> = emptyList(),
    val moonrise: List<String?> = emptyList(),
    @SerialName("sunshine_duration") val sunshineDuration: List<Double?> = emptyList(),
    @SerialName("daylight_duration") val daylightDuration: List<Double?> = emptyList(),
    val sunset: List<String?> = emptyList(),
    val sunrise: List<String?> = emptyList(),
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?> = emptyList(),
    @SerialName("precipitation_hours") val precipitationHours: List<Double?> = emptyList(),
    @SerialName("precipitation_sum") val precipitationSum: List<Double?> = emptyList(),
    @SerialName("snowfall_sum") val snowfallSum: List<Double?> = emptyList(),
    @SerialName("showers_sum") val showersSum: List<Double?> = emptyList(),
    @SerialName("rain_sum") val rainSum: List<Double?> = emptyList()
)

@Serializable
data class Minutely15Dto(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature2m: List<Double?> = emptyList(),
    val precipitation: List<Double?> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int?> = emptyList(),
    @SerialName("is_day") val isDay: List<Int?> = emptyList()
)
