package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDChipModel

internal fun HomeListCardModel.BasicCard.markerChipForSelection(isSelected: Boolean): SDChipModel {
    val serverChip = if (isSelected) marker.focused else marker.unfocused
    return if (serverChip.isOpenStatusMarker()) serverChip.withoutMarkerText() else serverChip
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
