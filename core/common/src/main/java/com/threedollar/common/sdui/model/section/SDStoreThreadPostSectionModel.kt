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

    val supportedCards: List<Card>
        get() = cards.orEmpty()
            .filter { it.snsPlatform != null && !it.permalink.isNullOrBlank() }
            .distinctBy { it.permalink }

    val permalinks: List<String>
        get() = supportedCards.mapNotNull { it.permalink }

    val threadsPermalinks: List<String>
        get() = supportedCards.filter { it.snsPlatform == SDSnsPostPlatform.THREADS }.mapNotNull { it.permalink }

    data class Card(
        val permalink: String?,
        val clickLog: SDLogModel?,
        val platform: String? = null,
    ) {
        val snsPlatform: SDSnsPostPlatform?
            get() = if (platform == null) SDSnsPostPlatform.THREADS else SDSnsPostPlatform.from(platform)
    }

    data class Embed(
        val permalink: String,
        val html: String,
        val clickLog: SDLogModel?
    )
}

enum class SDSnsPostPlatform {
    THREADS,
    INSTAGRAM;

    companion object {
        fun from(raw: String?): SDSnsPostPlatform? = entries.firstOrNull { it.name == raw }
    }
}
