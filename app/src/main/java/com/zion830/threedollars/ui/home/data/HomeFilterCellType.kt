package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDLinkModel

sealed interface HomeFilterCellType {
    data class Chip(
        val chip: SDChipModel,
        val action: ChipAction,
    ) : HomeFilterCellType

    data class SelectedCategoryChip(
        val chip: SDChipModel,
        val current: HomeFilterCurrentCategory?,
    ) : HomeFilterCellType

    data class Button(
        val button: SDButtonModel,
        val clickLog: SDClickLogModel?,
    ) : HomeFilterCellType
}

sealed interface ChipAction {
    data object OpenCategoryFilter : ChipAction
    data class SelectRadio(val paramKey: String, val optionIndex: Int) : ChipAction
    data class DeepLink(val link: SDLinkModel) : ChipAction
}
