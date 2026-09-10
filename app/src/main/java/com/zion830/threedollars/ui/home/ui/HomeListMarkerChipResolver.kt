package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDChipModel

internal fun HomeListCardModel.BasicCard.markerChipForSelection(isSelected: Boolean): SDChipModel? {
    val marker = marker ?: return null
    val serverChip = if (isSelected) marker.focused else marker.unfocused
    return if (serverChip.isOpenStatusMarker()) serverChip.withoutMarkerText() else serverChip
}

internal fun List<HomeListCardModel.BasicCard>.homeListMarkerCards(): List<HomeListCardModel.BasicCard> =
    filter { it.marker != null }

internal data class HomeListMarkerRenderItem(
    val card: HomeListCardModel.BasicCard,
    val chip: SDChipModel?,
    val isSelected: Boolean,
)

internal fun List<HomeListCardModel.BasicCard>.homeListMarkerRenderItems(
    selectedCardId: String?,
): List<HomeListMarkerRenderItem> = homeListMarkerCards().map { card ->
    val selected = card.cardId == selectedCardId
    HomeListMarkerRenderItem(
        card = card,
        chip = card.markerChipForSelection(isSelected = selected),
        isSelected = selected,
    )
}

internal class HomePendingMarkerSelection(initialCardId: String? = null) {
    var selectedCardId: String? = initialCardId
        private set

    fun update(cards: List<HomeListCardModel.BasicCard>, requestedCardId: String?) {
        selectedCardId = requestedCardId?.takeIf { requested ->
            cards.homeListMarkerCards().any { it.cardId == requested }
        }
    }
}

private fun SDChipModel.isOpenStatusMarker(): Boolean {
    return image == null && displayText().replace(" ", "") == OPEN_STATUS_MARKER_TEXT
}

private fun SDChipModel.withoutMarkerText(): SDChipModel {
    return copy(
        text = text.copy(text = "", isHtml = false),
        additionalText = null,
    )
}

private const val OPEN_STATUS_MARKER_TEXT = "영업중"
