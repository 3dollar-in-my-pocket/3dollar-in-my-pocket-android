package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeAdMobUnitIdMapperTest {
    private val gson = Gson()

    private fun <T> load(name: String, type: Class<T>): T = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        type,
    )

    // TH-1565 TC1
    @Test
    fun `TH1565_TC1_홈주변가게_애드몹카드의_adUnitId를_모델로_옮긴다`() {
        // Given
        val response = load("HomeListSectionWithAdMobUnitId.json", HomeListSectionResponse::class.java)

        // When
        val adMob = response.asModel().cards.filterIsInstance<HomeListCardModel.AdMobCard>().single()

        // Then
        assertEquals("ca-app-pub-5385646520024289/3147975229", adMob.adUnitId)
        assertEquals(50, adMob.heightDp)
    }

    // TH-1565 TC1
    @Test
    fun `TH1565_TC1_요즘뜨는_섹션_애드몹카드의_adUnitId를_모델로_옮긴다`() {
        // Given
        val response = load("HomeCurationSectionWithAdMobUnitId.json", HomeCurationSectionResponse::class.java)

        // When
        val adMob = response.asCurationModel().items.filterIsInstance<SDHomeCurationItemModel.AdMob>().single()

        // Then
        assertEquals("ca-app-pub-5385646520024289/6488454438", adMob.card.adUnitId)
    }

    // TH-1565 TC1
    @Test
    fun `TH1565_TC1_요즘뜨는_캐러셀카드_애드몹의_adUnitId를_모델로_옮긴다`() {
        // Given
        val response = load("HomeCurationCardsWithAdMobUnitId.json", HomeCurationCardsResponse::class.java)

        // When
        val adMob = response.asCurationCardsModel().cards.filterIsInstance<SDAdMobCardModel>().single()

        // Then
        assertEquals("ca-app-pub-5385646520024289/6488454438", adMob.adUnitId)
    }

    // TH-1565 TC2
    @Test
    fun `TH1565_TC2_adUnitId가_없는_예전_홈응답은_실패없이_null이다`() {
        // Given
        val list = load("HomeListSectionWithAdCard.json", HomeListSectionResponse::class.java)
        val section = load("HomeCurationSectionWithAdCard.json", HomeCurationSectionResponse::class.java)
        val cards = load("HomeCurationCardsWithDevice.json", HomeCurationCardsResponse::class.java)

        // When
        val listAdMob = list.asModel().cards.filterIsInstance<HomeListCardModel.AdMobCard>()
        val sectionAdMob = section.asCurationModel().items.filterIsInstance<SDHomeCurationItemModel.AdMob>()
        val carouselAdMob = cards.asCurationCardsModel().cards.filterIsInstance<SDAdMobCardModel>()

        // Then
        assertTrue(listAdMob.isNotEmpty() && sectionAdMob.isNotEmpty() && carouselAdMob.isNotEmpty())
        listAdMob.forEach { assertNull(it.adUnitId) }
        sectionAdMob.forEach { assertNull(it.card.adUnitId) }
        carouselAdMob.forEach { assertNull(it.adUnitId) }
    }
}
