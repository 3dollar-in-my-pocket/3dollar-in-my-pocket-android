package com.zion830.threedollars.ui.home.ui.compose

import com.zion830.threedollars.ui.home.ui.HomeSheetLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSheetStateCalculatorTest {
    private val collapsedPeekHeightPx = HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP.toInt()
    private val collapsedOffsetPx = 812 - collapsedPeekHeightPx

    @Test
    fun `collapsed anchor keeps only peek height visible`() {
        val anchors = HomeSheetStateCalculator.anchors(
            containerHeightPx = 812,
            fullListTopPx = 188,
            collapsedPeekHeightPx = collapsedPeekHeightPx,
        )

        assertEquals(188f, anchors.fullListOffset, 0f)
        assertEquals(collapsedOffsetPx.toFloat(), anchors.collapsedOffset, 0f)
    }

    @Test
    fun `anchors are clamped between full list and collapsed`() {
        val anchors = HomeSheetStateCalculator.anchors(
            containerHeightPx = 812,
            fullListTopPx = 188,
            collapsedPeekHeightPx = collapsedPeekHeightPx,
        )

        assertEquals(188f, anchors.clamp(100f), 0f)
        assertEquals(400f, anchors.clamp(400f), 0f)
        assertEquals(collapsedOffsetPx.toFloat(), anchors.clamp(700f), 0f)
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
        assertEquals(
            collapsedPeekHeightPx.toFloat(),
            HomeSheetStateCalculator.visibleHeight(
                containerHeightPx = 812,
                currentOffset = collapsedOffsetPx.toFloat(),
            ),
            0f,
        )
    }

    @Test
    fun `preview offset uses desired height while preserving collapsed minimum`() {
        assertEquals(
            420f,
            HomeSheetStateCalculator.previewOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 392,
                minimumVisibleHeightPx = collapsedPeekHeightPx,
            ),
            0f,
        )
        assertEquals(
            collapsedOffsetPx.toFloat(),
            HomeSheetStateCalculator.previewOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 120,
                minimumVisibleHeightPx = collapsedPeekHeightPx,
            ),
            0f,
        )
    }

    @Test
    fun `preview target offset allows shorter than collapsed height`() {
        val anchors = HomeSheetAnchors(
            fullListOffset = 188f,
            collapsedOffset = collapsedOffsetPx.toFloat(),
        )

        assertEquals(
            692f,
            HomeSheetStateCalculator.previewTargetOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 120,
                minimumVisibleHeightPx = 0,
                anchors = anchors,
            ),
            0f,
        )
    }

    @Test
    fun `preview target offset does not pass full list top`() {
        val anchors = HomeSheetAnchors(
            fullListOffset = 188f,
            collapsedOffset = collapsedOffsetPx.toFloat(),
        )

        assertEquals(
            188f,
            HomeSheetStateCalculator.previewTargetOffset(
                containerHeightPx = 812,
                desiredVisibleHeightPx = 700,
                minimumVisibleHeightPx = 0,
                anchors = anchors,
            ),
            0f,
        )
    }

    @Test
    fun `closing preview restores previous list sheet value`() {
        assertEquals(
            HomeSheetValue.FullList,
            HomeSheetStateCalculator.restoreAfterPreview(HomeSheetValue.FullList),
        )
        assertEquals(
            HomeSheetValue.Collapsed,
            HomeSheetStateCalculator.restoreAfterPreview(HomeSheetValue.Collapsed),
        )
    }
}
