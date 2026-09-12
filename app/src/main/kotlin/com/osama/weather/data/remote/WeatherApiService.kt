package com.osama.weather.data.remote

import com.osama.weather.data.remote.dto.ForecastResponseDto
import com.osama.weather.util.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

/**
 * Talks to https://api.open-meteo.com/v1/forecast requesting *every* variable
 * named in the project brief — current conditions, the full hourly block
 * (including the deep scientific fields), the 16-day daily block, and the
 * 96-step 15-minute nowcast.
 */
class WeatherApiService {

    private val dailyParams = listOf(
        "weather_code", "temperature_2m_max", "temperature_2m_min", "apparent_temperature_max",
        "uv_index_max", "apparent_temperature_min", "uv_index_clear_sky_max", "wind_speed_10m_max",
        "wind_gusts_10m_max", "wind_direction_10m_dominant", "shortwave_radiation_sum",
        "et0_fao_evapotranspiration", "moon_phase", "moonset", "moonrise", "sunshine_duration",
        "daylight_duration", "sunset", "sunrise", "precipitation_probability_max",
        "precipitation_hours", "precipitation_sum", "snowfall_sum", "showers_sum", "rain_sum"
    )

    private val hourlyParams = listOf(
        "temperature_2m", "relative_humidity_2m", "dew_point_2m", "apparent_temperature",
        "precipitation_probability", "precipitation", "rain", "showers", "snowfall", "snow_depth",
        "vapour_pressure_deficit", "et0_fao_evapotranspiration", "evapotranspiration", "visibility",
        "cloud_cover_high", "cloud_cover_low", "cloud_cover_mid", "cloud_cover", "surface_pressure",
        "pressure_msl", "weather_code", "wind_speed_10m", "wind_speed_80m", "wind_speed_120m",
        "wind_speed_180m", "wind_direction_10m", "wind_direction_80m", "wind_direction_120m",
        "wind_direction_180m", "wind_gusts_10m", "temperature_80m", "temperature_120m",
        "temperature_180m", "soil_temperature_0cm", "soil_temperature_6cm", "soil_temperature_18cm",
        "soil_temperature_54cm", "soil_moisture_0_to_1cm", "soil_moisture_1_to_3cm",
        "soil_moisture_3_to_9cm", "soil_moisture_9_to_27cm", "soil_moisture_27_to_81cm", "uv_index",
        "is_day", "uv_index_clear_sky", "sunshine_duration", "wet_bulb_temperature_2m",
        "total_column_integrated_water_vapour", "boundary_layer_height", "freezing_level_height",
        "convective_inhibition", "lifted_index", "cape"
    )

    private val currentParams = listOf(
        "temperature_2m", "relative_humidity_2m", "apparent_temperature", "is_day", "wind_speed_10m",
        "wind_direction_10m", "wind_gusts_10m", "snowfall", "showers", "rain", "precipitation",
        "weather_code", "cloud_cover", "pressure_msl", "surface_pressure"
    )

    private val minutely15Params = listOf(
        "temperature_2m", "relative_humidity_2m", "dew_point_2m", "apparent_temperature",
        "precipitation", "wind_gusts_10m", "visibility", "cape", "lightning_potential", "is_day",
        "shortwave_radiation", "direct_radiation", "diffuse_radiation", "direct_normal_irradiance",
        "global_tilted_irradiance", "terrestrial_radiation", "terrestrial_radiation_instant",
        "global_tilted_irradiance_instant", "diffuse_radiation_instant", "direct_normal_irradiance_instant",
        "direct_radiation_instant", "shortwave_radiation_instant", "sunshine_duration",
        "freezing_level_height", "snowfall_height", "snowfall", "rain", "wind_direction_80m",
        "wind_direction_10m", "wind_speed_80m", "wind_speed_10m", "weather_code"
    )

    suspend fun fetchForecast(latitude: Double, longitude: Double): Result<ForecastResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = ApiConfig.FORECAST_BASE_URL.toHttpUrl().newBuilder()
                    .addQueryParameter("latitude", latitude.toString())
                    .addQueryParameter("longitude", longitude.toString())
                    .addQueryParameter("daily", dailyParams.joinToString(","))
                    .addQueryParameter("hourly", hourlyParams.joinToString(","))
                    .addQueryParameter("current", currentParams.joinToString(","))
                    .addQueryParameter("minutely_15", minutely15Params.joinToString(","))
                    .addQueryParameter("forecast_days", ApiConfig.FORECAST_DAYS.toString())
                    .addQueryParameter("forecast_minutely_15", ApiConfig.MINUTELY_15_STEPS.toString())
                    .addQueryParameter("timezone", "auto")
                    .build()

                val request = Request.Builder().url(url).get().build()

                NetworkModule.okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("Forecast request failed: HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    NetworkModule.json.decodeFromString(ForecastResponseDto.serializer(), body)
                }
            }
        }
}
