package com.threedollar.common.sdui.model.section.home

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDHomeBottomSheetTabsModel(
    val tabs: List<SDHomeBottomSheetTabModel> = emptyList()
)

data class SDHomeBottomSheetTabModel(
    val tabId: String,
    val viewType: String,
    val selected: SDHomeTabAppearanceModel,
    val unselected: SDHomeTabAppearanceModel,
    val defaultSelected: Boolean,
    val clickLog: SDLogModel? = null
)

data class SDHomeTabAppearanceModel(
    val title: SDTextModel?,
    val style: SDSurfaceStyleModel?
)

data class SDHomeCurationSectionModel(
    val items: List<SDHomeCurationItemModel> = emptyList()
)

sealed interface SDHomeCurationItemModel {
    data class Carousel(
        val carouselId: String,
        val header: SDHeaderModel?,
        val defaultCategoryId: String,
        val categoryFilters: List<SDHomeCurationCategoryFilterModel>,
        val cards: List<SDCardModel> = emptyList()
    ) : SDHomeCurationItemModel

    data class AdMob(val card: SDAdMobCardModel) : SDHomeCurationItemModel

    data class Unknown(val rawType: String?) : SDHomeCurationItemModel
}

data class SDHomeCurationCategoryFilterModel(
    val categoryId: String,
    val selected: SDChipModel?,
    val unselected: SDChipModel?,
    val clickLog: SDLogModel? = null
)

data class SDHomeCurationCardsModel(
    val cards: List<SDCardModel> = emptyList()
)
