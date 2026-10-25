package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeAdCardMapperTest {
    private val gson = Gson()

    private fun <T> load(name: String, type: Class<T>): T = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        type,
    )

    // TH-1529 TC1
    @Test
    fun `TH1529_TC1_홈리스트_AD_CARD를_필드와_서버순서대로_파싱한다`() {
        // Given
        val response = load("HomeListSectionWithAdCard.json", HomeListSectionResponse::class.java)

        // When
        val cards = response.asModel().cards

        // Then
        assertEquals(
            listOf("S:12806244", "S:12804906", "AD:109", "ADMOB:7e0f5a99-a746-4dbd-88ff-45a334811ad3"),
            cards.map { it.cardId },
        )
        val ad = (cards[2] as HomeListCardModel.AdCard).card
        assertEquals("AD:109", ad.cardId)
        assertEquals("가3팀이 붕어빵을 판다?", ad.title?.text)
        assertEquals("익선동 팝업에서 슈크림 붕어빵을 먼저 만나보세요", ad.subTitle?.text)
        val label = ad.labels.orEmpty().single()
        assertEquals("광고", label.text?.text)
        assertEquals("#FF8181", label.text?.fontColor)
        assertEquals("#FFEFEF", label.style?.backgroundColor)
        assertEquals("https://storage.dev.threedollars.co.kr/popup/v1-d49d6f2d-43ae-4694-aae6-2c9fc6995c2a.png", ad.image?.url)
        assertEquals(SDLinkType.WEB, ad.link?.type)
        assertEquals("https://web.threedollars.co.kr", ad.link?.link)
        assertEquals("CLICK", ad.clickLog?.eventType)
        assertEquals("advertisement", ad.clickLog?.objectId)
        assertEquals("IMPRESSION", ad.impressionLog?.eventType)
        assertEquals("109", ad.impressionLog?.extraParameters?.get("advertisement_id"))
        assertTrue(cards[3] is HomeListCardModel.AdMobCard)
    }

    // TH-1529 TC2
    @Test
    fun `TH1529_TC2_큐레이션_캐러셀과_더보기의_AD_CARD가_서버순서_위치에_남는다`() {
        // Given
        val section = load("HomeCurationSectionWithAdCard.json", HomeCurationSectionResponse::class.java)
        val more = load("HomeCurationCardsWithAdCard.json", HomeCurationCardsResponse::class.java)

        // When
        val carousel = section.asCurationModel().items.first() as SDHomeCurationItemModel.Carousel
        val moreCards = more.asCurationCardsModel().cards

        // Then
        listOf(carousel.cards, moreCards).forEach { cards ->
            assertEquals(4, cards.size)
            assertTrue(cards[0] is ImagePreviewCardModel)
            assertTrue(cards[1] is ImagePreviewCardModel)
            assertTrue(cards[3] is ImagePreviewCardModel)
            val ad = cards[2] as SDAdCardModel
            assertEquals("AD:110", ad.cardId)
            assertEquals("가3팀 붕어빵", ad.title?.text)
            assertEquals("익선동 팝업 오픈", ad.subTitle?.text)
            assertEquals("광고", ad.labels.orEmpty().single().text?.text)
            assertNotNull(ad.image?.url)
            assertEquals("POPULAR_SNACKS", ad.impressionLog?.extraParameters?.get("carouselId"))
        }
        assertTrue((section.asCurationModel().items[1] as SDHomeCurationItemModel.AdMob).card is SDAdMobCardModel)
    }

    // TH-1529 TC7
    @Test
    fun `TH1529_TC7_선택필드가_없는_광고카드도_파싱되고_없는요소만_null이다`() {
        // Given
        val list = load("HomeListSectionWithAdCardVariants.json", HomeListSectionResponse::class.java)
        val curation = load("HomeCurationCardsWithAdCardVariants.json", HomeCurationCardsResponse::class.java)

        // When
        val listAd = list.asModel().cards.filterIsInstance<HomeListCardModel.AdCard>().single().card
        val curationAd = curation.asCurationCardsModel().cards.filterIsInstance<SDAdCardModel>().single()

        // Then
        listOf(listAd, curationAd).forEach { ad ->
            assertNull(ad.title)
            assertNull(ad.subTitle)
            assertNull(ad.image)
            assertNull(ad.trailingIcon)
            assertNull(ad.link)
            assertEquals("광고", ad.labels.orEmpty().single().text?.text)
            assertNotNull(ad.clickLog)
            assertNotNull(ad.impressionLog)
        }
    }

    // TH-1529 TC13
    @Test
    fun `TH1529_TC13_알수없는_카드타입은_무시하고_광고카드와_나머지는_남는다`() {
        // Given
        val list = load("HomeListSectionWithAdCardVariants.json", HomeListSectionResponse::class.java)
        val curation = load("HomeCurationCardsWithAdCardVariants.json", HomeCurationCardsResponse::class.java)

        // When
        val listCards = list.asModel().cards
        val curationCards = curation.asCurationCardsModel().cards

        // Then
        assertEquals(
            listOf("S:12806244", "S:12804906", "AD:109", "ADMOB:7e0f5a99-a746-4dbd-88ff-45a334811ad3"),
            listCards.map { it.cardId },
        )
        assertEquals(4, curationCards.size)
        assertTrue(curationCards.none { it.cardId == "NEW:1" })
        assertTrue(curationCards[2] is SDAdCardModel)
    }
}
