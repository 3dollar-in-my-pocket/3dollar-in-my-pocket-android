package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStoreMarginSectionModel(
    override val sectionId: String?,
    val height: Int?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.MARGIN
}
