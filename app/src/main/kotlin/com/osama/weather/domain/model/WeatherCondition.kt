package com.osama.weather.domain.model

import androidx.annotation.StringRes
import com.osama.weather.R

/**
 * The visual "family" a WMO weather code belongs to. Every single code the
 * Open-Meteo forecast API can return (0–99, the official WMO table) maps to
 * exactly one of these — see [WeatherCodeMapper.conditionFor]. This is the
 * type the dynamic background system and the icon system both key off of.
 */
enum class WeatherCondition {
    CLEAR,
    MOSTLY_CLEAR,
    PARTLY_CLOUDY,
    OVERCAST,
    FOG,
    DRIZZLE,
    RAIN,
    FREEZING_RAIN,
    SNOW,
    RAIN_SHOWERS,
    SNOW_SHOWERS,
    THUNDERSTORM,
    THUNDERSTORM_HAIL
}

/** Where "now" sits relative to sunrise/sunset — drives the background's light. */
enum class TimeOfDay { DAWN, DAY, DUSK, NIGHT }

object WeatherCodeMapper {

    /** Maps a raw WMO weather_code (as returned by Open-Meteo) to a visual family. */
    fun conditionFor(code: Int): WeatherCondition = when (code) {
        0 -> WeatherCondition.CLEAR
        1 -> WeatherCondition.MOSTLY_CLEAR
        2 -> WeatherCondition.PARTLY_CLOUDY
        3 -> WeatherCondition.OVERCAST
        45, 48 -> WeatherCondition.FOG
        51, 53, 55, 56, 57 -> WeatherCondition.DRIZZLE
        61, 63, 65 -> WeatherCondition.RAIN
        66, 67 -> WeatherCondition.FREEZING_RAIN
        71, 73, 75, 77 -> WeatherCondition.SNOW
        80, 81, 82 -> WeatherCondition.RAIN_SHOWERS
        85, 86 -> WeatherCondition.SNOW_SHOWERS
        95 -> WeatherCondition.THUNDERSTORM
        96, 99 -> WeatherCondition.THUNDERSTORM_HAIL
        else -> WeatherCondition.PARTLY_CLOUDY
    }

    @StringRes
    fun stringResFor(code: Int): Int = when (code) {
        0 -> R.string.wc_0
        1 -> R.string.wc_1
        2 -> R.string.wc_2
        3 -> R.string.wc_3
        45 -> R.string.wc_45
        48 -> R.string.wc_48
        51 -> R.string.wc_51
        53 -> R.string.wc_53
        55 -> R.string.wc_55
        56 -> R.string.wc_56
        57 -> R.string.wc_57
        61 -> R.string.wc_61
        63 -> R.string.wc_63
        65 -> R.string.wc_65
        66 -> R.string.wc_66
        67 -> R.string.wc_67
        71 -> R.string.wc_71
        73 -> R.string.wc_73
        75 -> R.string.wc_75
        77 -> R.string.wc_77
        80 -> R.string.wc_80
        81 -> R.string.wc_81
        82 -> R.string.wc_82
        85 -> R.string.wc_85
        86 -> R.string.wc_86
        95 -> R.string.wc_95
        96 -> R.string.wc_96
        99 -> R.string.wc_99
        else -> R.string.wc_unknown
    }

    /** Does this condition realistically bring precipitation right now (for badges/nowcast)? */
    fun bringsPrecipitation(condition: WeatherCondition): Boolean = condition in setOf(
        WeatherCondition.DRIZZLE, WeatherCondition.RAIN, WeatherCondition.FREEZING_RAIN,
        WeatherCondition.SNOW, WeatherCondition.RAIN_SHOWERS, WeatherCondition.SNOW_SHOWERS,
        WeatherCondition.THUNDERSTORM, WeatherCondition.THUNDERSTORM_HAIL
    )

    fun isSnowy(condition: WeatherCondition): Boolean =
        condition == WeatherCondition.SNOW || condition == WeatherCondition.SNOW_SHOWERS

    /**
     * Classifies "now" into dawn / day / dusk / night given sunrise/sunset as
     * epoch seconds and the current epoch second. Dawn/dusk are a ±35-minute
     * window around the sun's edge crossing, matching the soft golden light
     * you actually see at those times.
     */
    fun timeOfDay(nowEpochSeconds: Long, sunriseEpochSeconds: Long, sunsetEpochSeconds: Long): TimeOfDay {
        val goldenWindow = 35 * 60L
        return when {
            nowEpochSeconds in (sunriseEpochSeconds - goldenWindow)..(sunriseEpochSeconds + goldenWindow) -> TimeOfDay.DAWN
            nowEpochSeconds in (sunsetEpochSeconds - goldenWindow)..(sunsetEpochSeconds + goldenWindow) -> TimeOfDay.DUSK
            nowEpochSeconds in sunriseEpochSeconds..sunsetEpochSeconds -> TimeOfDay.DAY
            else -> TimeOfDay.NIGHT
        }
    }

    /** Simple day/night split (matches Open-Meteo's own is_day flag) when a coarser split will do. */
    fun timeOfDay(isDay: Boolean): TimeOfDay = if (isDay) TimeOfDay.DAY else TimeOfDay.NIGHT
}
