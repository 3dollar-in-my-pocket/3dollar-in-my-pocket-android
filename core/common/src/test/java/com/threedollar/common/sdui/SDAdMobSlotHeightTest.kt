package com.threedollar.common.sdui

import com.threedollar.common.sdui.model.section.SDAdMobSlotHeight
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SDAdMobSlotHeightTest {

    // TH-1431 TC4
    @Test
    fun `TH1431_TC4_height가_50미만이면_슬롯높이를_50으로_올린다`() {
        // Given
        val height = 20

        // When
        val slotHeight = SDAdMobSlotHeight.resolve(height)

        // Then
        assertEquals(50, slotHeight)
    }

    // TH-1431 TC5
    @Test
    fun `TH1431_TC5_height가_50이상이면_서버값을_그대로_슬롯높이로_쓴다`() {
        listOf(50, 100, 250).forEach { height ->
            assertEquals(height, SDAdMobSlotHeight.resolve(height))
        }
    }

    // TH-1431 TC2
    @Test
    fun `TH1431_TC2_가게상세_카드는_height로_슬롯높이를_정한다`() {
        // Given
        val card = SDStoreAdmobSectionModel.Card(
            type = "ADMOB_CARD",
            cardId = "ADMOB:AD_MOB:1",
            clickLog = null,
            impressionLog = null,
            height = 120,
        )

        // When
        val slotHeight = card.slotHeightDp

        // Then
        assertEquals(120, slotHeight)
    }
}
