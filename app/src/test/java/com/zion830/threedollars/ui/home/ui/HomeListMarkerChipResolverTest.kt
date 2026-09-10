package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeListMarkerChipResolverTest {

    @Test
    fun markerChipForSelection_usesServerFocusedMarkerWhenItHasTextOtherThanOpenStatus() {
        val card = card(
            focused = chip("대표"),
            secondary = listOf(chip("영업중")),
        )

        val markerChip = requireNotNull(card.markerChipForSelection(isSelected = true))

        assertEquals("대표", markerChip.text.text)
    }

    @Test
    fun markerChipForSelection_hidesServerOpenStatusMarker() {
        val card = card(
            focused = chip("영업 중"),
            secondary = listOf(chip("영업중")),
        )

        val markerChip = requireNotNull(card.markerChipForSelection(isSelected = true))

        assertEquals("", markerChip.text.text)
        assertEquals(false, markerChip.text.isHtml)
    }

    @Test
    fun markerChipForSelection_doesNotFallbackToOpenStatusForSelectedMarkerWhenFocusedMarkerIsBlank() {
        val card = card(
            focused = chip(""),
            secondary = listOf(chip("영업중", fontColor = "#FFFFFF", fontWeight = "BOLD")),
        )

        val markerChip = requireNotNull(card.markerChipForSelection(isSelected = true))

        assertEquals("", markerChip.text.text)
        assertEquals(false, markerChip.text.isHtml)
    }

    @Test
    fun markerChipForSelection_doesNotFallbackForUnselectedMarker() {
        val card = card(
            focused = chip(""),
            unfocused = chip(""),
            secondary = listOf(chip("영업중")),
        )

        val markerChip = requireNotNull(card.markerChipForSelection(isSelected = false))

        assertEquals("", markerChip.text.text)
    }

    @Test
    fun `marker cards preserve list order while omitting cards without markers`() {
        val first = card(focused = chip("A")).copy(cardId = "A")
        val withoutMarker = card(focused = chip("B")).copy(cardId = "B", marker = null)
        val third = card(focused = chip("C")).copy(cardId = "C")

        assertEquals(listOf("A", "C"), listOf(first, withoutMarker, third).homeListMarkerCards().map { it.cardId })
        assertNull(withoutMarker.markerChipForSelection(isSelected = true))
    }

    @Test
    fun `marker render items preserve selected card across nullable marker gap`() {
        val first = card(focused = chip("A-selected"), unfocused = chip("A")).copy(cardId = "A")
        val withoutMarker = card(focused = chip("B-selected")).copy(cardId = "B", marker = null)
        val third = card(focused = chip("C-selected"), unfocused = chip("C")).copy(cardId = "C")

        val items = listOf(first, withoutMarker, third).homeListMarkerRenderItems(selectedCardId = "C")

        assertEquals(listOf("A", "C"), items.map { it.card.cardId })
        assertEquals(listOf(false, true), items.map { it.isSelected })
        assertEquals(listOf("A", "C-selected"), items.map { it.chip?.text?.text })
    }

    @Test
    fun `pending marker selection clears when selected card has no marker or selection closes`() {
        val first = card(focused = chip("A")).copy(cardId = "A")
        val withoutMarker = card(focused = chip("B")).copy(cardId = "B", marker = null)
        val third = card(focused = chip("C")).copy(cardId = "C")
        val cards = listOf(first, withoutMarker, third)
        val pending = HomePendingMarkerSelection(initialCardId = "A")

        pending.update(cards, requestedCardId = "B")
        assertNull(pending.selectedCardId)

        pending.update(cards, requestedCardId = "C")
        assertEquals("C", pending.selectedCardId)

        pending.update(cards, requestedCardId = null)
        assertNull(pending.selectedCardId)
    }

    private fun card(
        focused: SDChipModel,
        unfocused: SDChipModel = chip(""),
        secondary: List<SDChipModel> = emptyList(),
    ): HomeListCardModel.BasicCard {
        return HomeListCardModel.BasicCard(
            type = "BASIC_CARD",
            cardId = "S:100186",
            header = HomeListCardHeaderModel(title = SDTextModel("가게", isHtml = false)),
            metadata = HomeListCardMetadataModel(secondary = secondary),
            marker = HomeListMarkerModel(
                focused = focused,
                unfocused = unfocused,
                location = SDLocationModel(latitude = 37.1, longitude = 127.2),
            ),
        )
    }

    private fun chip(
        text: String,
        fontColor: String? = null,
        fontWeight: String? = null,
    ): SDChipModel {
        return SDChipModel(
            text = SDTextModel(
                text = text,
                isHtml = false,
                fontColor = fontColor,
                fontWeight = fontWeight,
            ),
        )
    }
}
