package com.osama.weather.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.osama.weather.R

data class AirQualityBundle(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val hourly: List<HourlyAirQualityEntry>
) {
    /** The reading closest to "now" — used for the summary card on the home screen. */
    val current: HourlyAirQualityEntry? get() = hourly.firstOrNull()
}

data class HourlyAirQualityEntry(
    val time: String,
    val epochSeconds: Long,
    val pm10: Double?,
    val pm2_5: Double?,
    val carbonMonoxide: Double?,
    val carbonDioxide: Double?,
    val nitrogenDioxide: Double?,
    val sulphurDioxide: Double?,
    val ozone: Double?,
    val aerosolOpticalDepth: Double?,
    val dust: Double?,
    val uvIndex: Double?,
    val uvIndexClearSky: Double?,
    val methane: Double?,
    val europeanAqi: Int?,
    val europeanAqiPm2_5: Int?,
    val europeanAqiPm10: Int?,
    val europeanAqiNitrogenDioxide: Int?,
    val europeanAqiOzone: Int?,
    val europeanAqiSulphurDioxide: Int?,
    val usAqi: Int?,
    val usAqiPm2_5: Int?,
    val usAqiPm10: Int?,
    val usAqiNitrogenDioxide: Int?,
    val usAqiOzone: Int?,
    val usAqiSulphurDioxide: Int?,
    val usAqiCarbonMonoxide: Int?,
    val formaldehyde: Double?,
    val glyoxal: Double?,
    val peroxyacylNitrates: Double?,
    val seaSaltAerosol: Double?,
    val nitrogenMonoxide: Double?
) {
    /** The pollutant with the worst individual US sub-index — "what's driving this number". */
    val dominantUsPollutant: Pair<String, Int>?
        get() = listOfNotNull(
            usAqiPm2_5?.let { "PM2.5" to it },
            usAqiPm10?.let { "PM10" to it },
            usAqiOzone?.let { "O₃" to it },
            usAqiNitrogenDioxide?.let { "NO₂" to it },
            usAqiSulphurDioxide?.let { "SO₂" to it },
            usAqiCarbonMonoxide?.let { "CO" to it }
        ).maxByOrNull { it.second }
}

/** US EPA Air Quality Index bands (2024 breakpoint revision), 0–500 scale. */
enum class UsAqiCategory(
    val range: IntRange,
    @StringRes val labelRes: Int,
    @StringRes val adviceRes: Int,
    val color: Color
) {
    GOOD(0..50, R.string.aqi_good, R.string.advice_good, Color(0xFF4CAF50)),
    MODERATE(51..100, R.string.aqi_moderate, R.string.advice_moderate, Color(0xFFFFC107)),
    UNHEALTHY_SENSITIVE(101..150, R.string.aqi_usg, R.string.advice_usg, Color(0xFFFF9800)),
    UNHEALTHY(151..200, R.string.aqi_unhealthy, R.string.advice_unhealthy, Color(0xFFF4511E)),
    VERY_UNHEALTHY(201..300, R.string.aqi_very_unhealthy, R.string.advice_very_unhealthy, Color(0xFF8E24AA)),
    HAZARDOUS(301..500, R.string.aqi_hazardous, R.string.advice_hazardous, Color(0xFF7E0023));

    companion object {
        fun fromValue(aqi: Int): UsAqiCategory =
            entries.firstOrNull { aqi in it.range } ?: if (aqi > 500) HAZARDOUS else GOOD
    }
}

/** European Environment Agency Air Quality Index bands, 0–100+ scale (Open-Meteo/CAMS). */
enum class EuropeanAqiCategory(
    val range: IntRange,
    @StringRes val labelRes: Int,
    val color: Color
) {
    GOOD(0..19, R.string.eaqi_good, Color(0xFF50C878)),
    FAIR(20..39, R.string.eaqi_fair, Color(0xFFA8D95B)),
    MODERATE(40..59, R.string.eaqi_moderate, Color(0xFFFFC107)),
    POOR(60..79, R.string.eaqi_poor, Color(0xFFFF7043)),
    VERY_POOR(80..99, R.string.eaqi_very_poor, Color(0xFFB71C1C)),
    EXTREMELY_POOR(100..Int.MAX_VALUE, R.string.eaqi_extremely_poor, Color(0xFF6A1B4D));

    companion object {
        fun fromValue(aqi: Int): EuropeanAqiCategory =
            entries.firstOrNull { aqi in it.range } ?: EXTREMELY_POOR
    }
}
