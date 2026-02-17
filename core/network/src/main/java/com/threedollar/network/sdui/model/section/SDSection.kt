package com.threedollar.network.sdui.model.section

import com.threedollar.network.sdui.model.component.SDCard
import com.threedollar.network.sdui.model.component.SDHeader

enum class SDSectionType {
    RELATED_STORES
}

interface SDSection {
    val type: SDSectionType
}

data class RelatedStoresSection(
    val header: SDHeader?,
    val cards: List<SDCard>,
    val reference: List<Reference>?
) : SDSection {
    override val type: SDSectionType = SDSectionType.RELATED_STORES

    data class Reference(
        val type: String?,
        val experimentKey: String?,
        val variant: String?
    )
}
