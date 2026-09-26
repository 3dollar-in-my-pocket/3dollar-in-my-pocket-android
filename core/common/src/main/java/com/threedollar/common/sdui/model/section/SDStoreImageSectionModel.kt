package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDStoreImageSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val cards: List<Card>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.IMAGE

    data class Card(
        val cardId: String?,
        val image: SDImageModel?,
        val title: SDTextModel?,
        val subTitle: SDTextModel?,
        val link: SDLink?,
        val customAction: SDCustomActionModel?,
        val style: SDSurfaceStyleModel?,
        val clickLog: SDLogModel?
    )
}
