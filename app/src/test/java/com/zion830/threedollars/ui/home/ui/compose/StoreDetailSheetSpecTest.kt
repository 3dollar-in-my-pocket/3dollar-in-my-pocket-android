package com.zion830.threedollars.ui.home.ui.compose

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

    // TH-1226 TC2
    @Test
    fun `TH1226_TC2_조금만_끌어올려도_위로_빠르게_튕기면_full로_간다`() {
        // Given
        val releasedOffset = 1300f

        // When
        val expand = StoreDetailSheetSpec.shouldExpand(currentOffset = releasedOffset, tipOffset = tipOffset, velocityY = -2000f)

        // Then
        assertTrue(expand)
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
}
