package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewMoreSectionModel
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

        // Then (TH-1584 이후 하단 티저 PREVIEW_MORE 가 맨 끝에 붙는다)
        assertEquals(listOf(SDSectionType.AD_MOB, SDSectionType.PREVIEW, SDSectionType.PREVIEW_MORE), previewSections.map { it.type })
        val card = (previewSections.first() as SDStoreAdmobSectionModel).cards.orEmpty().single()
        assertEquals("ca-app-pub-5385646520024289/7789991453", card.adUnitId)
        assertEquals(50, card.slotHeightDp)
    }

    // TH-1592 TC2
    @Test
    fun `TH1592_TC2_AD_MOB이_PREVIEW아래에_있으면_서버순서대로_배너가_아래에_그려진다`() {
        // Given
        val (adMob, others) = screen.sections.orEmpty()
            .filterNot { it is SDStorePreviewMoreSectionModel }
            .partition { it is SDStoreAdmobSectionModel }
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
        val sections = screen.sections.orEmpty().filterNot { it is SDStoreAdmobSectionModel || it is SDStorePreviewMoreSectionModel }

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

    // TH-1584 TC1 (TH-1592 TC8 "PREVIEW_MORE 는 Unknown 으로 빠진다" 를 대체)
    @Test
    fun `TH1584_TC1_PREVIEW_MORE는_제목과_배경을_가진_티저로_파싱된다`() {
        // Given
        val sections = screen.sections.orEmpty()

        // When
        val more = sections.filterIsInstance<SDStorePreviewMoreSectionModel>().single()

        // Then
        assertTrue(sections.none { it is SDUnknownSectionModel })
        assertEquals("PREVIEW_MORE", more.sectionId)
        assertTrue(more.header?.title?.text.orEmpty().contains("가게 정보 &amp; 메뉴"))
        assertEquals("#FFFFFF", more.style?.backgroundColor)
    }

    // TH-1584 TC2
    @Test
    fun `TH1584_TC2_PREVIEW_MORE는_서버위치와_상관없이_맨끝에_하나만_붙는다`() {
        // Given
        val all = screen.sections.orEmpty()
        val preview = all.first { it.type == SDSectionType.PREVIEW }
        val admob = all.first { it.type == SDSectionType.AD_MOB }
        val more = all.first { it.type == SDSectionType.PREVIEW_MORE }

        // When
        val sections = StorePreviewSections.from(listOf(more, preview, more, admob))

        // Then
        assertEquals(listOf(SDSectionType.PREVIEW, SDSectionType.AD_MOB, SDSectionType.PREVIEW_MORE), sections.map { it.type })
    }

    // TH-1584 TC3
    @Test
    fun `TH1584_TC3_PREVIEW_MORE가_없으면_기존과_같고_PREVIEW가_없으면_티저만_붙지_않는다`() {
        // Given
        val all = screen.sections.orEmpty()
        val preview = all.first { it.type == SDSectionType.PREVIEW }
        val more = all.first { it.type == SDSectionType.PREVIEW_MORE }

        // When
        val withoutMore = StorePreviewSections.from(listOf(preview))
        val withoutPreview = StorePreviewSections.from(listOf(more))

        // Then
        assertEquals(listOf(SDSectionType.PREVIEW), withoutMore.map { it.type })
        assertTrue(withoutPreview.isEmpty())
    }

    // TH-1584 TC4
    @Test
    fun `TH1584_TC4_티저노출로그는_서버값이_있을때만_미리보기접두키로_보낸다`() {
        // Given
        val more = screen.sections.orEmpty().filterIsInstance<SDStorePreviewMoreSectionModel>().single()
        val log = SDLogModel(screenName = "store_bottom_sheet", eventType = "IMPRESSION", objectType = "section", objectId = "PREVIEW_MORE")

        // When
        val withoutLog = StorePreviewSections.impressionLogs(2, more)
        val withLog = StorePreviewSections.impressionLogs(2, more.copy(impressionLog = log))

        // Then
        assertTrue(withoutLog.isEmpty())
        assertEquals(listOf("${StorePreviewSections.IMPRESSION_KEY_PREFIX}PREVIEW_MORE" to log), withLog)
    }

    // TH-1584 TC5
    @Test
    fun `TH1584_TC5_시트에_알리는_높이는_티저를_빼꼼높이까지만_넣고_티저가_없으면_본문그대로다`() {
        // Given
        val bodyPx = 900
        val maxPeekPx = 156

        // When
        val tallPeek = StorePreviewSections.peekLayoutHeight(bodyPx = bodyPx, peekPx = 400, maxPeekPx = maxPeekPx)
        val shortPeek = StorePreviewSections.peekLayoutHeight(bodyPx = bodyPx, peekPx = 100, maxPeekPx = maxPeekPx)
        val noPeek = StorePreviewSections.peekLayoutHeight(bodyPx = bodyPx, peekPx = null, maxPeekPx = maxPeekPx)

        // Then
        assertEquals(1_056, tallPeek)
        assertEquals(1_000, shortPeek)
        assertEquals(900, noPeek)
    }

    // TH-1584 TC6
    @Test
    fun `TH1584_TC6_상세화면은_PREVIEW_MORE와_모르는섹션을_그리지_않는다`() {
        // Given
        val sections = screen.sections.orEmpty() + SDUnknownSectionModel("NEW_TYPE")

        // When
        val detail = StorePreviewSections.detailSections(sections)

        // Then
        assertEquals(listOf(SDSectionType.AD_MOB, SDSectionType.PREVIEW), detail.map { it.type })
    }
}
