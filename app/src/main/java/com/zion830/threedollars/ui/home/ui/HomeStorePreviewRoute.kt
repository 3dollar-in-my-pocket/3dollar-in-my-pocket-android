package com.zion830.threedollars.ui.home.ui

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

internal data class HomeStorePreviewRoute(
    val storeId: Long,
    val storeType: String?,
) {
    companion object {
        fun fromLink(
            link: String?,
            fallbackStoreId: Long? = null,
            fallbackStoreType: String? = null,
        ): HomeStorePreviewRoute? {
            val queryParameters = link.queryParameters()
            val storeId = queryParameters["storeId"]?.toLongOrNull() ?: fallbackStoreId ?: return null
            val storeType = queryParameters["storeType"] ?: fallbackStoreType

            return HomeStorePreviewRoute(
                storeId = storeId,
                storeType = storeType,
            )
        }
    }
}

private fun String?.queryParameters(): Map<String, String> {
    if (isNullOrBlank()) return emptyMap()

    return runCatching {
        URI(this).rawQuery
            ?.split("&")
            .orEmpty()
            .mapNotNull { part ->
                val key = part.substringBefore("=", missingDelimiterValue = "")
                if (key.isBlank()) return@mapNotNull null

                val value = part.substringAfter("=", missingDelimiterValue = "")
                decodeUrl(key) to decodeUrl(value)
            }
            .toMap()
    }.getOrDefault(emptyMap())
}

private fun decodeUrl(value: String): String {
    return URLDecoder.decode(value, StandardCharsets.UTF_8.name())
}
