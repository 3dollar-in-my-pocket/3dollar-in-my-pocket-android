package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

data class SDStoreThreadPostSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val cards: List<Card>?,
    override val impressionLog: SDLogModel?,
    override val style: SDSurfaceStyleModel?,
    @Transient val embeds: List<Embed>? = null,
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.THREADS_POST

    val permalinks: List<String>
        get() = cards.orEmpty().mapNotNull { it.permalink?.takeIf(String::isNotBlank) }.distinct()

    data class Card(
        val permalink: String?,
        val clickLog: SDLogModel?
    )

    data class Embed(
        val permalink: String,
        val html: String,
        val clickLog: SDLogModel?
    )
}
