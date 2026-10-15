package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStoreAppearanceDaySectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val items: List<Item>?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.APPEARANCE_DAY

    data class Item(
        val leadingText: SDTextModel?,
        val primaryText: SDTextModel?,
        val secondaryText: SDTextModel?,
        val style: SDSurfaceStyleModel?
    )
}
