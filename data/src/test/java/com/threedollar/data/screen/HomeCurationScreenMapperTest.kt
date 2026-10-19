package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.element.SDImageAlignment
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeCurationScreenMapperTest {
    private val gson = Gson()

    private fun section(name: String): HomeCurationSectionResponse = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        HomeCurationSectionResponse::class.java,
    )

    private fun cards(name: String): HomeCurationCardsResponse = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        HomeCurationCardsResponse::class.java,
    )

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_실서버섹션이면_캐러셀순서_기본카테고리와_초기카드를_보존한다`() {
        // Given
        val response = section("HomeCurationSectionWithDevice.json")

        // When
        val model = response.asCurationModel()

        // Then
        val carousels = model.items.filterIsInstance<SDHomeCurationItemModel.Carousel>()
        assertEquals(listOf("POPULAR_SNACKS", "TASTE_SNACKS"), carousels.map { it.carouselId })
        assertEquals(listOf("BUNGEOPPANG", "GRANDMA_TASTE"), carousels.map { it.defaultCategoryId })
        assertEquals(listOf("BUNGEOPPANG", "WAKBBU_SALT_BREAD", "FRUIT_SANDO", "PIZZA_SEOLGI"), carousels.first().categoryFilters.map { it.categoryId })
        assertEquals(response.items.orEmpty().first { it.type == "CAROUSEL" }.cards.orEmpty().map { it.cardId }, carousels.first().cards.map { it.cardId })
        assertTrue(carousels.first().cards.filterIsInstance<ImagePreviewCardModel>().isNotEmpty())
        assertTrue(carousels.first().header?.title?.isHtml == true)
        assertTrue(carousels.first().header?.title?.text.orEmpty().contains("font-size:20px"))
        assertNull(carousels.first().header?.subTitle)
        assertNull(carousels.first().header?.trailingAction)
        assertEquals(1f, carousels.first().categoryFilters.first().selected?.style?.border?.width)
        assertEquals("CLICK", carousels.first().categoryFilters.first().clickLog?.eventType)
    }

    // TH-1401 TC4 / TC6
    @Test
    fun `TH1401_TC6_실서버가게카드면_HTML_메타_링크와_로그를_보존한다`() {
        // Given
        val response = section("HomeCurationSectionWithDevice.json")

        // When
        val carousels = response.asCurationModel().items.filterIsInstance<SDHomeCurationItemModel.Carousel>()

        // Then
        assertEquals(2, carousels.size)
        val card = carousels.first().cards.filterIsInstance<ImagePreviewCardModel>().first()
        assertEquals(100f, card.image?.style?.width)
        assertEquals(100f, card.image?.style?.height)
        assertTrue(card.title?.isHtml == true)
        assertTrue(card.title?.text.orEmpty().contains("font-size:14px"))
        assertEquals(2, card.metricLabel.orEmpty().size)
        assertEquals(1, card.contextLabel.orEmpty().size)
        assertEquals(SDImageAlignment.START, card.metricLabel?.first()?.imageAlignment)
        assertEquals(2f, card.metricLabel?.first()?.contentSpacing)
        assertEquals(SDLinkType.APP_SCHEME, card.link?.type)
        assertNotNull(card.link?.link)
        assertEquals("CLICK", card.clickLog?.eventType)
        assertEquals("home", card.clickLog?.screenName)
        assertEquals("CURATION", card.clickLog?.extraParameters?.get("tabId"))
        assertEquals("POPULAR_SNACKS", card.clickLog?.extraParameters?.get("carouselId"))
        assertEquals("BUNGEOPPANG", card.clickLog?.extraParameters?.get("categoryId"))
        assertEquals("47", card.clickLog?.extraParameters?.get("store_id"))
        assertTrue(card.clickLog?.extraParameters?.get("store_id") is String)
        assertTrue(card.refs.orEmpty().isEmpty())
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_기기위치없는응답이면_거리칩을_만들지않고_리뷰와평점을_보존한다`() {
        // Given
        val response = section("HomeCurationSectionWithoutDevice.json")

        // When
        val carousels = response.asCurationModel().items.filterIsInstance<SDHomeCurationItemModel.Carousel>()

        // Then
        assertEquals(2, carousels.size)
        val card = carousels.first().cards.filterIsInstance<ImagePreviewCardModel>().first()
        assertEquals(1, card.metricLabel.orEmpty().size)
        assertTrue(card.metricLabel?.single()?.text?.text.orEmpty().contains("개"))
        assertEquals(1, card.contextLabel.orEmpty().size)
    }

    // TH-1401 TC5 / TC7
    @Test
    fun `TH1401_TC7_실서버카드목록이면_가게와광고순서_높이와_노출로그를_보존한다`() {
        // Given
        val response = cards("HomeCurationCardsWithDevice.json")

        // When
        val model = response.asCurationCardsModel()

        // Then
        assertEquals(response.cards.orEmpty().map { it.cardId }, model.cards.map { it.cardId })
        assertTrue(model.cards.filterIsInstance<ImagePreviewCardModel>().isNotEmpty())
        val ads = model.cards.filterIsInstance<SDAdMobCardModel>()
        assertTrue(ads.isNotEmpty())
        ads.forEach { ad ->
            assertEquals(100, ad.height)
            assertEquals("CLICK", ad.clickLog?.eventType)
            assertEquals("IMPRESSION", ad.impressionLog?.eventType)
            assertEquals("CURATION", ad.impressionLog?.extraParameters?.get("tabId"))
        }
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_캐러셀사이광고면_서버높이와_로그를_보존한다`() {
        // Given
        val response = section("HomeCurationSectionWithDevice.json")

        // When
        val ads = response.asCurationModel().items.filterIsInstance<SDHomeCurationItemModel.AdMob>()

        // Then
        assertEquals(1, ads.size)
        val ad = ads.first().card
        assertEquals(50, ad.height)
        assertEquals("CLICK", ad.clickLog?.eventType)
        assertEquals("IMPRESSION", ad.impressionLog?.eventType)
        assertEquals("CURATION", ad.impressionLog?.extraParameters?.get("tabId"))
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_실데이터에_미지원타입이추가돼도_다른캐러셀과카드는_보존한다`() {
        // Given
        val response = section("HomeCurationSectionWithUnknownTypes.json")

        // When
        val model = response.asCurationModel()

        // Then
        assertTrue(model.items.any { it is SDHomeCurationItemModel.Unknown && it.rawType == "FUTURE_ITEM" })
        val carousels = model.items.filterIsInstance<SDHomeCurationItemModel.Carousel>()
        assertEquals(listOf("POPULAR_SNACKS", "TASTE_SNACKS"), carousels.map { it.carouselId })
        assertTrue(carousels.first().cards.filterIsInstance<ImagePreviewCardModel>().isNotEmpty())
        assertFalse(carousels.first().cards.any { it.cardId == "FUTURE_CARD" })
        val firstCard = carousels.first().cards.filterIsInstance<ImagePreviewCardModel>().first()
        assertNull(firstCard.link?.type)
        assertNull(firstCard.metricLabel?.first()?.imageAlignment)
    }
    // TH-1517 TC3
    @Test
    fun `TH1517_TC3_카테고리가빈배열이면_빈카테고리캐러셀로_카드는보존한다`() {
        // Given
        val response = gson.fromJson(carouselJson(""""defaultCategoryId": "", "categoryFilters": [],"""), HomeCurationSectionResponse::class.java)

        // When
        val carousel = response.asCurationModel().items.single() as SDHomeCurationItemModel.Carousel

        // Then
        assertTrue(carousel.categoryFilters.isEmpty())
        assertEquals(listOf("S:1"), carousel.cards.map { it.cardId })
    }

    @Test
    fun `TH1517_TC3_카테고리필드가없어도_캐러셀과카드를_보존한다`() {
        // Given
        val response = gson.fromJson(carouselJson(""), HomeCurationSectionResponse::class.java)

        // When
        val carousel = response.asCurationModel().items.single() as SDHomeCurationItemModel.Carousel

        // Then
        assertTrue(carousel.categoryFilters.isEmpty())
        assertEquals("", carousel.defaultCategoryId)
        assertEquals(listOf("S:1"), carousel.cards.map { it.cardId })
    }

    private fun carouselJson(categoryFields: String) = """
        {
          "items": [
            {
              "type": "CAROUSEL",
              "carouselId": "SNS_STORES",
              "header": { "title": { "text": "SNS에 나온 가게", "isHtml": false, "fontColor": "#0F0F0F" } },
              $categoryFields
              "cards": [
                {
                  "type": "IMAGE_PREVIEW_CARD",
                  "cardId": "S:1",
                  "image": { "url": "https://example.com/a.png", "style": { "width": 100.0, "height": 100.0 } },
                  "title": { "text": "가게", "isHtml": false, "fontColor": "#0F0F0F" },
                  "metricLabel": [],
                  "contextLabel": [],
                  "link": { "type": "APP_SCHEME", "link": "/store?storeId=1&storeType=USER_STORE" }
                }
              ]
            }
          ]
        }
    """.trimIndent()
}
