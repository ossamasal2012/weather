package com.osama.weather.domain.model

/**
 * Everything the app knows about the weather at one place: "now", the next
 * ~48 hours in detail, the next 16 days, and a 24-hour 15-minute-resolution
 * nowcast for the "starting soon" precipitation strip. This is the fully
 * parsed, unit-independent (metric, SI) result of one forecast request —
 * every single field Open-Meteo returned for the requested variables lives
 * here somewhere, nothing is discarded.
 */
data class WeatherBundle(
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double,
    val timezone: String,
    val utcOffsetSeconds: Int,
    val current: CurrentConditions,
    val hourly: List<HourlyEntry>,
    val daily: List<DailyEntry>,
    val minutely15: List<MinutelyEntry>
) {
    /** Convenience: today's entry, used everywhere as "today's high/low" etc. */
    val today: DailyEntry? get() = daily.firstOrNull()
}

data class CurrentConditions(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val isDay: Boolean,
    val relativeHumidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGusts: Double,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val weatherCode: Int,
    val cloudCover: Int,
    val pressureMsl: Double,
    val surfacePressure: Double
)

/**
 * One hourly step. Carries both the "everyday" fields shown in the main
 * hourly strip and the deeper scientific fields (soil, radiation, storm
 * indices, winds aloft) surfaced on the Advanced Details screen — Open-Meteo
 * returns them all in the same `hourly` block, so we keep them together too.
 */
data class HourlyEntry(
    val time: String,
    val epochSeconds: Long,
    val temperature: Double,
    val relativeHumidity: Int,
    val dewPoint: Double,
    val apparentTemperature: Double,
    val precipitationProbability: Int?,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val snowDepth: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val cloudCover: Int,
    val cloudCoverLow: Int,
    val cloudCoverMid: Int,
    val cloudCoverHigh: Int,
    val visibility: Double?,
    val pressureMsl: Double,
    val surfacePressure: Double,
    val windSpeed10m: Double,
    val windSpeed80m: Double?,
    val windSpeed120m: Double?,
    val windSpeed180m: Double?,
    val windDirection10m: Int,
    val windDirection80m: Int?,
    val windDirection120m: Int?,
    val windDirection180m: Int?,
    val windGusts10m: Double,
    val temperature80m: Double?,
    val temperature120m: Double?,
    val temperature180m: Double?,
    val uvIndex: Double,
    val uvIndexClearSky: Double?,
    val sunshineDuration: Double?,
    val vapourPressureDeficit: Double?,
    val et0FaoEvapotranspiration: Double?,
    val evapotranspiration: Double?,
    val wetBulbTemperature: Double?,
    val totalColumnWaterVapour: Double?,
    val boundaryLayerHeight: Double?,
    val freezingLevelHeight: Double?,
    val convectiveInhibition: Double?,
    val liftedIndex: Double?,
    val cape: Double?,
    val soilTemperature0cm: Double?,
    val soilTemperature6cm: Double?,
    val soilTemperature18cm: Double?,
    val soilTemperature54cm: Double?,
    val soilMoisture0to1cm: Double?,
    val soilMoisture1to3cm: Double?,
    val soilMoisture3to9cm: Double?,
    val soilMoisture9to27cm: Double?,
    val soilMoisture27to81cm: Double?
)

data class DailyEntry(
    val date: String,
    val weatherCode: Int,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val apparentTemperatureMax: Double,
    val apparentTemperatureMin: Double,
    val uvIndexMax: Double,
    val uvIndexClearSkyMax: Double?,
    val windSpeedMax: Double,
    val windGustsMax: Double,
    val windDirectionDominant: Int,
    val shortwaveRadiationSum: Double?,
    val et0FaoEvapotranspiration: Double?,
    val moonPhase: Double?,
    val moonrise: String?,
    val moonset: String?,
    val sunshineDuration: Double?,
    val daylightDuration: Double,
    val sunrise: String,
    val sunset: String,
    val precipitationProbabilityMax: Int?,
    val precipitationHours: Double,
    val precipitationSum: Double,
    val snowfallSum: Double,
    val showersSum: Double,
    val rainSum: Double
)

/** One 15-minute nowcast step, used for the "next hour" precipitation strip. */
data class MinutelyEntry(
    val time: String,
    val epochSeconds: Long,
    val temperature: Double?,
    val precipitation: Double,
    val weatherCode: Int?,
    val isDay: Boolean
)
