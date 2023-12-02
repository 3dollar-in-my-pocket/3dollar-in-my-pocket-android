package com.threedollar.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun calculatePercentages(a: Int, b: Int): Pair<Int, Int> {
    val total = a + b
    if (total == 0) return Pair(0, 0)

    val percentageA = (a * 100) / total
    val percentageB = (b * 100) / total

    return Pair(percentageA, percentageB)
}

fun getDeadlineString(inputDate: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val deadlineCalendar = Calendar.getInstance().apply {
        time = dateFormat.parse(inputDate)
    }

    val currentCalendar = Calendar.getInstance()

    val daysUntilDeadline = kotlin.math.ceil(
        (deadlineCalendar.timeInMillis - currentCalendar.timeInMillis) / (1000.0 * 60 * 60 * 24)
    ).toInt()

    if (daysUntilDeadline == 0) {
        return "오늘 마감"
    } else if (daysUntilDeadline < 0) {
        return "마감"
    }

    return "D-$daysUntilDeadline"
}