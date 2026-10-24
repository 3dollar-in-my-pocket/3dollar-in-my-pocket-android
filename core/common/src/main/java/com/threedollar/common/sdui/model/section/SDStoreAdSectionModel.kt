package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreAdSectionModel(
    override val sectionId: String?,
    val cards: List<SDCardModel>?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.AD

    val firstAdCard: SDAdCardModel? get() = cards?.filterIsInstance<SDAdCardModel>()?.firstOrNull()
}
