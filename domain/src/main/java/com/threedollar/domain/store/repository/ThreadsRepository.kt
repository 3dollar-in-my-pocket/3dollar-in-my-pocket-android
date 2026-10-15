package com.threedollar.domain.store.repository

interface ThreadsRepository {
    suspend fun getOEmbedHtml(url: String): Result<String>
}
