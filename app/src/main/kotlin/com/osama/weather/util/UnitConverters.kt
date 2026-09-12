package com.osama.weather.util

import com.osama.weather.data.local.PrecipitationUnit
import com.osama.weather.data.local.TemperatureUnit
import com.osama.weather.data.local.WindUnit

/**
 * Everything is fetched from Open-Meteo and stored internally in its default
 * metric units (°C, km/h, mm) — these helpers convert to the user's chosen
 * display unit only at the UI layer, so the domain/data layers never need to
 * know about user preferences at all.
 */
object UnitConverters {

    fun temperature(celsius: Double, unit: TemperatureUnit): Double = when (unit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
    }

    fun temperatureSuffix(unit: TemperatureUnit): String = when (unit) {
        TemperatureUnit.CELSIUS -> "°"
        TemperatureUnit.FAHRENHEIT -> "°"
    }

    fun windSpeed(kmh: Double, unit: WindUnit): Double = when (unit) {
        WindUnit.KMH -> kmh
        WindUnit.MS -> kmh / 3.6
        WindUnit.MPH -> kmh * 0.621371
        WindUnit.KNOTS -> kmh * 0.539957
    }

    fun windUnitLabel(unit: WindUnit): String = when (unit) {
        WindUnit.KMH -> "كم/س"
        WindUnit.MS -> "م/ث"
        WindUnit.MPH -> "ميل/س"
        WindUnit.KNOTS -> "عقدة"
    }

    fun precipitation(mm: Double, unit: PrecipitationUnit): Double = when (unit) {
        PrecipitationUnit.MM -> mm
        PrecipitationUnit.INCH -> mm / 25.4
    }

    fun precipitationUnitLabel(unit: PrecipitationUnit): String = when (unit) {
        PrecipitationUnit.MM -> "ملم"
        PrecipitationUnit.INCH -> "إنش"
    }
}
