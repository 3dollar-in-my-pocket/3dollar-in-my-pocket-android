package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDSnsPostPlatform
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
            .flatMap { it.threadsPermalinks }
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
        val isResolved = threadsPermalinks.all { it in htmls || it in failedPermalinks }
        if (!isResolved) return this
        val embeds = supportedCards.mapNotNull { card ->
            val permalink = card.permalink ?: return@mapNotNull null
            val html = when (card.snsPlatform) {
                SDSnsPostPlatform.THREADS -> htmls[permalink]
                SDSnsPostPlatform.INSTAGRAM -> instagramEmbedHtml(permalink)
                null -> null
            }
            html?.let { SDStoreThreadPostSectionModel.Embed(permalink, it, card.clickLog) }
        }
        return copy(embeds = embeds)
    }

    private fun instagramEmbedHtml(permalink: String): String {
        val escapedPermalink = permalink
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        return """<blockquote class="instagram-media" data-instgrm-permalink="$escapedPermalink" data-instgrm-version="14"></blockquote>""" +
            """<script async src="$INSTAGRAM_EMBED_SCRIPT_URL"></script>"""
    }

    private companion object {
        const val INSTAGRAM_EMBED_SCRIPT_URL = "https://platform.instagram.com/en_US/embeds.js"
    }
}
