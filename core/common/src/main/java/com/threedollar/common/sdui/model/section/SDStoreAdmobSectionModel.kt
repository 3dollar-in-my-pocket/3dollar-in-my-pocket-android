package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreAdmobSectionModel(
    override val sectionId: String?,
    val cards: List<Card>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.AD_MOB

    data class Card(
        val type: String?,
        val cardId: String?,
        val clickLog: SDLogModel?,
        val impressionLog: SDLogModel?
    )
}
