package com.threedollar.network.sdui.model.section

import com.threedollar.network.sdui.model.component.SDCardModel
import com.threedollar.network.sdui.model.component.SDHeaderModel

enum class SDSectionType {
    RELATED_STORES
}

interface SDSectionModel {
    val type: SDSectionType
}

data class SDRelatedStoresSectionModel(
    val header: SDHeaderModel?,
    val cards: List<SDCardModel>,
    val reference: List<Reference>?
) : SDSectionModel {
    override val type: SDSectionType = SDSectionType.RELATED_STORES

    data class Reference(
        val type: String?,
        val experimentKey: String?,
        val variant: String?
    )
}
