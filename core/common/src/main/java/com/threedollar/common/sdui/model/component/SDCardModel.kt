package com.threedollar.common.sdui.model.component

import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDTextModel

enum class SDCardType {
    IMAGE_PREVIEW_CARD,
    UNKNOWN
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
    val refs: List<Ref>?,
    val clickLog: SDLogModel? = null
) : SDCardModel {
    override val type: SDCardType get() = SDCardType.IMAGE_PREVIEW_CARD

    data class Style(
        val backgroundColor: String
    )

    data class Ref(
        val type: String?,
        val storeId: String?,
        val storeType: String?
    )
}

data class SDUnknownCardModel(
    override val cardId: String
) : SDCardModel {
    override val type: SDCardType get() = SDCardType.UNKNOWN
}
