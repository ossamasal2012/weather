package com.osama.weather.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToInt

/**
 * All Open-Meteo timestamps are requested with `timezone=auto`, so every ISO
 * string (e.g. "2026-09-11T15:00") is already local time *at the forecast
 * location*, not the device's timezone and not UTC. These helpers treat the
 * string as local wall-clock time and use the response's own utc_offset_seconds
 * to get a real, comparable epoch second — never the device's zone.
 */
object DateTimeUtils {

    fun parseEpochSeconds(isoLocal: String, utcOffsetSeconds: Int): Long =
        runCatching {
            LocalDateTime.parse(isoLocal).toEpochSecond(ZoneOffset.ofTotalSeconds(utcOffsetSeconds))
        }.getOrDefault(0L)

    fun toLocalDateTime(epochSeconds: Long, utcOffsetSeconds: Int): LocalDateTime =
        LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.ofTotalSeconds(utcOffsetSeconds))

    private val arabicDayNames = mapOf(
        DayOfWeek.SUNDAY to "الأحد",
        DayOfWeek.MONDAY to "الاثنين",
        DayOfWeek.TUESDAY to "الثلاثاء",
        DayOfWeek.WEDNESDAY to "الأربعاء",
        DayOfWeek.THURSDAY to "الخميس",
        DayOfWeek.FRIDAY to "الجمعة",
        DayOfWeek.SATURDAY to "السبت"
    )

    fun dayName(epochSeconds: Long, utcOffsetSeconds: Int): String =
        arabicDayNames[toLocalDateTime(epochSeconds, utcOffsetSeconds).dayOfWeek] ?: ""

    /** "3 م" / "12 ص" — compact 12-hour hour label with Arabic morning/evening marker. */
    fun hourLabel(epochSeconds: Long, utcOffsetSeconds: Int): String {
        val dt = toLocalDateTime(epochSeconds, utcOffsetSeconds)
        val hour24 = dt.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val marker = if (hour24 < 12) "ص" else "م"
        return "$hour12 $marker"
    }

    /** "5:42 ص" — clock time with minutes, used for sunrise/sunset/moonrise/moonset. */
    fun clockTime(isoLocal: String): String = runCatching {
        val dt = LocalDateTime.parse(isoLocal)
        val hour24 = dt.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val minute = dt.minute.toString().padStart(2, '0')
        val marker = if (hour24 < 12) "ص" else "م"
        "$hour12:$minute $marker"
    }.getOrDefault("—")

    fun nowEpochSeconds(): Long = Instant.now().epochSecond

    fun isSameLocalDay(epochA: Long, epochB: Long, utcOffsetSeconds: Int): Boolean =
        toLocalDateTime(epochA, utcOffsetSeconds).toLocalDate() ==
            toLocalDateTime(epochB, utcOffsetSeconds).toLocalDate()
}

object NumberFormatters {
    fun roundedTemp(value: Double): String = "${value.roundToInt()}°"
    fun roundedInt(value: Double): Int = value.roundToInt()
    fun percent(value: Int): String = "$value%"
    fun percent(value: Double): String = "${value.roundToInt()}%"
    fun oneDecimal(value: Double): String = "%.1f".format(value)
    fun withUnit(value: Double, unit: String, decimals: Int = 0): String =
        if (decimals == 0) "${value.roundToInt()} $unit" else "%.${decimals}f $unit".format(value)
}
