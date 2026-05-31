package com.zion830.threedollars.ui.home.ui.compose

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSheetStateCalculatorTest {

    @Test
    fun `collapsed anchor keeps only peek height visible`() {
        val anchors = HomeSheetStateCalculator.anchors(
            containerHeightPx = 812,
            fullListTopPx = 188,
            collapsedPeekHeightPx = 164,
        )

        assertEquals(188f, anchors.fullListOffset, 0f)
        assertEquals(648f, anchors.collapsedOffset, 0f)
    }

    @Test
    fun `anchors are clamped between full list and collapsed`() {
        val anchors = HomeSheetStateCalculator.anchors(
            containerHeightPx = 812,
            fullListTopPx = 188,
            collapsedPeekHeightPx = 164,
        )

        assertEquals(188f, anchors.clamp(100f), 0f)
        assertEquals(400f, anchors.clamp(400f), 0f)
        assertEquals(648f, anchors.clamp(700f), 0f)
    }

    @Test
    fun `settle moves to nearest anchor by midpoint`() {
        val anchors = HomeSheetAnchors(
            fullListOffset = 188f,
            collapsedOffset = 648f,
        )

        assertEquals(HomeSheetValue.FullList, HomeSheetStateCalculator.settleValue(320f, anchors))
        assertEquals(HomeSheetValue.Collapsed, HomeSheetStateCalculator.settleValue(560f, anchors))
    }

    @Test
    fun `settle honors fling direction`() {
        val anchors = HomeSheetAnchors(
            fullListOffset = 188f,
            collapsedOffset = 648f,
        )

        assertEquals(HomeSheetValue.FullList, HomeSheetStateCalculator.settleValue(560f, anchors, velocityY = -1_300f))
        assertEquals(HomeSheetValue.Collapsed, HomeSheetStateCalculator.settleValue(320f, anchors, velocityY = 1_300f))
    }

    @Test
    fun `visible sheet height subtracts current offset`() {
        assertEquals(624f, HomeSheetStateCalculator.visibleHeight(containerHeightPx = 812, currentOffset = 188f), 0f)
        assertEquals(164f, HomeSheetStateCalculator.visibleHeight(containerHeightPx = 812, currentOffset = 648f), 0f)
    }

    @Test
    fun `preview offset uses desired height while preserving collapsed minimum`() {
        assertEquals(
            420f,
            HomeSheetStateCalculator.previewOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 392,
                minimumVisibleHeightPx = 164,
            ),
            0f,
        )
        assertEquals(
            648f,
            HomeSheetStateCalculator.previewOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 120,
                minimumVisibleHeightPx = 164,
            ),
            0f,
        )
    }
}
