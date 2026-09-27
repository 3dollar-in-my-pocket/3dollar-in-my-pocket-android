package com.zion830.threedollars.ui.storeDetail.post.model

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 소식 카드의 상대 시간 분류 (iOS `Date.toRelativeString` 과 동일 기준).
 * 1분 미만은 "방금", 2일 이내는 기기 로케일의 상대 시간, 그 이후는 날짜 문자열.
 */
sealed interface StorePostTime {
    data object JustNow : StorePostTime
    data class Relative(val epochMillis: Long) : StorePostTime
    data class Date(val text: String) : StorePostTime

    companion object {
        private const val MINUTE_MILLIS = 60_000L
        private const val TWO_DAYS_MILLIS = 2 * 24 * 60 * MINUTE_MILLIS
        private val SERVER_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        fun of(
            serverDateTime: String,
            nowMillis: Long = System.currentTimeMillis(),
            zoneId: ZoneId = ZoneId.systemDefault(),
        ): StorePostTime? {
            val dateTime = try {
                LocalDateTime.parse(serverDateTime, SERVER_FORMAT)
            } catch (e: DateTimeParseException) {
                return null
            }
            val epochMillis = dateTime.atZone(zoneId).toInstant().toEpochMilli()
            val elapsed = nowMillis - epochMillis
            return when {
                elapsed < MINUTE_MILLIS -> JustNow
                elapsed < TWO_DAYS_MILLIS -> Relative(epochMillis)
                else -> Date(dateTime.format(DATE_FORMAT))
            }
        }
    }
}
