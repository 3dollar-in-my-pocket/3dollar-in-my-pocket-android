package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDUnknownSectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSectionImpressions
import com.zion830.threedollars.ui.storeDetail.sdui.model.StorePreviewSections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StorePreviewSectionsTest {

    private val screen: SDStoreScreenModel by lazy {
        val json = requireNotNull(javaClass.classLoader?.getResource("sdui/StorePreviewWithAdMob.json")).readText()
        SDUIGson.provideGson().fromJson(json, SDStoreScreenModel::class.java)
    }

    // TH-1592 TC1
    @Test
    fun `TH1592_TC1_AD_MOB이_맨위인_응답이면_배너가_PREVIEW보다_먼저_그려진다`() {
        // Given
        val sections = screen.sections

        // When
        val previewSections = StorePreviewSections.from(sections)

        // Then
        assertEquals(listOf(SDSectionType.AD_MOB, SDSectionType.PREVIEW), previewSections.map { it.type })
        val card = (previewSections.first() as SDStoreAdmobSectionModel).cards.orEmpty().single()
        assertEquals("ca-app-pub-5385646520024289/7789991453", card.adUnitId)
        assertEquals(50, card.slotHeightDp)
    }

    // TH-1592 TC2
    @Test
    fun `TH1592_TC2_AD_MOB이_PREVIEW아래에_있으면_서버순서대로_배너가_아래에_그려진다`() {
        // Given
        val (adMob, others) = screen.sections.orEmpty().partition { it is SDStoreAdmobSectionModel }
        val sections = others + adMob

        // When
        val previewSections = StorePreviewSections.from(sections)

        // Then
        assertEquals(listOf(SDSectionType.PREVIEW, SDSectionType.AD_MOB), previewSections.map { it.type })
    }

    // TH-1592 TC3
    @Test
    fun `TH1592_TC3_AD_MOB이_없는_응답이면_PREVIEW만_남아_기존과_같다`() {
        // Given
        val sections = screen.sections.orEmpty().filterNot { it is SDStoreAdmobSectionModel }

        // When
        val previewSections = StorePreviewSections.from(sections)

        // Then
        assertEquals(listOf(SDSectionType.PREVIEW), previewSections.map { it.type })
    }

    // TH-1592 TC6
    @Test
    fun `TH1592_TC6_미리보기광고_노출로그는_섹션과_카드를_서버값으로_만들고_상세광고키와_겹치지_않는다`() {
        // Given
        val adMob = screen.sections.orEmpty().filterIsInstance<SDStoreAdmobSectionModel>().single()

        // When
        val previewLogs = StorePreviewSections.impressionLogs(index = 0, section = adMob)
        val detailLogs = StoreDetailSectionImpressions.logs("0-AD_MOB", adMob)

        // Then
        assertEquals(listOf("preview-AD_MOB", "preview-AD_MOB-admob-card"), previewLogs.map { it.first })
        assertEquals(listOf(adMob.impressionLog, adMob.cards?.single()?.impressionLog), previewLogs.map { it.second })
        assertTrue(previewLogs.all { it.second.screenName == "store_bottom_sheet" && it.second.eventType == "IMPRESSION" })
        assertTrue(previewLogs.map { it.first }.intersect(detailLogs.map { it.first }.toSet()).isEmpty())
    }

    // TH-1592 TC7
    @Test
    fun `TH1592_TC7_미리보기광고_카드의_클릭로그는_서버값_그대로다`() {
        // Given
        val adMob = StorePreviewSections.from(screen.sections).filterIsInstance<SDStoreAdmobSectionModel>().single()

        // When
        val clickLog = adMob.cards?.single()?.clickLog

        // Then
        assertEquals("store_bottom_sheet", clickLog?.screenName)
        assertEquals("admob", clickLog?.objectId)
        assertEquals("CLICK", clickLog?.eventType)
    }

    // TH-1592 TC8
    @Test
    fun `TH1592_TC8_모르는_PREVIEW_MORE섹션은_Unknown으로_파싱되고_미리보기에서만_빠진다`() {
        // Given
        val sections = screen.sections.orEmpty()

        // When
        val previewSections = StorePreviewSections.from(sections)

        // Then
        assertEquals("PREVIEW_MORE", sections.filterIsInstance<SDUnknownSectionModel>().single().rawType)
        assertEquals(listOf(SDSectionType.AD_MOB, SDSectionType.PREVIEW), previewSections.map { it.type })
    }
}
