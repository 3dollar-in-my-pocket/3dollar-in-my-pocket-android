package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreMarginSectionModel(
    override val sectionId: String?,
    val height: Int?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.MARGIN
}
