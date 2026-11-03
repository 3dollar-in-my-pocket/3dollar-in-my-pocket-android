package com.zion830.threedollars.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NaverMapUtilsTest {

    // TH-1356 TC1
    @Test
    fun `TH1356_TC1_조회반경은_화면대각선의_3분의1이다`() {
        // Given
        val viewportDiagonalM = 6_804.0

        // When
        val distanceM = NaverMapUtils.searchDistanceM(viewportDiagonalM)

        // Then
        assertEquals(2_268.0, distanceM, 0.001)
    }

    // TH-1356 TC1
    @Test
    fun `TH1356_TC1_조회반경은_화면중심에서_꼭짓점까지보다_짧다`() {
        // Given
        val viewportDiagonalM = 30_000.0

        // When
        val distanceM = NaverMapUtils.searchDistanceM(viewportDiagonalM)

        // Then
        assertTrue(distanceM < viewportDiagonalM / 2)
    }
}
