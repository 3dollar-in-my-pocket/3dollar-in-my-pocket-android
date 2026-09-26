package com.zion830.threedollars.ui.home.ui.compose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailSheetSpecTest {

    private val tipOffset = 1400f

    // TH-1226 TC2
    @Test
    fun `TH1226_TC2_미리보기시트를_절반이상_끌어올렸다_놓으면_full로_스냅한다`() {
        // Given
        val releasedOffset = 600f

        // When
        val expand = StoreDetailSheetSpec.shouldExpand(currentOffset = releasedOffset, tipOffset = tipOffset, velocityY = 0f)

        // Then
        assertTrue(expand)
    }

    // TH-1226 TC2, TH-1374
    @Test
    fun `TH1374_조금만_끌어올려도_위로_튕겨_예측위치가_중간을_넘으면_full로_간다`() {
        // Given
        val releasedOffset = 1300f
        val weakFling = -400f
        val strongFling = -1500f

        // When
        val weak = StoreDetailSheetSpec.shouldExpand(currentOffset = releasedOffset, tipOffset = tipOffset, velocityY = weakFling)
        val strong = StoreDetailSheetSpec.shouldExpand(currentOffset = releasedOffset, tipOffset = tipOffset, velocityY = strongFling)

        // Then
        assertFalse(weak)
        assertTrue(strong)
    }

    // TH-1226 TC4
    @Test
    fun `TH1226_TC4_full에서_끌어내렸다_놓으면_가까운_tip으로_스냅한다`() {
        // Given
        val releasedOffset = 900f

        // When
        val expand = StoreDetailSheetSpec.shouldExpand(currentOffset = releasedOffset, tipOffset = tipOffset, velocityY = 0f)
        val flingDown = StoreDetailSheetSpec.shouldExpand(currentOffset = 100f, tipOffset = tipOffset, velocityY = 2000f)

        // Then
        assertFalse(expand)
        assertFalse(flingDown)
    }

    // TH-1226 TC2
    @Test
    fun `TH1226_TC2_full이_네비높이만큼_내려와_있어도_tip과_full의_중간을_기준으로_스냅한다`() {
        // Given
        val fullOffset = 168f
        val midpoint = (tipOffset + fullOffset) / 2f

        // When
        val justAbove = StoreDetailSheetSpec.shouldExpand(midpoint - 1f, tipOffset, velocityY = 0f, fullOffset = fullOffset)
        val justBelow = StoreDetailSheetSpec.shouldExpand(midpoint + 1f, tipOffset, velocityY = 0f, fullOffset = fullOffset)

        // Then
        assertTrue(justAbove)
        assertFalse(justBelow)
    }

    // TH-1226 TC2, TC4
    @Test
    fun `TH1226_TC2_시트가_올라온_만큼_상단네비가_진해지고_tip에서는_보이지_않는다`() {
        // Given
        val fullOffset = 168f

        // When
        val atTip = StoreDetailSheetSpec.expandProgress(currentOffset = tipOffset, tipOffset = tipOffset, fullOffset = fullOffset)
        val halfway = StoreDetailSheetSpec.expandProgress(currentOffset = 784f, tipOffset = tipOffset, fullOffset = fullOffset)
        val atFull = StoreDetailSheetSpec.expandProgress(currentOffset = fullOffset, tipOffset = tipOffset, fullOffset = fullOffset)

        // Then
        assertEquals(0f, atTip, 0.001f)
        assertEquals(0.5f, halfway, 0.001f)
        assertEquals(1f, atFull, 0.001f)
    }
}
