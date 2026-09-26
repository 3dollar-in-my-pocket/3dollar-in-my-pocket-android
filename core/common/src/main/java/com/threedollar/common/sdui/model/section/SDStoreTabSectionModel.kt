package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreTabSectionModel(
    override val sectionId: String?,
    val tabs: List<SDActionBarModel>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.TAB
}
