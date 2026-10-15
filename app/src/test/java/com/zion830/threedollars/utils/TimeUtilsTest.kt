package com.zion830.threedollars.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    // TH-1454 TC1
    @Test
    fun `TH1454_TC1_분이있는시간은_시와분까지표시된다`() {
        // Given
        val serverTime = "14:30"

        // When
        val display = TimeUtils.toDisplayOpeningHour(serverTime)

        // Then
        assertEquals("오후 2시 30분", display)
    }

    // TH-1454 TC1
    @Test
    fun `TH1454_TC1_오전시간도_시와분까지표시된다`() {
        // Given
        val serverTime = "09:10"

        // When
        val display = TimeUtils.toDisplayOpeningHour(serverTime)

        // Then
        assertEquals("오전 9시 10분", display)
    }

    // TH-1454 TC2
    @Test
    fun `TH1454_TC2_정각은_시까지만표시된다`() {
        // Given
        val serverTime = "10:00"

        // When
        val display = TimeUtils.toDisplayOpeningHour(serverTime)

        // Then
        assertEquals("오전 10시", display)
    }

    // TH-1454 TC2
    @Test
    fun `TH1454_TC2_자정과정오는_12시로표시된다`() {
        // When
        val midnight = TimeUtils.formatOpeningHour(hourOfDay = 0, minute = 0)
        val noon = TimeUtils.formatOpeningHour(hourOfDay = 12, minute = 0)

        // Then
        assertEquals("오전 12시", midnight)
        assertEquals("오후 12시", noon)
    }

    // TH-1454 TC3
    @Test
    fun `TH1454_TC3_표시문자열은_서버형식으로_되돌린다`() {
        // When
        val withMinute = TimeUtils.convertTo24HourFormat("오후 2시 30분")
        val onTheHour = TimeUtils.convertTo24HourFormat("오후 2시")
        val legacyPadded = TimeUtils.convertTo24HourFormat("오전 9시 00분")
        val midnight = TimeUtils.convertTo24HourFormat("오전 12시")
        val alreadyServer = TimeUtils.convertTo24HourFormat("22:00")

        // Then
        assertEquals("14:30", withMinute)
        assertEquals("14:00", onTheHour)
        assertEquals("09:00", legacyPadded)
        assertEquals("00:00", midnight)
        assertEquals("22:00", alreadyServer)
    }

    // TH-1454 TC3
    @Test
    fun `TH1454_TC3_해석할수없는값은_그대로둔다`() {
        // When
        val display = TimeUtils.toDisplayOpeningHour("미정")
        val server = TimeUtils.convertTo24HourFormat("미정")

        // Then
        assertEquals("미정", display)
        assertEquals("미정", server)
    }
}
