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

    @Test
    fun `full list progress is 0 when collapsed and 1 when fully expanded`() {
        val anchors = HomeSheetAnchors(fullListOffset = 188f, collapsedOffset = 588f)

        assertEquals(0f, HomeSheetStateCalculator.fullListProgress(588f, anchors), 0f)
        assertEquals(1f, HomeSheetStateCalculator.fullListProgress(188f, anchors), 0f)
    }

    @Test
    fun `full list progress grows as the sheet moves up`() {
        val anchors = HomeSheetAnchors(fullListOffset = 188f, collapsedOffset = 588f)

        assertEquals(0.5f, HomeSheetStateCalculator.fullListProgress(388f, anchors), 0.0001f)
    }

    @Test
    fun `full list progress is clamped outside the anchor range`() {
        val anchors = HomeSheetAnchors(fullListOffset = 188f, collapsedOffset = 588f)

        assertEquals(0f, HomeSheetStateCalculator.fullListProgress(1000f, anchors), 0f)
        assertEquals(1f, HomeSheetStateCalculator.fullListProgress(0f, anchors), 0f)
    }

    @Test
    fun `full list progress is 0 when there is no room to expand`() {
        val anchors = HomeSheetAnchors(fullListOffset = 588f, collapsedOffset = 588f)

        assertEquals(0f, HomeSheetStateCalculator.fullListProgress(588f, anchors), 0f)
    }

    private val minimizedPeekHeightPx = HomeSheetLayout.minimizedPeekHeightDp(hasTabs = true).toInt()
    private val threeStepAnchors = HomeSheetStateCalculator.anchors(
        containerHeightPx = 812,
        fullListTopPx = 188,
        collapsedPeekHeightPx = collapsedPeekHeightPx,
        minimizedPeekHeightPx = minimizedPeekHeightPx,
    )

    // TH-1535 TC1
    @Test
    fun `TH1535_TC1_홈첫진입이면_시트는_기존_기본높이다`() {
        // Given
        val initialValue = HomeSheetStateCalculator.initialValue

        // When
        val visibleHeight = HomeSheetStateCalculator.visibleHeight(
            containerHeightPx = 812,
            currentOffset = threeStepAnchors.offsetOf(initialValue),
        )

        // Then
        assertEquals(HomeSheetValue.Collapsed, initialValue)
        assertEquals(collapsedPeekHeightPx.toFloat(), visibleHeight, 0f)
    }

    // TH-1535 TC2
    @Test
    fun `TH1535_TC2_기본높이에서_아래로_드래그하면_핸들과_탭만_보이는_접힘으로_멈춘다`() {
        // Given
        val draggedOffset = threeStepAnchors.collapsedOffset + DRAG_PX

        // When
        val settled = HomeSheetStateCalculator.settleValueAfterDrag(
            currentOffset = draggedOffset,
            anchors = threeStepAnchors,
            totalDragY = DRAG_PX,
            dragThresholdPx = DRAG_THRESHOLD_PX,
        )
        val flung = HomeSheetStateCalculator.settleValue(
            currentOffset = draggedOffset,
            anchors = threeStepAnchors,
            velocityY = FLING_VELOCITY,
        )

        // Then
        assertEquals(HomeSheetValue.Minimized, settled)
        assertEquals(HomeSheetValue.Minimized, flung)
        assertEquals(88f, HomeSheetLayout.minimizedPeekHeightDp(hasTabs = true), 0f)
        assertEquals(20f, HomeSheetLayout.minimizedPeekHeightDp(hasTabs = false), 0f)
    }

    // TH-1535 TC3
    @Test
    fun `TH1535_TC3_접힘상태의_보이는높이는_접힘높이라_버튼이_시트를_따라_내려간다`() {
        // Given
        val minimizedOffset = threeStepAnchors.offsetOf(HomeSheetValue.Minimized)

        // When
        val visibleHeight = HomeSheetStateCalculator.visibleHeight(
            containerHeightPx = 812,
            currentOffset = minimizedOffset,
        )

        // Then
        assertEquals(minimizedPeekHeightPx.toFloat(), visibleHeight, 0f)
        assertEquals(minimizedOffset, threeStepAnchors.clamp(812f), 0f)
    }

    // TH-1535 TC4
    @Test
    fun `TH1535_TC4_접힘에서_위로_드래그하면_기본높이로_돌아온다`() {
        // Given
        val draggedOffset = threeStepAnchors.minimizedOffset - DRAG_PX

        // When
        val settled = HomeSheetStateCalculator.settleValueAfterDrag(
            currentOffset = draggedOffset,
            anchors = threeStepAnchors,
            totalDragY = -DRAG_PX,
            dragThresholdPx = DRAG_THRESHOLD_PX,
        )
        val flung = HomeSheetStateCalculator.settleValue(
            currentOffset = draggedOffset,
            anchors = threeStepAnchors,
            velocityY = -FLING_VELOCITY,
        )

        // Then
        assertEquals(HomeSheetValue.Collapsed, settled)
        assertEquals(HomeSheetValue.Collapsed, flung)
    }

    // TH-1535 TC5
    @Test
    fun `TH1535_TC5_접힘에서_기본높이를_넘겨_크게_올리면_전체높이로_올라간다`() {
        // Given
        val largeDragY = -(threeStepAnchors.minimizedOffset - threeStepAnchors.collapsedOffset + DRAG_PX)
        val draggedOffset = threeStepAnchors.minimizedOffset + largeDragY

        // When
        val settled = HomeSheetStateCalculator.settleValueAfterDrag(
            currentOffset = draggedOffset,
            anchors = threeStepAnchors,
            totalDragY = largeDragY,
            dragThresholdPx = DRAG_THRESHOLD_PX,
        )

        // Then
        assertEquals(HomeSheetValue.FullList, settled)
    }

    // TH-1535 TC7
    @Test
    fun `TH1535_TC7_접힘에서_미리보기를_열었다_닫으면_접힘으로_복귀한다`() {
        // Given
        val previousListValue = HomeSheetValue.Minimized

        // When
        val restored = HomeSheetStateCalculator.restoreAfterPreview(previousListValue)

        // Then
        assertEquals(HomeSheetValue.Minimized, restored)
        assertEquals(threeStepAnchors.minimizedOffset, threeStepAnchors.offsetOf(restored), 0f)
    }

    private companion object {
        const val DRAG_PX = 40f
        const val DRAG_THRESHOLD_PX = 24f
        const val FLING_VELOCITY = 1_300f
    }
}
