package com.threedollar.data.screen

import com.google.gson.JsonElement
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.network.data.screen.HomeListCardHeaderResponse
import com.threedollar.network.data.screen.HomeListCardMetadataResponse
import com.threedollar.network.data.screen.HomeListCardResponse
import com.threedollar.network.data.screen.HomeListMarkerResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.SDBorderResponse
import com.threedollar.network.data.screen.SDButtonResponse
import com.threedollar.network.data.screen.SDChipResponse
import com.threedollar.network.data.screen.SDClickLogResponse
import com.threedollar.network.data.screen.SDCursorResponse
import com.threedollar.network.data.screen.SDCustomActionResponse
import com.threedollar.network.data.screen.SDImageResponse
import com.threedollar.network.data.screen.SDImageStyleResponse
import com.threedollar.network.data.screen.SDImpressionLogResponse
import com.threedollar.network.data.screen.SDLinkResponse
import com.threedollar.network.data.screen.SDLocationResponse
import com.threedollar.network.data.screen.SDPageViewLogResponse
import com.threedollar.network.data.screen.SDSurfaceStyleResponse
import com.threedollar.network.data.screen.SDTextResponse
import com.threedollar.network.data.screen.StoreActionBarResponse
import com.threedollar.network.data.screen.StoreScreenResponse
import com.threedollar.network.data.screen.StoreSectionResponse

fun HomeListSectionResponse.asModel(): HomeListSectionModel = HomeListSectionModel(
    cards = cards.orEmpty().mapNotNull { it.asHomeListCardModelOrNull() },
    cursor = cursor?.asModel(),
)

fun StoreScreenResponse.asModel(): StoreScreenModel = StoreScreenModel(
    sections = sections.orEmpty().mapNotNull { it.asStoreSectionModelOrNull() },
    viewLog = viewLog?.asModelOrNull(),
)

private fun HomeListCardResponse.asHomeListCardModelOrNull(): HomeListCardModel? {
    val normalizedType = type.orEmpty().uppercase()
    return when {
        normalizedType == "BASIC_CARD" || normalizedType == "BASIC" || hasBasicCardShape() -> {
            val markerModel = marker?.asModelOrNull() ?: return null
            HomeListCardModel.BasicCard(
                type = type.orEmpty(),
                cardId = cardId.orEmpty(),
                header = header.asModel(),
                metadata = metadata.asModel(),
                images = images.orEmpty().mapNotNull { it.asModelOrNull() },
                bodies = bodies.orEmpty().map { it.asModel() },
                marker = markerModel,
                link = link?.takeIf { !it.link.isNullOrBlank() }?.asModel(),
                style = style?.asModel(),
                clickLog = clickLog?.asModel(),
                impressionLog = impressionLog?.asModel(),
            )
        }

        normalizedType == "EMPTY_CARD" -> HomeListCardModel.EmptyCard(
            type = type.orEmpty(),
            cardId = cardId.orEmpty(),
            header = header?.asModel(),
            bodies = bodies.orEmpty().map { it.asModel() },
            style = style?.asModel(),
        )

        normalizedType == "ADMOB_CARD" -> HomeListCardModel.AdMobCard(
            type = type.orEmpty(),
            cardId = cardId.orEmpty(),
            clickLog = clickLog?.asModel(),
            impressionLog = impressionLog?.asModel(),
        )

        else -> null
    }
}

private fun HomeListCardResponse.hasBasicCardShape(): Boolean {
    return header != null && metadata != null && marker != null
}

private fun StoreSectionResponse.asStoreSectionModelOrNull(): StoreSectionModel? {
    val normalizedType = type.orEmpty().uppercase()
    return when (normalizedType) {
        "PREVIEW" -> StoreSectionModel.Preview(
            type = type.orEmpty(),
            header = header.asModel(),
            metadata = metadata.asModel(),
            topActionBars = topActionBars.orEmpty().map { it.asModel() },
            actionBars = actionBars.orEmpty().map { it.asModel() },
            images = images.orEmpty().mapNotNull { it.asModelOrNull() },
            bodies = bodies.orEmpty().map { it.asModel() },
            style = style?.asModel(),
        )

        else -> null
    }
}

private fun HomeListCardHeaderResponse?.asModel(): HomeListCardHeaderModel = HomeListCardHeaderModel(
    title = this?.title?.asModel(),
    badge = this?.badge?.asModelOrNull(),
)

private fun HomeListCardMetadataResponse?.asModel(): HomeListCardMetadataModel = HomeListCardMetadataModel(
    primary = this?.primary.orEmpty().map { it.asModel() },
    secondary = this?.secondary.orEmpty().map { it.asModel() },
    separator = this?.separator?.asModelOrNull(),
)

