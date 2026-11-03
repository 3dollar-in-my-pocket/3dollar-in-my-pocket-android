package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SDAdMobUnitIdParsingTest {

    private val gson = SDUIGson.provideGson()

    private fun card(name: String): SDCardModel =
        gson.fromJson(requireNotNull(javaClass.classLoader?.getResource("sdui/home/$name")).readText(), SDCardModel::class.java)

    private fun storeAdMobCards(name: String): List<SDStoreAdmobSectionModel.Card> =
        gson.fromJson(requireNotNull(javaClass.classLoader?.getResource("sdui/store-v2/$name")).readText(), SDStoreScreenModel::class.java)
            .sections.orEmpty()
            .filterIsInstance<SDStoreAdmobSectionModel>()
            .flatMap { it.cards.orEmpty() }

    // TH-1565 TC1
    @Test
    fun `TH1565_TC1_요즘뜨는_애드몹카드에_adUnitId가_있으면_모델에_담긴다`() {
        // Given
        val fixture = "HomeCurationAdMobCardWithUnitId.json"

        // When
        val model = card(fixture)

        // Then
        assertTrue(model is SDAdMobCardModel)
        assertEquals("ca-app-pub-5385646520024289/6488454438", (model as SDAdMobCardModel).adUnitId)
    }

    // TH-1565 TC1
    @Test
    fun `TH1565_TC1_가게상세_AD_MOB섹션_카드마다_adUnitId가_담긴다`() {
        // Given
        val fixture = "StoreScreenV2WithAdMobUnitId.json"

        // When
        val cards = storeAdMobCards(fixture)

        // Then
        assertEquals(listOf("ADMOB:AD_MOB", "ADMOB:AD_MOB_2"), cards.map { it.cardId })
        assertTrue(cards.all { it.adUnitId == "ca-app-pub-5385646520024289/7477048617" })
    }

    // TH-1565 TC2
    @Test
    fun `TH1565_TC2_adUnitId가_없는_예전_애드몹카드는_실패없이_null이다`() {
        // Given
        val fixture = "HomeCurationAdMobCard.json"

        // When
        val model = card(fixture) as SDAdMobCardModel

        // Then
        assertNull(model.adUnitId)
        assertEquals(50, model.height)
    }

    // TH-1565 TC2
    @Test
    fun `TH1565_TC2_adUnitId가_없는_예전_가게상세_응답은_실패없이_null이다`() {
        // Given
        val fixture = "StoreScreenV2UserStore.json"

        // When
        val cards = storeAdMobCards(fixture)

        // Then
        assertTrue(cards.isNotEmpty())
        assertTrue(cards.all { it.adUnitId == null })
    }
}
