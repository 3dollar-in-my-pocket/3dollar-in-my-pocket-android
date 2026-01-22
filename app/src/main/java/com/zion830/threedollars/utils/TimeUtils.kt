package com.zion830.threedollars.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    fun convertTo24HourFormat(timeString: String?): String? {
        if (timeString.isNullOrBlank()) return null

        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("a h시 mm분", Locale.KOREAN)
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(timeString, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            timeString
        }
    }
}
