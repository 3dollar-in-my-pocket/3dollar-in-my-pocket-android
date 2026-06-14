package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionAdditionalInfosModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.common.utils.Constants.USER_STORE

internal fun HomeListCardModel.BasicCard.toFallbackStorePreviewScreen(): StoreScreenModel? {
    val storeId = storePreviewStoreIdOrNull() ?: return null
    val storeType = storePreviewStoreTypeOrNull() ?: USER_STORE
    val storeName = header.title?.text.orEmpty()
    val actionParams = mapOf(
        "STORE_ID" to storeId.toStoreIdClickLogValue(),
        "STORE_TYPE" to SDClickLogValue.StringValue(storeType),
        "STORE_NAME" to SDClickLogValue.StringValue(storeName),
        "LATITUDE" to SDClickLogValue.DoubleValue(marker.location.latitude),
        "LONGITUDE" to SDClickLogValue.DoubleValue(marker.location.longitude),
    )

    return StoreScreenModel(
        sections = listOf(
            StoreSectionModel.Preview(
                type = STORE_PREVIEW_SECTION_TYPE,
                header = header,
                metadata = metadata,
                additionalInfos = StoreSectionAdditionalInfosModel(type = STORE_ADDITIONAL_INFO_TYPE),
                actionBars = listOf(
                    visitAction(storeId),
                    customAction(label = "리뷰 작성", actionType = "STORE_PREVIEW_SECTION_REVIEW_WRITE", params = actionParams),
                    customAction(label = "공유", actionType = "STORE_PREVIEW_SECTION_SHARE", params = actionParams),
                    customAction(label = "길안내", actionType = "STORE_PREVIEW_SECTION_NAVIGATION", params = actionParams),
                ),
                images = images,
                bodies = bodies,
                style = style ?: SDSurfaceStyleModel(backgroundColor = "#FFFFFF"),
            )
        ),
    )
}

private fun visitAction(storeId: Long): StoreActionBarModel {
    return StoreActionBarModel(
        type = ACTION_BAR_TYPE,
        button = SDButtonModel(
            text = fallbackText("방문 인증", fontColor = "#FFFFFF"),
            imageAlignment = "END",
            link = SDLinkModel(type = APP_SCHEME_LINK_TYPE, link = "/visit?storeId=$storeId"),
        ),
    )
}

private fun customAction(
    label: String,
    actionType: String,
    params: Map<String, SDClickLogValue>,
): StoreActionBarModel {
    return StoreActionBarModel(
        type = ACTION_BAR_TYPE,
        button = SDButtonModel(
            text = fallbackText(label),
            customAction = SDCustomActionModel(
                actionType = actionType,
                extraParams = params,
            ),
        ),
    )
}

private fun fallbackText(text: String, fontColor: String? = null): SDTextModel {
    return SDTextModel(
        text = text,
        isHtml = false,
        fontColor = fontColor,
    )
}

private fun Long.toStoreIdClickLogValue(): SDClickLogValue {
    return if (this in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
        SDClickLogValue.IntValue(toInt())
    } else {
        SDClickLogValue.StringValue(toString())
    }
}

private const val STORE_PREVIEW_SECTION_TYPE = "PREVIEW"
private const val STORE_ADDITIONAL_INFO_TYPE = "STORE"
private const val ACTION_BAR_TYPE = "ACTION_BAR"
private const val APP_SCHEME_LINK_TYPE = "APP_SCHEME"
