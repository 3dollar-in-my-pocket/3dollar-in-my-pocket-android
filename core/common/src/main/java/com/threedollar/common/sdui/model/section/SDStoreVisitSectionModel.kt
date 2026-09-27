package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDStoreVisitSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val summary: Summary?,
    val history: History?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.VISIT

    data class Summary(
        val chips: List<SDChipModel>?
    )

    data class History(
        val items: List<SDChipModel>?,
        val moreText: SDTextModel?,
        val style: SDSurfaceStyleModel?
    )
}
