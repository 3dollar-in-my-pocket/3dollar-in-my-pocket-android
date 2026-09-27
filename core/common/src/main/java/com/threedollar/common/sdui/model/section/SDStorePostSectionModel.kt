package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDToggleActionModel

data class SDStorePostSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val cards: List<Card>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.POST

    data class Card(
        val cardId: String?,
        val header: SDChipModel?,
        val images: List<SDImageModel>?,
        val body: SDTextModel?,
        val like: SDToggleActionModel?,
        val link: SDLink?,
        val style: SDSurfaceStyleModel?,
        val clickLog: SDLogModel?
    )
}
