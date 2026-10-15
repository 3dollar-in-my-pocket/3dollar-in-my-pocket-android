package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SDUISectionLogMapperTest {
    private val gson = Gson()

    private fun <T> load(name: String, type: Class<T>): T = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        type,
    )

    // TH-1467 TC1
    @Test
    fun `TH1467_TC1_큐레이션섹션응답의_노출로그를_서버값그대로_매핑한다`() {
        // Given
        val response = load("HomeCurationSectionWithLogs.json", HomeCurationSectionResponse::class.java)

        // When
        val model = response.asCurationModel()

        // Then
        val log = requireNotNull(model.impressionLog)
        assertEquals("IMPRESSION", log.eventType)
        assertEquals("home", log.screenName)
        assertEquals("section", log.objectType)
        assertEquals("home_curation", log.objectId)
        assertEquals("CURATION", log.extraParameters?.get("tabId"))
    }

    // TH-1467 TC2
    @Test
    fun `TH1467_TC2_리스트섹션응답의_노출로그를_서버값그대로_매핑한다`() {
        // Given
        val response = load("HomeListSectionWithSectionLog.json", HomeListSectionResponse::class.java)

        // When
        val model = response.asModel()

        // Then
        val log = requireNotNull(model.impressionLog)
        assertEquals("home", log.screenName)
        assertEquals("section", log.objectType)
        assertEquals("home_list", log.objectId)
        assertEquals("15", log.extraParameters["store_card_count"]?.anyValue)
    }

    // TH-1467 TC2
    @Test
    fun `TH1467_TC2_노출로그가없는리스트섹션은_null로_매핑한다`() {
        // Given
        val response = load("HomeListEmptyCard.json", HomeListSectionResponse::class.java)

        // When
        val model = response.asModel()

        // Then
        assertNull(model.impressionLog)
    }

    // TH-1467 TC3
    @Test
    fun `TH1467_TC3_큐레이션가게카드의_노출로그를_섹션과_카테고리카드응답모두에서_매핑한다`() {
        // Given
        val section = load("HomeCurationSectionWithLogs.json", HomeCurationSectionResponse::class.java)
        val cards = load("HomeCurationCardsWithLogs.json", HomeCurationCardsResponse::class.java)

        // When
        val sectionCard = section.asCurationModel().items
            .filterIsInstance<SDHomeCurationItemModel.Carousel>().first()
            .cards.filterIsInstance<ImagePreviewCardModel>().first()
        val categoryCard = cards.asCurationCardsModel().cards.filterIsInstance<ImagePreviewCardModel>().first()

        // Then
        assertEquals("card", sectionCard.impressionLog?.objectType)
        assertEquals("store", sectionCard.impressionLog?.objectId)
        assertEquals("BUNGEOPPANG", sectionCard.impressionLog?.extraParameters?.get("categoryId"))
        assertEquals("FRUIT_SANDO", categoryCard.impressionLog?.extraParameters?.get("categoryId"))
        assertTrue(categoryCard.impressionLog?.extraParameters?.containsKey("position") == true)
    }

    // TH-1467 TC3
    @Test
    fun `TH1467_TC3_노출로그가없는큐레이션카드는_null로_매핑한다`() {
        // Given
        val response = load("HomeCurationSectionWithDevice.json", HomeCurationSectionResponse::class.java)

        // When
        val card = response.asCurationModel().items
            .filterIsInstance<SDHomeCurationItemModel.Carousel>().first()
            .cards.filterIsInstance<ImagePreviewCardModel>().first()

        // Then
        assertNull(card.impressionLog)
    }
}
