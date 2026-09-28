package com.zion830.threedollars.ui.storeDetail.post

import com.zion830.threedollars.ui.storeDetail.post.model.StorePostTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class StorePostTimeTest {

    private val zone = ZoneId.of("Asia/Seoul")
    private val now = LocalDateTime.of(2026, 9, 27, 15, 0, 0)
    private val nowMillis = now.atZone(zone).toInstant().toEpochMilli()

    private fun timeOf(dateTime: LocalDateTime) = StorePostTime.of(dateTime.toString(), nowMillis, zone)

    // TH-197 TC9
    @Test
    fun `TH197_TC9_30초전은_방금_2시간전은_상대시간_3일전은_날짜로_표시한다`() {
        // Given
        val thirtySecondsAgo = now.minusSeconds(30)
        val twoHoursAgo = now.minusHours(2)
        val threeDaysAgo = now.minusDays(3)

        // When
        val justNow = timeOf(thirtySecondsAgo)
        val relative = timeOf(twoHoursAgo)
        val date = timeOf(threeDaysAgo)

        // Then
        assertEquals(StorePostTime.JustNow, justNow)
        assertEquals(StorePostTime.Relative(twoHoursAgo.atZone(zone).toInstant().toEpochMilli()), relative)
        assertEquals(StorePostTime.Date("2026-09-24"), date)
    }

    // TH-197 TC9
    @Test
    fun `TH197_TC9_2일_이내는_상대시간이고_2일을_넘으면_날짜다`() {
        // Given
        val almostTwoDays = now.minusHours(47)
        val overTwoDays = now.minusHours(49)

        // When
        val within = timeOf(almostTwoDays)
        val over = timeOf(overTwoDays)

        // Then
        assertEquals(StorePostTime.Relative(almostTwoDays.atZone(zone).toInstant().toEpochMilli()), within)
        assertEquals(StorePostTime.Date("2026-09-25"), over)
    }

    @Test
    fun `서버_시간형식이_아니면_시간을_표시하지_않는다`() {
        // Given
        val invalid = "어제"

        // When
        val time = StorePostTime.of(invalid, nowMillis, zone)

        // Then
        assertNull(time)
    }
}
