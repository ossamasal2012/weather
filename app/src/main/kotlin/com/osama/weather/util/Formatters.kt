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

    /** "5:42 ص" — clock time with minutes, used for sunrise/sunset. */
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

    private val arabicMonthNames = mapOf(
        1 to "يناير", 2 to "فبراير", 3 to "مارس", 4 to "أبريل",
        5 to "مايو", 6 to "يونيو", 7 to "يوليو", 8 to "أغسطس",
        9 to "سبتمبر", 10 to "أكتوبر", 11 to "نوفمبر", 12 to "ديسمبر"
    )

    /** "10:45 ص" — current wall-clock time at the given UTC offset, 12-hour format. */
    fun liveClock(nowEpochSeconds: Long, utcOffsetSeconds: Int): String {
        val dt = toLocalDateTime(nowEpochSeconds, utcOffsetSeconds)
        val hour24 = dt.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val minute = dt.minute.toString().padStart(2, '0')
        val marker = if (hour24 < 12) "ص" else "م"
        return "$hour12:$minute $marker"
    }

    /** "الأحد، 13 سبتمبر" — the calendar date at the given UTC offset. */
    fun liveDate(nowEpochSeconds: Long, utcOffsetSeconds: Int): String {
        val dt = toLocalDateTime(nowEpochSeconds, utcOffsetSeconds)
        val dayName = arabicDayNames[dt.dayOfWeek] ?: ""
        val month = arabicMonthNames[dt.monthValue] ?: ""
        return "$dayName، ${dt.dayOfMonth} $month"
    }

    /** "17/9" — compact day/month numeric date, shown next to each day in the forecast list. */
    fun shortDayMonth(epochSeconds: Long, utcOffsetSeconds: Int): String {
        val dt = toLocalDateTime(epochSeconds, utcOffsetSeconds)
        return "${dt.dayOfMonth}/${dt.monthValue}"
    }

    fun nowEpochSeconds(): Long = Instant.now().epochSecond

    fun isSameLocalDay(epochA: Long, epochB: Long, utcOffsetSeconds: Int): Boolean =
        toLocalDateTime(epochA, utcOffsetSeconds).toLocalDate() ==
            toLocalDateTime(epochB, utcOffsetSeconds).toLocalDate()
}

object NumberFormatters {
    /**
     * "-2°" / "24°" — a rounded, signed temperature (or any signed value with
     * a trailing unit symbol), wrapped in a Unicode left-to-right isolate
     * (U+2066 … U+2069).
     *
     * Without this, a bare "-2°" has no strong-direction character at all —
     * just a minus sign, digits, and a symbol, all "weak"/"neutral" under the
     * Unicode Bidi Algorithm — so inside this app's RTL layout it falls back
     * to the surrounding right-to-left paragraph and the minus sign gets
     * reordered to the wrong side (rendering as "2°-" instead of "-2°").
     * Isolating the run forces it to resolve as ordinary left-to-right
     * digits — the only sensible reading for a signed number in either
     * language — without affecting anything around it.
     */
    fun signedTemp(value: Double, suffix: String): String {
        val rounded = value.roundToInt()
        return "\u2066$rounded$suffix\u2069"
    }

    fun roundedInt(value: Double): Int = value.roundToInt()
    fun percent(value: Int): String = "$value%"
    fun percent(value: Double): String = "${value.roundToInt()}%"
    fun oneDecimal(value: Double): String = "%.1f".format(value)
    fun withUnit(value: Double, unit: String, decimals: Int = 0): String =
        if (decimals == 0) "${value.roundToInt()} $unit" else "%.${decimals}f $unit".format(value)
}
