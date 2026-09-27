package com.zion830.threedollars.ui.feed.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 피드 카드 상대 시간 분류 (iOS 와 동일 기준).
 * 1분 미만 "방금 전", 1시간 미만 "n분 전", 1일 미만 "n시간 전", 2일까지 "n일 전", 그 이후 날짜 (`feed_time_date_format`).
 */
sealed interface FeedTime {
    data object JustNow : FeedTime
    data class Minutes(val value: Long) : FeedTime
    data class Hours(val value: Long) : FeedTime
    data class Days(val value: Long) : FeedTime
    data class Date(val date: LocalDate) : FeedTime

    companion object {
        private val SERVER_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private const val MAX_RELATIVE_DAYS = 2L

        fun of(serverDateTime: String, now: LocalDateTime = LocalDateTime.now()): FeedTime? {
            val dateTime = try {
                LocalDateTime.parse(serverDateTime, SERVER_FORMAT)
            } catch (e: DateTimeParseException) {
                return null
            }
            val elapsed = Duration.between(dateTime, now)
            return when {
                elapsed.toMinutes() < 1 -> JustNow
                elapsed.toHours() < 1 -> Minutes(elapsed.toMinutes())
                elapsed.toDays() < 1 -> Hours(elapsed.toHours())
                elapsed.toDays() <= MAX_RELATIVE_DAYS -> Days(elapsed.toDays())
                else -> Date(dateTime.toLocalDate())
            }
        }
    }
}
