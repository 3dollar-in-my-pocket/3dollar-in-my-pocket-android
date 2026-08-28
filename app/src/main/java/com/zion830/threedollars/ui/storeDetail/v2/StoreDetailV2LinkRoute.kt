package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDLinkModel
import java.net.URI

internal enum class StoreDetailV2LinkRoute {
    Visit,
    Contributors,
    Reviews,
    Dynamic,
    External,
    Unsupported,
}

internal fun SDLinkModel.storeDetailV2Route(): StoreDetailV2LinkRoute {
    if (link.isBlank()) return StoreDetailV2LinkRoute.Unsupported
    if (!type.equals(APP_SCHEME, ignoreCase = true)) {
        return if (type.equals(WEB, ignoreCase = true)) {
            StoreDetailV2LinkRoute.External
        } else {
            StoreDetailV2LinkRoute.Unsupported
        }
    }

    val route = runCatching {
        val uri = URI(link)
        uri.path
            ?.trim('/')
            ?.substringAfterLast('/')
            ?.takeIf(String::isNotBlank)
            ?: uri.host.orEmpty()
    }.getOrNull() ?: return StoreDetailV2LinkRoute.Unsupported

    return when (route.lowercase()) {
        "visit" -> StoreDetailV2LinkRoute.Visit
        "contributors", "store-contributors" -> StoreDetailV2LinkRoute.Contributors
        "review", "reviews", "reviewlist" -> StoreDetailV2LinkRoute.Reviews
        "bookmark", "home", "medal", "store", "storepreview", "polldetail", "community", "browser" -> {
            StoreDetailV2LinkRoute.Dynamic
        }
        else -> StoreDetailV2LinkRoute.Unsupported
    }
}

private const val APP_SCHEME = "APP_SCHEME"
private const val WEB = "WEB"
