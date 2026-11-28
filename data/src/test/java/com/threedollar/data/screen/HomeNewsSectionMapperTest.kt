package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.threedollar.network.data.screen.HomeNewsSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 픽스처 `screen/HomeNewsSection.json` 은 개발계 실응답이다 (군포 37.3617,126.9352, 2026-11-27). */
class HomeNewsSectionMapperTest {

    private val gson = Gson()

    private fun realSection() = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/HomeNewsSection.json")).readText(),
        HomeNewsSectionResponse::class.java,
    ).asNewsModel()

    private fun section(json: String) = gson.fromJson(json, HomeNewsSectionResponse::class.java).asNewsModel()

    private fun contentCards() = realSection().items.filterIsInstance<SDHomeNewsItemModel.ContentCard>()

    // TH-1559 TC1
    @Test
    fun `TH1559_TC1_실서버소식섹션이면_헤더와_items순서_섹션노출로그를_보존한다`() {
        // Given
        val model = realSection()

        // When
        val ids = model.items.map { it.cardId }

        // Then
        assertTrue(model.header?.title?.text.orEmpty().contains("우리 동네 가게 소식"))
        assertEquals(true, model.header?.title?.isHtml)
        assertTrue(model.header?.subTitle?.text.orEmpty().contains("내 주변 가게들의 소식을 확인해요."))
        assertEquals(17, ids.size)
        assertEquals("ADMOB:00f3ad1c-a1fc-4f8d-b838-300a0b4a78e3", ids.first())
        assertEquals("NEWS:6b03d032fdb9ab52247e1939", ids[1])
        assertEquals("home_news", model.impressionLog?.objectId)
        assertNull(model.nextPageCursor)
    }

    // TH-1559 TC2
    @Test
    fun `TH1559_TC2_소식카드는_배지_경과시간_가게요약_본문블록을_서버값대로_매핑한다`() {
        // Given
        val card = contentCards().first { it.cardId == "NEWS:6b03d032fdb9ab52247e1939" }

        // When
        val summary = requireNotNull(card.summary)

        // Then
        assertTrue(card.badge?.text?.text.orEmpty().contains("지금 뜨는 가게!"))
        assertEquals("#0047AB", card.badge?.style?.backgroundColor)
        assertTrue(card.trailingText?.text.orEmpty().contains("4일 전"))
        assertEquals(60f, summary.leadingImage?.style?.width)
        assertTrue(summary.overline?.text.orEmpty().contains("#카페/디저트"))
        assertTrue(summary.title?.text.orEmpty().contains("ㅗㅎㅎㅎ"))
        assertEquals(3, summary.metadata.size)
        assertEquals("#F4F4F4", card.bodyBlocks.single().style?.backgroundColor)
        assertEquals(SDLinkType.APP_SCHEME, card.link?.type)
        assertEquals("/store?storeId=530045&storeType=USER_STORE&view=collapsed", card.link?.link)
        assertEquals("home_news_card", card.impressionLog?.objectId)
        assertEquals("530045", card.clickLog?.extraParameters?.get("store_id"))
    }

    // TH-1559 TC2
    @Test
    fun `TH1559_TC2_리뷰카드는_작성자메달_별점을_제보카드는_본문앞아이콘을_매핑한다`() {
        // Given
        val cards = contentCards()

        // When
        val review = cards.first { it.cardId == "NEWS:6af4a1f27af92de3599ff76b" }.bodyBlocks.single()
        val newStore = cards.first { it.cardId == "NEWS:6b01a2799602304aee00ac1a" }.bodyBlocks.single()

        // Then
        assertNotNull(review.heading?.leadingImage)
        assertTrue(review.heading?.title?.text.orEmpty().contains("뽀미"))
        assertEquals(5, review.heading?.trailingImages?.size)
        assertNull(review.textLeadingImage)
        assertNull(newStore.heading)
        assertEquals(16f, newStore.textLeadingImage?.style?.width)
    }

    // TH-1559 TC10
    @Test
    fun `TH1559_TC10_본문사진은_서버이미지크기를_그대로_들고간다`() {
        // Given
        val review = contentCards().first { it.cardId == "NEWS:6af4a1f27af92de3599ff76b" }.bodyBlocks.single()

        // When
        val sizes = review.images.map { it.style?.width to it.style?.height }

        // Then
        assertEquals(listOf(124f to 124f, 124f to 124f), sizes)
    }

    // TH-1559 TC3
    @Test
    fun `TH1559_TC3_광고카드는_서버높이와_광고단위_로그를_매핑한다`() {
        // Given
        val model = realSection()

        // When
        val ad = model.items.filterIsInstance<SDHomeNewsItemModel.AdMob>().single().card

        // Then
        assertEquals(50, ad.height)
        assertEquals("ca-app-pub-5385646520024289/4870299205", ad.adUnitId)
        assertEquals("IMPRESSION", ad.impressionLog?.eventType)
        assertEquals("CLICK", ad.clickLog?.eventType)
        assertEquals("admob", ad.clickLog?.objectId)
    }

    // TH-1559 TC4
    @Test
    fun `TH1559_TC4_hasMore와_nextCursor가_있으면_다음페이지커서가_있다`() {
        // Given
        val json = """{"items": [], "cursor": {"nextCursor": "MjAyNjExMjYxMDMwMDA", "hasMore": true}}"""
        val last = """{"items": [], "cursor": {"nextCursor": null, "hasMore": false}}"""

        // When
        val model = section(json)
        val lastModel = section(last)

        // Then
        assertEquals("MjAyNjExMjYxMDMwMDA", model.nextPageCursor)
        assertNull(lastModel.nextPageCursor)
    }

    // TH-1559 TC1
    @Test
    fun `TH1559_TC1_모르는타입이나_cardId가없는항목은_크래시없이_뺀다`() {
        // Given
        val json = """{"items": [
            {"type": "NEW_TYPE", "cardId": "X:1"},
            {"type": "CONTENT_CARD"},
            {"type": "CONTENT_CARD", "cardId": "NEWS:1", "bodyBlocks": null},
            {"cardId": "NEWS:2"}
        ]}"""

        // When
        val model = section(json)
        val empty = section("{}")

        // Then
        assertEquals(listOf("NEWS:1"), model.items.map { it.cardId })
        assertTrue((model.items.single() as SDHomeNewsItemModel.ContentCard).bodyBlocks.isEmpty())
        assertTrue(empty.items.isEmpty())
        assertNull(empty.header)
    }
}
