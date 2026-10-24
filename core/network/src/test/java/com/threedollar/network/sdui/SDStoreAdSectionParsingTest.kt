package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.component.SDUnknownCardModel
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreAdSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SDStoreAdSectionParsingTest {

    private val gson = SDUIGson.provideGson()

    private fun load(name: String): SDStoreScreenModel {
        val json = requireNotNull(javaClass.classLoader?.getResource("sdui/store-v2/$name")).readText()
        return gson.fromJson(json, SDStoreScreenModel::class.java)
    }

    // TH-1529 TC3
    @Test
    fun `TH1529_TC3_상세_AD섹션의_sectionId_cards_impressionLog_style을_서버순서대로_파싱한다`() {
        // Given
        val fileName = "StoreScreenV2WithAdSection.json"

        // When
        val screen = load(fileName)

        // Then
        val sections = screen.sections.orEmpty()
        assertEquals(listOf(SDSectionType.CTA, SDSectionType.AD, SDSectionType.AD_MOB), sections.map { it.type })
        val ad = sections[1] as SDStoreAdSectionModel
        assertEquals("AD", ad.sectionId)
        assertEquals("#FFFFFF", ad.style?.backgroundColor)
        assertEquals("section", ad.impressionLog?.objectType)
        assertEquals("AD", ad.impressionLog?.extraParameters?.get("section_type"))
        val card = requireNotNull(ad.firstAdCard)
        assertEquals("AD:108", card.cardId)
        assertEquals("가3팀이 붕어빵을 판다?", card.title?.text)
        assertEquals("익선동으로 많이 와주세요", card.subTitle?.text)
        assertEquals("광고", card.labels.orEmpty().single().text?.text)
        assertEquals("#FFEFEF", card.labels.orEmpty().single().style?.backgroundColor)
        assertNotNull(card.image?.url)
        assertEquals("https://storage.threedollars.co.kr/app/chevron_right-2.png", card.trailingIcon?.url)
        assertEquals(SDLinkType.WEB, card.link?.type)
        assertEquals("advertisement", card.clickLog?.objectId)
        assertEquals("IMPRESSION", card.impressionLog?.eventType)
    }

    // TH-1529 TC7
    @Test
    fun `TH1529_TC7_선택필드가_없는_상세_광고카드도_파싱되고_없는요소만_null이다`() {
        // Given
        val fileName = "StoreScreenV2WithAdSectionVariants.json"

        // When
        val ad = load(fileName).sections.orEmpty().filterIsInstance<SDStoreAdSectionModel>().single()

        // Then
        val card = requireNotNull(ad.firstAdCard)
        assertNull(card.title)
        assertNull(card.subTitle)
        assertNull(card.image)
        assertNull(card.trailingIcon)
        assertNull(card.link)
        assertEquals("광고", card.labels.orEmpty().single().text?.text)
        assertNotNull(card.clickLog)
    }

    // TH-1529 TC12
    @Test
    fun `TH1529_TC12_AD와_AD_MOB이_함께오면_서버순서대로_둘다_남는다`() {
        // Given
        val fileName = "StoreScreenV2WithAdSection.json"

        // When
        val sections = load(fileName).sections.orEmpty()

        // Then
        val adIndex = sections.indexOfFirst { it is SDStoreAdSectionModel }
        val adMobIndex = sections.indexOfFirst { it is SDStoreAdmobSectionModel }
        assertTrue(adIndex in 0 until adMobIndex)
    }

    // TH-1529 TC13
    @Test
    fun `TH1529_TC13_알수없는_섹션과_카드타입은_무시하고_광고섹션은_첫_광고카드를_쓴다`() {
        // Given
        val fileName = "StoreScreenV2WithAdSectionVariants.json"

        // When
        val sections = load(fileName).sections.orEmpty()

        // Then
        assertEquals(
            listOf(SDSectionType.CTA, SDSectionType.UNKNOWN, SDSectionType.AD, SDSectionType.AD_MOB),
            sections.map { it.type },
        )
        val ad = sections[2] as SDStoreAdSectionModel
        assertTrue(ad.cards.orEmpty().first() is SDUnknownCardModel)
        assertEquals("AD:108", ad.firstAdCard?.cardId)
        assertTrue(ad.firstAdCard is SDAdCardModel)
    }
}
