package com.zion830.threedollars.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    private val koreanTimeRegex = Regex("(오전|오후)\\s*(\\d{1,2})시(?:\\s*(\\d{1,2})분)?")
    private val clockTimeRegex = Regex("^(\\d{1,2}):(\\d{2})(?::\\d{2})?$")
    private val hourOnlyFormatter = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN)
    private val hourMinuteFormatter = DateTimeFormatter.ofPattern("a h시 m분", Locale.KOREAN)
    private val serverFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun convertTo24HourFormat(timeString: String?): String? {
        if (timeString.isNullOrBlank()) return null
        return parse(timeString)?.format(serverFormatter) ?: timeString
    }

    fun formatOpeningHour(hourOfDay: Int, minute: Int): String {
        val time = LocalTime.of(hourOfDay, minute)
        return time.format(if (minute == 0) hourOnlyFormatter else hourMinuteFormatter)
    }

    fun toDisplayOpeningHour(timeString: String?): String? {
        if (timeString.isNullOrBlank()) return timeString
        val time = parse(timeString) ?: return timeString
        return formatOpeningHour(time.hour, time.minute)
    }

    fun parse(timeString: String): LocalTime? {
        clockTimeRegex.find(timeString.trim())?.let { match ->
            val hour = match.groupValues[1].toInt()
            val minute = match.groupValues[2].toInt()
            return if (hour in 0..23 && minute in 0..59) LocalTime.of(hour, minute) else null
        }
        val match = koreanTimeRegex.find(timeString) ?: return null
        val hour12 = match.groupValues[2].toInt()
        val minute = match.groupValues[3].toIntOrNull() ?: 0
        if (hour12 !in 1..12 || minute !in 0..59) return null
        val isPm = match.groupValues[1] == "오후"
        val hourOfDay = when {
            hour12 == 12 -> if (isPm) 12 else 0
            isPm -> hour12 + 12
            else -> hour12
        }
        return LocalTime.of(hourOfDay, minute)
    }
}
