package com.zion830.threedollars.ui.storeDetail.contributor.model

import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.serverdriven.model.SDLinkModel
import java.net.URI

internal const val STORE_UPDATE_PATH = "/storeUpdate"

internal val storeContributorScreenName: ScreenName = ScreenName.STORE_CONTRIBUTORS

internal fun createStoreContributorEditClickEvent(storeId: String): ClickEvent = ClickEvent(
    screen = storeContributorScreenName,
    objectType = LogObjectType.BUTTON,
    objectId = LogObjectId.EDIT,
    additionalParams = mapOf(ParameterName.STORE_ID to storeId),
)

internal val storeContributorPageViewClassName: String = "StoreContributorActivity"

internal fun SDLinkModel.isStoreUpdateAction(): Boolean {
    if (!type.equals("APP_SCHEME", ignoreCase = true)) return false

    return runCatching { URI(link).path == STORE_UPDATE_PATH }
        .getOrDefault(false)
}
