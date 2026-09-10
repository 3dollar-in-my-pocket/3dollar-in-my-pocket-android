package com.zion830.threedollars.ui.home.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeMapViewportTest {
    @Test
    fun `padding uses filter and sheet overlap without adding bottom navigation`() {
        assertEquals(
            HomeMapPadding(left = 16, top = 156, right = 16, bottom = 309),
            calculateHomeMapPadding(
                mapHeightPx = 800,
                mapWindowTopPx = 0,
                filterWindowBottomPx = 140,
                sheetVisibleHeightPx = 293,
                edgeMarginPx = 16,
            ),
        )
    }

    @Test
    fun `full list obstruction defers focus bounds`() {
        assertNull(
            calculateHomeMapPadding(
                mapHeightPx = 800,
                mapWindowTopPx = 20,
                filterWindowBottomPx = 200,
                sheetVisibleHeightPx = 640,
                edgeMarginPx = 16,
            ),
        )
    }

    @Test
    fun `negative overlap is clamped`() {
        assertEquals(
            HomeMapPadding(left = 8, top = 8, right = 8, bottom = 8),
            calculateHomeMapPadding(800, 100, 90, -1, 8),
        )
    }
}
