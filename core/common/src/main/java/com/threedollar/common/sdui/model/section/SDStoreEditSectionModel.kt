package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreEditSectionModel(
    override val sectionId: String?,
    val map: Map?,
    val actionBars: List<SDActionBarModel>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.EDIT

    data class Map(
        val location: Location?,
        val footerLeft: SDActionBarModel?,
        val footerRight: SDActionBarModel?
    )

    data class Location(
        val latitude: Double?,
        val longitude: Double?
    )
}
