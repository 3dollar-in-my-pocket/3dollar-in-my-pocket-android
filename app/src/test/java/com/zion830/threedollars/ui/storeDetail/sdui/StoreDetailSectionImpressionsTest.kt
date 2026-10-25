package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.component.SDUnknownCardModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDStoreAdSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSectionImpressions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailSectionImpressionsTest {
    private val sectionLog = SDLogModel(screenName = "store_detail", objectType = "section", objectId = "store_section")
    private val adCardLog = SDLogModel(screenName = "store_detail", objectType = "card", objectId = "admob")

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_섹션노출로그가있으면_섹션키로_한건을만든다`() {
        // Given
        val section = SDStoreMarginSectionModel(sectionId = "MARGIN", height = 8, style = null, impressionLog = sectionLog)

        // When
        val logs = StoreDetailSectionImpressions.logs("3-MARGIN", section)

        // Then
        assertEquals(listOf("MARGIN" to sectionLog), logs)
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_섹션노출로그가없으면_아무것도보내지않는다`() {
        // Given
        val section = SDStoreMarginSectionModel(sectionId = "MARGIN", height = 8, style = null)

        // When
        val logs = StoreDetailSectionImpressions.logs("3-MARGIN", section)

        // Then
        assertTrue(logs.isEmpty())
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_광고섹션은_섹션노출과_기존광고카드노출을_다른키로_함께만든다`() {
        // Given
        val section = SDStoreAdmobSectionModel(
            sectionId = "AD_MOB_TOP",
            cards = listOf(SDStoreAdmobSectionModel.Card(type = "ADMOB_CARD", cardId = "ad", clickLog = null, impressionLog = adCardLog)),
            style = null,
            impressionLog = sectionLog,
        )

        // When
        val logs = StoreDetailSectionImpressions.logs("2-AD_MOB", section)

        // Then
        assertEquals(listOf("AD_MOB_TOP" to sectionLog, "AD_MOB_TOP-admob-card" to adCardLog), logs)
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_섹션위치가바뀌어도_같은섹션은_같은중복방지키를쓴다`() {
        // Given
        val section = SDStoreMarginSectionModel(sectionId = "COUPON", height = 8, style = null, impressionLog = sectionLog)

        // When
        val before = StoreDetailSectionImpressions.logs("4-COUPON", section)
        val afterShift = StoreDetailSectionImpressions.logs("5-COUPON", section)

        // Then
        assertEquals(before.map { it.first }, afterShift.map { it.first })
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_섹션아이디가없으면_목록키를_중복방지키로쓴다`() {
        // Given
        val section = SDStoreMarginSectionModel(sectionId = null, height = 8, style = null, impressionLog = sectionLog)

        // When
        val logs = StoreDetailSectionImpressions.logs("3-MARGIN", section)

        // Then
        assertEquals(listOf("3-MARGIN" to sectionLog), logs)
    }

    // TH-1529 TC11
    @Test
    fun `TH1529_TC11_AD섹션이_보이면_섹션과_첫광고카드_노출로그를_한건씩_만든다`() {
        // Given
        val firstCardLog = SDLogModel(screenName = "store_detail", objectType = "card", objectId = "advertisement")
        val secondCardLog = SDLogModel(screenName = "store_detail", objectType = "card", objectId = "advertisement-2")
        val section = SDStoreAdSectionModel(
            sectionId = "AD",
            cards = listOf(
                SDAdCardModel(cardId = "AD:108", impressionLog = firstCardLog),
                SDAdCardModel(cardId = "AD:109", impressionLog = secondCardLog),
            ),
            style = null,
            impressionLog = sectionLog,
        )

        // When
        val logs = StoreDetailSectionImpressions.logs("9-AD", section)

        // Then
        assertEquals(listOf("AD" to sectionLog, "AD-ad-card" to firstCardLog), logs)
    }

    // TH-1529 TC13
    @Test
    fun `TH1529_TC13_그릴_광고카드가_없는_AD섹션은_노출로그를_보내지_않는다`() {
        // Given
        val section = SDStoreAdSectionModel(
            sectionId = "AD",
            cards = listOf(SDUnknownCardModel(cardId = "NEW:1")),
            style = null,
            impressionLog = sectionLog,
        )

        // When
        val logs = StoreDetailSectionImpressions.logs("9-AD", section)

        // Then
        assertTrue(logs.isEmpty())
    }
}
