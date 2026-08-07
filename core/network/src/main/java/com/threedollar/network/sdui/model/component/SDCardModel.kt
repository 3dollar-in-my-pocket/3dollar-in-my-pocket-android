package com.threedollar.network.sdui.model.component

import com.threedollar.network.sdui.model.element.SDChipModel
import com.threedollar.network.sdui.model.element.SDImageModel
import com.threedollar.network.sdui.model.element.SDLink
import com.threedollar.network.sdui.model.element.SDTextModel

enum class SDCardType {
    IMAGE_PREVIEW_CARD
}

interface SDCardModel {
    val type: SDCardType
    val cardId: String
}

data class ImagePreviewCardModel(
    override val cardId: String,
    val image: SDImageModel?,
    val title: SDTextModel?,
    val metricLabel: List<SDChipModel>?,
    val contextLabel: List<SDChipModel>?,
    val link: SDLink?,
    val style: Style?,
    val refs: List<Ref>?
) : SDCardModel {
    override val type: SDCardType = SDCardType.IMAGE_PREVIEW_CARD

    data class Style(
        val backgroundColor: String
    )

    data class Ref(
        val type: String?,
        val storeId: String?,
        val storeType: String?
    )
}
