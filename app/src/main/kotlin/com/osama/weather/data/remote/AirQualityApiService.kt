package com.osama.weather.data.remote

import com.osama.weather.data.remote.dto.AirQualityResponseDto
import com.osama.weather.util.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

/** Talks to https://air-quality-api.open-meteo.com/v1/air-quality requesting every pollutant + both AQI scales. */
class AirQualityApiService {

    private val hourlyParams = listOf(
        "pm10", "pm2_5", "carbon_monoxide", "carbon_dioxide", "nitrogen_dioxide", "sulphur_dioxide",
        "ozone", "aerosol_optical_depth", "dust", "uv_index", "uv_index_clear_sky", "methane",
        "european_aqi", "european_aqi_pm2_5", "european_aqi_pm10", "european_aqi_nitrogen_dioxide",
        "european_aqi_ozone", "european_aqi_sulphur_dioxide", "us_aqi", "us_aqi_pm2_5", "us_aqi_pm10",
        "us_aqi_nitrogen_dioxide", "us_aqi_ozone", "us_aqi_sulphur_dioxide", "us_aqi_carbon_monoxide",
        "formaldehyde", "glyoxal", "peroxyacyl_nitrates", "sea_salt_aerosol", "nitrogen_monoxide"
    )

    suspend fun fetchAirQuality(latitude: Double, longitude: Double): Result<AirQualityResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = ApiConfig.AIR_QUALITY_BASE_URL.toHttpUrl().newBuilder()
                    .addQueryParameter("latitude", latitude.toString())
                    .addQueryParameter("longitude", longitude.toString())
                    .addQueryParameter("hourly", hourlyParams.joinToString(","))
                    .addQueryParameter("timezone", "auto")
                    .addQueryParameter("forecast_days", ApiConfig.AIR_QUALITY_FORECAST_DAYS.toString())
                    .build()

                val request = Request.Builder().url(url).get().build()

                NetworkModule.okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("Air quality request failed: HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    NetworkModule.json.decodeFromString(AirQualityResponseDto.serializer(), body)
                }
            }
        }
}
