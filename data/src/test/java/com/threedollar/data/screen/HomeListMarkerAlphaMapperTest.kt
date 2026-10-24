package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.network.data.screen.HomeListSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListMarkerAlphaMapperTest {
    private val gson = Gson()

    private fun load(name: String): HomeListSectionResponse = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        HomeListSectionResponse::class.java,
    )

    private fun HomeListSectionResponse.basicCard(cardId: String): HomeListCardModel.BasicCard =
        asModel().cards.filterIsInstance<HomeListCardModel.BasicCard>().first { it.cardId == cardId }

    // TH-1533 TC1
    @Test
    fun `TH1533_TC1_alpha가없는_이미지스타일이면_alpha는_1이다`() {
        // Given
        val response = load("HomeListSectionWithMarkerAlphaVariants.json")

        // When
        val card = response.basicCard("S:12804902")

        // Then
        assertEquals(1.0, requireNotNull(card.marker.focused.image?.style).alpha, DELTA)
        assertEquals(1.0, requireNotNull(card.marker.unfocused.image?.style).alpha, DELTA)
    }

    // TH-1533 TC2
    @Test
    fun `TH1533_TC2_alpha가_0점5면_alpha는_0점5다`() {
        // Given
        val response = load("HomeListSectionWithMarkerAlphaVariants.json")

        // When
        val card = response.basicCard("S:12806244")

        // Then
        assertEquals(0.5, requireNotNull(card.marker.unfocused.image?.style).alpha, DELTA)
    }

    // TH-1533 TC3
    @Test
    fun `TH1533_TC3_홈리스트_마커의_focused와_unfocused_alpha가_각각_보존된다`() {
        // Given
        val real = load("HomeListSectionWithMarkerAlpha.json")
        val variant = load("HomeListSectionWithMarkerAlphaVariants.json")

        // When
        val opaque = real.basicCard("S:12806244")
        val stale = real.basicCard("S:12804902")
        val mixed = variant.basicCard("S:12806244")

        // Then
        assertEquals(1.0, requireNotNull(opaque.marker.focused.image?.style).alpha, DELTA)
        assertEquals(1.0, requireNotNull(opaque.marker.unfocused.image?.style).alpha, DELTA)
        assertEquals(0.0, requireNotNull(stale.marker.focused.image?.style).alpha, DELTA)
        assertEquals(0.0, requireNotNull(stale.marker.unfocused.image?.style).alpha, DELTA)
        assertEquals(1.0, requireNotNull(mixed.marker.focused.image?.style).alpha, DELTA)
        assertEquals(0.5, requireNotNull(mixed.marker.unfocused.image?.style).alpha, DELTA)
    }

    private companion object {
        const val DELTA = 0.0001
    }
}
