package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListStoreReferenceModel
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

internal fun HomeListCardModel.BasicCard.storePreviewStoreIdOrNull(): Long? {
    return resolvedStoreReferenceOrNull()?.storeId
}

internal fun HomeListCardModel.BasicCard.storePreviewStoreTypeOrNull(): String? {
    return resolvedStoreReferenceOrNull()?.storeType
}

internal data class HomeStoreReference(
    val storeId: Long,
    val storeType: String?,
)

internal fun HomeListCardModel.BasicCard.resolvedStoreReferenceOrNull(): HomeStoreReference? {
    refs.firstNotNullOfOrNull(HomeListStoreReferenceModel::validStoreReferenceOrNull)?.let { return it }
    link?.link?.linkStoreReferenceOrNull()?.let { return it }
    marker?.link?.link?.linkStoreReferenceOrNull()?.let { return it }
    val cardId = storeIdFromCardId() ?: return null
    return HomeStoreReference(storeId = cardId, storeType = storeTypeFromCardId())
}

private fun HomeListStoreReferenceModel.validStoreReferenceOrNull(): HomeStoreReference? {
    if (!type.equals(STORE_REFERENCE_TYPE, ignoreCase = true)) return null
    val id = storeId.toLongOrNull() ?: return null
    val resolvedType = storeType.takeIf { it.isNotBlank() } ?: return null
    return HomeStoreReference(storeId = id, storeType = resolvedType)
}

private fun String.linkStoreReferenceOrNull(): HomeStoreReference? {
    val id = storeIdQueryOrNull() ?: return null
    return HomeStoreReference(storeId = id, storeType = queryValue(STORE_TYPE_QUERY))
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
private const val STORE_REFERENCE_TYPE = "STORE"