private fun HomeListMarkerResponse.asModelOrNull(): HomeListMarkerModel? {
    val locationModel = location?.asModelOrNull() ?: return null
    return HomeListMarkerModel(
        focused = focused.asModel(),
        unfocused = unfocused.asModel(),
        location = locationModel,
        link = link?.takeIf { !it.link.isNullOrBlank() }?.asModel(),
        clickLog = clickLog?.asModel(),
    )
}

private fun StoreActionBarResponse.asModel(): StoreActionBarModel = StoreActionBarModel(
    type = type.orEmpty(),
    button = button.asModel(),
    clickLog = clickLog?.asModel(),
)

private fun SDButtonResponse?.asModel(): SDButtonModel {
    val linkModel = this?.link?.takeIf { !it.link.isNullOrBlank() }?.asModel()
    return SDButtonModel(
        text = this?.text.asModel(),
        image = this?.image?.asModelOrNull(),
        imageAlignment = this?.imageAlignment,
        link = linkModel,
        customAction = if (linkModel == null) this?.customAction?.asModelOrNull() else null,
        style = this?.style?.asModel(),
    )
}

private fun SDChipResponse?.asModel(): SDChipModel = SDChipModel(
    image = this?.image?.asModelOrNull(),
    text = this?.text.asModel(),
    additionalText = this?.additionalText?.asModel(),
    style = this?.style?.asModel(),
)

private fun SDTextResponse?.asModel(): SDTextModel = SDTextModel(
    text = this?.text.orEmpty(),
    isHtml = this?.isHtml ?: false,
    fontColor = this?.fontColor,
)

private fun SDImageResponse.asModelOrNull(): SDImageModel? {
    val imageUrl = url?.takeIf { it.isNotBlank() } ?: return null
    return SDImageModel(
        url = imageUrl,
        style = style?.asModel(),
    )
}

private fun SDImageStyleResponse.asModel(): SDImageStyleModel = SDImageStyleModel(
    width = width,
    height = height,
)

private fun SDLinkResponse.asModel(): SDLinkModel = SDLinkModel(
    type = type.orEmpty(),
    link = link.orEmpty(),
)

private fun SDCustomActionResponse.asModelOrNull(): SDCustomActionModel? {
    val normalizedActionType = actionType?.takeIf { it.isNotBlank() } ?: return null
    return SDCustomActionModel(
        actionType = normalizedActionType,
        extraParams = extraParams.orEmpty().mapValues { it.value.asClickLogValue() },
    )
}

private fun SDLocationResponse.asModelOrNull(): SDLocationModel? {
    val latitudeValue = latitude ?: return null
    val longitudeValue = longitude ?: return null
    return SDLocationModel(
        latitude = latitudeValue,
        longitude = longitudeValue,
    )
}

private fun SDSurfaceStyleResponse.asModel(): SDSurfaceStyleModel = SDSurfaceStyleModel(
    backgroundColor = backgroundColor,
    border = border?.asModel(),
)

private fun SDBorderResponse.asModel(): SDBorderModel = SDBorderModel(
    color = color,
    width = width,
)

private fun SDCursorResponse.asModel(): SDCursorModel = SDCursorModel(
    nextCursor = nextCursor,
    hasMore = hasMore ?: false,
)

private fun SDClickLogResponse.asModel(): SDClickLogModel = SDClickLogModel(
    eventType = eventType.orEmpty(),
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asClickLogValue() },
)

private fun SDImpressionLogResponse.asModel(): SDImpressionLogModel = SDImpressionLogModel(
    eventType = eventType.orEmpty(),
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asClickLogValue() },
)

private fun SDPageViewLogResponse.asModelOrNull(): SDViewLogModel? {
    val screen = screenName?.takeIf { it.isNotBlank() } ?: return null
    return SDViewLogModel(
        eventType = eventType.orEmpty(),
        screenName = screen,
        objectType = objectType.orEmpty(),
        objectId = objectId.orEmpty(),
        extraParameters = extraParameters.orEmpty().mapValues { it.value.asClickLogValue() },
    )
}

private fun JsonElement.asClickLogValue(): SDClickLogValue {
    if (isJsonNull) return SDClickLogValue.Null
    if (!isJsonPrimitive) return SDClickLogValue.StringValue(toString())
    val primitive = asJsonPrimitive
    return when {
        primitive.isBoolean -> SDClickLogValue.BoolValue(primitive.asBoolean)
        primitive.isNumber -> {
            val number = primitive.asNumber
            val asDouble = number.toDouble()
            if (asDouble == asDouble.toLong().toDouble() && !primitive.asString.contains('.')) {
                SDClickLogValue.IntValue(number.toInt())
            } else {
                SDClickLogValue.DoubleValue(asDouble)
            }
        }
        else -> SDClickLogValue.StringValue(primitive.asString)
    }
}
