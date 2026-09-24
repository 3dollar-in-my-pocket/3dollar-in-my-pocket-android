package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreCouponSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDUnknownSectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SDStoreScreenParsingTest {

    private val gson = SDUIGson.provideGson()

    private fun load(name: String): SDStoreScreenModel {
        val json = requireNotNull(javaClass.classLoader?.getResource("sdui/store-v2/$name")).readText()
        return gson.fromJson(json, SDStoreScreenModel::class.java)
    }

    // TH-1226 TC2
    @Test
    fun `TH1226_TC2_제보가게_응답이면_섹션을_서버순서대로_파싱한다`() {
        // Given
        val fileName = "StoreScreenV2UserStore.json"

        // When
        val screen = load(fileName)

        // Then
        val types = screen.sections.orEmpty().map { it.type }
        assertEquals(
            listOf(
                SDSectionType.PREVIEW, SDSectionType.MARGIN, SDSectionType.TAB, SDSectionType.EDIT,
                SDSectionType.INFO_V1, SDSectionType.CTA, SDSectionType.MARGIN, SDSectionType.AD_MOB,
                SDSectionType.MARGIN, SDSectionType.VISIT, SDSectionType.MARGIN, SDSectionType.IMAGE,
                SDSectionType.MARGIN, SDSectionType.REVIEW, SDSectionType.MARGIN, SDSectionType.RELATED_STORES,
                SDSectionType.MARGIN
            ),
            types
        )
        assertEquals("store_detail", screen.viewLog?.screenName)
    }

    // TH-1226 TC16
    @Test
    fun `TH1226_TC16_모르는섹션타입이_있으면_그섹션만_Unknown으로_떨어지고_나머지는_파싱된다`() {
        // Given
        val fileName = "StoreScreenV2WithUnknownTypes.json"

        // When
        val screen = load(fileName)

        // Then
        val sections = screen.sections.orEmpty()
        val unknown = sections.filterIsInstance<SDUnknownSectionModel>()
        assertEquals(1, unknown.size)
        assertEquals("NEW_FANCY_SECTION", unknown.single().rawType)
        assertEquals(16, sections.count { it.type != SDSectionType.UNKNOWN })
    }

    // TH-1226 TC16
    @Test
    fun `TH1226_TC16_모르는정보행타입은_type이_null이고_다른행은_그대로다`() {
        // Given
        val fileName = "StoreScreenV2WithUnknownTypes.json"

        // When
        val info = load(fileName).sections.orEmpty().filterIsInstance<SDStoreInfoV1SectionModel>().single()

        // Then
        val rows = info.informationCard?.rows.orEmpty()
        assertNull(rows.first().type)
        assertTrue(rows.drop(1).all { it.type != null })
    }

    // TH-1226 TC20
    @Test
    fun `TH1226_TC20_사장님가게_응답도_같은_섹션모델로_파싱된다`() {
        // Given
        val fileName = "StoreScreenV2BossStore.json"

        // When
        val screen = load(fileName)

        // Then
        val preview = screen.sections.orEmpty().filterIsInstance<SDStorePreviewSectionModel>().single()
        assertEquals("BOSS_STORE", preview.additionalInfos?.storeType)
        assertEquals(SDSectionType.CALLOUT, screen.sections?.first()?.type)
        assertTrue(screen.sections.orEmpty().any { it.type == SDSectionType.POST })
        assertTrue(screen.sections.orEmpty().none { it.type == SDSectionType.UNKNOWN })
    }

    // TH-1226 TC12
    @Test
    fun `TH1226_TC12_공유액션의_URL과_숫자로_온_좌표를_문자열로_꺼낼수있다`() {
        // Given
        val preview = load("StoreScreenV2UserStore.json").sections.orEmpty()
            .filterIsInstance<SDStorePreviewSectionModel>().single()

        // When
        val actions = preview.actionBars.orEmpty().mapNotNull { it.button?.customAction }
        val share = actions.single { it.actionType == SDCustomActionType.STORE_PREVIEW_SECTION_SHARE }
        val navigation = actions.single { it.actionType == SDCustomActionType.STORE_PREVIEW_SECTION_NAVIGATION }

        // Then
        assertTrue(share.param(SDCustomActionModel.URL).orEmpty().startsWith("http"))
        assertNotNull(navigation.doubleParam(SDCustomActionModel.LATITUDE))
        assertTrue(navigation.param(SDCustomActionModel.LATITUDE).orEmpty().contains("."))
    }

    @Test
    fun `쿠폰섹션과_연관가게카드를_파싱한다`() {
        // Given
        val fileName = "StoreScreenV2WithCoupon.json"

        // When
        val sections = load(fileName).sections.orEmpty()

        // Then
        val coupon = sections.filterIsInstance<SDStoreCouponSectionModel>().single()
        assertTrue(coupon.cards.orEmpty().isNotEmpty())
        assertNotNull(coupon.cards?.first()?.trailingButton)
        val related = sections.filterIsInstance<SDRelatedStoresSectionModel>().single()
        assertTrue(related.cards.all { it is ImagePreviewCardModel })
    }

    @Test
    fun `모르는_customAction_타입은_null로_떨어진다`() {
        // Given
        val json = """{"sections":[{"type":"IMAGE","sectionId":"IMAGE","cards":[{"cardId":"I:1",
            |"customAction":{"actionType":"NEW_ACTION","extraParams":{"STORE_ID":"1"}}}]}]}""".trimMargin()

        // When
        val screen = gson.fromJson(json, SDStoreScreenModel::class.java)

        // Then
        val card = (screen.sections?.single() as SDStoreImageSectionModel)
            .cards?.single()
        assertNull(card?.customAction?.actionType)
        assertEquals("1", card?.customAction?.param(SDCustomActionModel.STORE_ID))
    }
}
