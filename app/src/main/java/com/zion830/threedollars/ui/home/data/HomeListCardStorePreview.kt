package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

internal fun HomeListCardModel.BasicCard.storePreviewStoreIdOrNull(): Long? {
    return link?.link.storeIdQueryOrNull()
        ?: marker.link?.link.storeIdQueryOrNull()
        ?: storeIdFromCardId()
}

internal fun HomeListCardModel.BasicCard.storePreviewStoreTypeOrNull(): String? {
    return link?.link.queryValue(STORE_TYPE_QUERY)
        ?: marker.link?.link.queryValue(STORE_TYPE_QUERY)
        ?: storeTypeFromCardId()
}

private fun String?.storeIdQueryOrNull(): Long? {
    return queryValue(STORE_ID_QUERY)?.toLongOrNull()
}

private fun String?.queryValue(key: String): String? {
    if (isNullOrBlank()) return null

    return runCatching {
        URI(this).rawQuery
            ?.split("&")
            .orEmpty()
            .firstNotNullOfOrNull { part ->
                val name = part.substringBefore("=", missingDelimiterValue = "")
                if (decodeUrl(name) != key) return@firstNotNullOfOrNull null

                val value = part.substringAfter("=", missingDelimiterValue = "")
                decodeUrl(value)
            }
    }.getOrNull()
}

private fun HomeListCardModel.BasicCard.storeIdFromCardId(): Long? {
    return cardId.substringAfter(":", missingDelimiterValue = cardId).toLongOrNull()
}

private fun HomeListCardModel.BasicCard.storeTypeFromCardId(): String? {
    return when (cardId.substringBefore(":", missingDelimiterValue = "")) {
        "B" -> BOSS_STORE
        "S" -> USER_STORE
        else -> null
    }
}

private fun decodeUrl(value: String): String {
    return URLDecoder.decode(value, StandardCharsets.UTF_8.name())
}

private const val STORE_ID_QUERY = "storeId"
private const val STORE_TYPE_QUERY = "storeType"
