package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListMarkerChipResolverTest {

    @Test
    fun markerChipForSelection_usesServerFocusedMarkerWhenItHasTextOtherThanOpenStatus() {
        val card = card(
            focused = chip("대표"),
            secondary = listOf(chip("영업중")),
        )

        val markerChip = card.markerChipForSelection(isSelected = true)

        assertEquals("대표", markerChip.text.text)
    }

    @Test
    fun markerChipForSelection_hidesServerOpenStatusMarker() {
        val card = card(
            focused = chip("영업 중"),
            secondary = listOf(chip("영업중")),
        )

        val markerChip = card.markerChipForSelection(isSelected = true)

        assertEquals("", markerChip.text.text)
        assertEquals(false, markerChip.text.isHtml)
    }

    @Test
    fun markerChipForSelection_doesNotFallbackToOpenStatusForSelectedMarkerWhenFocusedMarkerIsBlank() {
        val card = card(
            focused = chip(""),
            secondary = listOf(chip("영업중", fontColor = "#FFFFFF", fontWeight = "BOLD")),
        )

        val markerChip = card.markerChipForSelection(isSelected = true)

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

        val markerChip = card.markerChipForSelection(isSelected = false)

        assertEquals("", markerChip.text.text)
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
