package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreThreadPostSectionModel
import com.threedollar.domain.store.repository.ThreadsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class StoreThreadPostEmbedLoader(private val repository: ThreadsRepository) {
    private val htmls = mutableMapOf<String, String>()
    private val failedPermalinks = mutableSetOf<String>()

    fun applyEmbeds(sections: List<SDSectionModel>): List<SDSectionModel> = sections.map { section ->
        if (section is SDStoreThreadPostSectionModel && section.embeds == null) section.resolved() else section
    }

    suspend fun fetchPendingEmbeds(sections: List<SDSectionModel>): Boolean {
        val pendingPermalinks = sections
            .filterIsInstance<SDStoreThreadPostSectionModel>()
            .filter { it.embeds == null }
            .flatMap { it.permalinks }
            .filter { it !in htmls && it !in failedPermalinks }
            .distinct()
        if (pendingPermalinks.isEmpty()) return false

        val results = coroutineScope {
            pendingPermalinks.map { permalink -> async { permalink to repository.getOEmbedHtml(permalink).getOrNull() } }.awaitAll()
        }
        results.forEach { (permalink, html) ->
            if (html != null) htmls[permalink] = html else failedPermalinks += permalink
        }
        return true
    }

    private fun SDStoreThreadPostSectionModel.resolved(): SDStoreThreadPostSectionModel {
        val isResolved = permalinks.all { it in htmls || it in failedPermalinks }
        if (!isResolved) return this
        val clickLogs = cards.orEmpty().associate { it.permalink to it.clickLog }
        val embeds = permalinks.mapNotNull { permalink ->
            htmls[permalink]?.let { SDStoreThreadPostSectionModel.Embed(permalink, it, clickLogs[permalink]) }
        }
        return copy(embeds = embeds)
    }
}
