package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
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

    // TH-1533 TC5
    @Test
    fun `TH1533_TC5_서버alpha가_0이면_하한없이_마커alpha도_0이다`() {
        // Given
        val card = card(focused = imageChip(alpha = 0.0), unfocused = imageChip(alpha = 0.0))

        // When
        val alpha = card.markerChipForSelection(isSelected = false).markerAlpha()

        // Then
        assertEquals(0f, alpha, ALPHA_DELTA)
    }

    // TH-1533 TC6
    @Test
    fun `TH1533_TC6_선택하면_focused_alpha_해제하면_unfocused_alpha를_쓴다`() {
        // Given
        val card = card(focused = imageChip(alpha = 1.0), unfocused = imageChip(alpha = 0.4))

        // When
        val selectedAlpha = card.markerChipForSelection(isSelected = true).markerAlpha()
        val unselectedAlpha = card.markerChipForSelection(isSelected = false).markerAlpha()

        // Then
        assertEquals(1f, selectedAlpha, ALPHA_DELTA)
        assertEquals(0.4f, unselectedAlpha, ALPHA_DELTA)
    }

    // TH-1533 TC7
    @Test
    fun `TH1533_TC7_alpha가없거나_이미지가없는_마커는_불투명하다`() {
        // Given
        val withoutAlpha = imageChip(alpha = null)
        val withoutImage = chip("영업중")

        // When
        val alphas = listOf(withoutAlpha.markerAlpha(), withoutImage.markerAlpha(), null.markerAlpha())

        // Then
        alphas.forEach { assertEquals(1f, it, ALPHA_DELTA) }
    }

    private fun imageChip(alpha: Double?): SDChipModel {
        return SDChipModel(
            image = SDImageModel(
                url = "https://storage.threedollars.co.kr/app/marker/boss_mappin_unfocused_31.png",
                style = alpha?.let { SDImageStyleModel(width = 28.0, height = 28.0, alpha = it) },
            ),
            text = SDTextModel(text = "", isHtml = false),
        )
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

    private companion object {
        const val ALPHA_DELTA = 0.0001f
    }
}
