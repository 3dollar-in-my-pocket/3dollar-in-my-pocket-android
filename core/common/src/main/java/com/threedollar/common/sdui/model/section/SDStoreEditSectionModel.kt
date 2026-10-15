package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStoreEditSectionModel(
    override val sectionId: String?,
    val map: Map?,
    val actionBars: List<SDActionBarModel>?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
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
