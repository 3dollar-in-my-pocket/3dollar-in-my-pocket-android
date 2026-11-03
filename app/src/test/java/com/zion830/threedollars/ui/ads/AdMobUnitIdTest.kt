package com.zion830.threedollars.ui.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AdMobUnitIdTest {

    private val serverUnitId = "ca-app-pub-5385646520024289/3147975229"
    private val fallbackUnitId = "ca-app-pub-5385646520024289/8009257851"
    private val testUnitId = "ca-app-pub-3940256099942544/6300978111"

    // TH-1565 TC3
    @Test
    fun `TH1565_TC3_release에서_서버값이_있으면_서버값을_쓴다`() {
        // Given
        val isDebug = false

        // When
        val unitId = AdMobUnitId.resolve(serverUnitId, fallbackUnitId, isDebug)

        // Then
        assertEquals(serverUnitId, unitId)
    }

    // TH-1565 TC4
    @Test
    fun `TH1565_TC4_release에서_서버값이_null이면_앱리소스_유닛을_쓴다`() {
        // Given
        val isDebug = false

        // When
        val unitId = AdMobUnitId.resolve(null, fallbackUnitId, isDebug)

        // Then
        assertEquals(fallbackUnitId, unitId)
    }

    // TH-1565 TC4
    @Test
    fun `TH1565_TC4_release에서_서버값이_빈문자열이면_앱리소스_유닛을_쓴다`() {
        // Given
        val blanks = listOf("", "  ")

        // When
        val unitIds = blanks.map { AdMobUnitId.resolve(it, fallbackUnitId, isDebug = false) }

        // Then
        assertEquals(listOf(fallbackUnitId, fallbackUnitId), unitIds)
    }

    // TH-1565 TC5
    @Test
    fun `TH1565_TC5_debug에서는_서버값이_있어도_테스트유닛을_쓴다`() {
        // Given
        val isDebug = true

        // When
        val unitId = AdMobUnitId.resolve(serverUnitId, testUnitId, isDebug)

        // Then
        assertEquals(testUnitId, unitId)
    }

    @Test
    fun `TH1565_TC10_서버유닛이_실패하면_앱리소스_유닛으로_다시_요청한다`() {
        // When
        val retryUnitId = AdMobUnitId.retryUnitId(failedUnitId = serverUnitId, fallbackUnitId = fallbackUnitId)

        // Then
        assertEquals(fallbackUnitId, retryUnitId)
    }

    @Test
    fun `TH1565_TC10_앱리소스_유닛까지_실패하면_다시_요청하지_않는다`() {
        // When
        val retryUnitId = AdMobUnitId.retryUnitId(failedUnitId = fallbackUnitId, fallbackUnitId = fallbackUnitId)

        // Then
        assertNull(retryUnitId)
    }
}
