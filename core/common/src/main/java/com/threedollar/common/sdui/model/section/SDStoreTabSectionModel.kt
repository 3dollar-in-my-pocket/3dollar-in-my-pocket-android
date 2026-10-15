package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStoreTabSectionModel(
    override val sectionId: String?,
    val tabs: List<SDActionBarModel>?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.TAB
}
