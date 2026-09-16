package com.osama.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors https://air-quality-api.open-meteo.com/v1/air-quality exactly. */
@Serializable
data class AirQualityResponseDto(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String? = null,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int = 0,
    val hourly: AirQualityHourlyDto? = null
)

@Serializable
data class AirQualityHourlyDto(
    val time: List<String> = emptyList(),
    val pm10: List<Double?> = emptyList(),
    @SerialName("pm2_5") val pm2_5: List<Double?> = emptyList(),
    @SerialName("carbon_monoxide") val carbonMonoxide: List<Double?> = emptyList(),
    @SerialName("carbon_dioxide") val carbonDioxide: List<Double?> = emptyList(),
    @SerialName("nitrogen_dioxide") val nitrogenDioxide: List<Double?> = emptyList(),
    @SerialName("sulphur_dioxide") val sulphurDioxide: List<Double?> = emptyList(),
    val ozone: List<Double?> = emptyList(),
    @SerialName("aerosol_optical_depth") val aerosolOpticalDepth: List<Double?> = emptyList(),
    val dust: List<Double?> = emptyList(),
    @SerialName("uv_index") val uvIndex: List<Double?> = emptyList(),
    @SerialName("uv_index_clear_sky") val uvIndexClearSky: List<Double?> = emptyList(),
    val methane: List<Double?> = emptyList(),
    @SerialName("european_aqi") val europeanAqi: List<Int?> = emptyList(),
    @SerialName("european_aqi_pm2_5") val europeanAqiPm2_5: List<Int?> = emptyList(),
    @SerialName("european_aqi_pm10") val europeanAqiPm10: List<Int?> = emptyList(),
    @SerialName("european_aqi_nitrogen_dioxide") val europeanAqiNitrogenDioxide: List<Int?> = emptyList(),
    @SerialName("european_aqi_ozone") val europeanAqiOzone: List<Int?> = emptyList(),
    @SerialName("european_aqi_sulphur_dioxide") val europeanAqiSulphurDioxide: List<Int?> = emptyList(),
    @SerialName("us_aqi") val usAqi: List<Int?> = emptyList(),
    @SerialName("us_aqi_pm2_5") val usAqiPm2_5: List<Int?> = emptyList(),
    @SerialName("us_aqi_pm10") val usAqiPm10: List<Int?> = emptyList(),
    @SerialName("us_aqi_nitrogen_dioxide") val usAqiNitrogenDioxide: List<Int?> = emptyList(),
    @SerialName("us_aqi_ozone") val usAqiOzone: List<Int?> = emptyList(),
    @SerialName("us_aqi_sulphur_dioxide") val usAqiSulphurDioxide: List<Int?> = emptyList(),
    @SerialName("us_aqi_carbon_monoxide") val usAqiCarbonMonoxide: List<Int?> = emptyList(),
    val formaldehyde: List<Double?> = emptyList(),
    val glyoxal: List<Double?> = emptyList(),
    @SerialName("peroxyacyl_nitrates") val peroxyacylNitrates: List<Double?> = emptyList(),
    @SerialName("sea_salt_aerosol") val seaSaltAerosol: List<Double?> = emptyList(),
    @SerialName("nitrogen_monoxide") val nitrogenMonoxide: List<Double?> = emptyList()
)
