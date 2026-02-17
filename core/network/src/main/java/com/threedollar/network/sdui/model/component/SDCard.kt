package com.threedollar.network.sdui.model.component

import com.threedollar.network.sdui.model.element.SDChip
import com.threedollar.network.sdui.model.element.SDImage
import com.threedollar.network.sdui.model.element.SDLink
import com.threedollar.network.sdui.model.element.SDText

enum class SDCardType {
    IMAGE_PREVIEW_CARD
}

interface SDCard {
    val type: SDCardType
    val cardId: String
}

data class ImagePreviewCard(
    override val cardId: String,
    val image: SDImage?,
    val title: SDText?,
    val metricLabel: List<SDChip>?,
    val contextLabel: List<SDChip>?,
    val link: SDLink?,
    val style: Style?,
    val refs: List<Ref>?
) : SDCard {
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
