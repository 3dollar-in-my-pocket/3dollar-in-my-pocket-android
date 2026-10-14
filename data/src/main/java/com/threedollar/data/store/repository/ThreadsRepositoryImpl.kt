package com.threedollar.data.store.repository

import com.threedollar.domain.store.repository.ThreadsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

class ThreadsRepositoryImpl @Inject constructor() : ThreadsRepository {

    override suspend fun getOEmbedHtml(url: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val query = URLEncoder.encode(url, Charsets.UTF_8.name())
            val connection = URL("$OEMBED_ENDPOINT?url=$query").openConnection() as HttpURLConnection
            try {
                connection.connectTimeout = TIMEOUT_MS
                connection.readTimeout = TIMEOUT_MS
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("oEmbed response ${connection.responseCode}")
                }
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                JSONObject(body).optString("html").takeIf { it.isNotBlank() }
                    ?: throw IOException("oEmbed html is empty")
            } finally {
                connection.disconnect()
            }
        }
    }

    private companion object {
        const val OEMBED_ENDPOINT = "https://graph.threads.net/v1.0/oembed"
        const val TIMEOUT_MS = 10_000
    }
}
