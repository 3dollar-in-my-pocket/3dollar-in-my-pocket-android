package com.threedollar.data.screen

import com.google.gson.JsonElement
import com.threedollar.common.serverdriven.model.SDActionBarModel
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDHeaderModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.network.data.screen.StoreContributorActionBarResponse
import com.threedollar.network.data.screen.StoreContributorBorderResponse
import com.threedollar.network.data.screen.StoreContributorButtonResponse
import com.threedollar.network.data.screen.StoreContributorCardResponse
import com.threedollar.network.data.screen.StoreContributorChipResponse
import com.threedollar.network.data.screen.StoreContributorCursorResponse
import com.threedollar.network.data.screen.StoreContributorHeaderResponse
import com.threedollar.network.data.screen.StoreContributorImageResponse
import com.threedollar.network.data.screen.StoreContributorImageStyleResponse
import com.threedollar.network.data.screen.StoreContributorLinkResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreContributorSectionResponse
import com.threedollar.network.data.screen.StoreContributorSurfaceStyleResponse
import com.threedollar.network.data.screen.StoreContributorTextResponse
import com.threedollar.network.data.screen.StoreContributorClickLogResponse
import com.threedollar.network.data.screen.StoreContributorCustomActionResponse
import com.threedollar.network.data.screen.SDPageViewLogResponse

fun StoreContributorScreenResponse.asModel(): SDScreenModel = SDScreenModel(
    sections = sections.orEmpty().map { it.asModel() },
    viewLog = viewLog?.asModelOrNull(),
)

fun StoreContributorHistoriesResponse.asCardsSectionModel(): SDSectionModel.CardsSection = SDSectionModel.CardsSection(
    type = type.orEmpty(),
    cards = cards.orEmpty().map { it.asModel() },
    cursor = cursor?.asModel(),
)

fun StoreContributorSectionResponse.asModel(): SDSectionModel {
    val normalizedType = type.orEmpty().uppercase()
    val actionBarValue = actionBar
    val headerValue = header
    val cardsValue = cards
    return when {
        normalizedType.contains("ACTION") || actionBarValue != null -> SDSectionModel.ActionBarSection(
            type = type.orEmpty(),
            actionBar = actionBarValue.asModel()
        )

        normalizedType.contains("HEADER") || headerValue != null -> SDSectionModel.HeaderSection(
            type = type.orEmpty(),
            header = headerValue.asModel()
        )

        normalizedType.contains("CARD") || normalizedType.contains("HIST") || cardsValue != null -> SDSectionModel.CardsSection(
            type = type.orEmpty(),
            cards = cardsValue.orEmpty().map { it.asModel() },
            cursor = cursor?.asModel(),
        )

        else -> SDSectionModel.Unknown(type = type.orEmpty())
    }
}

private fun StoreContributorActionBarResponse?.asModel(): SDActionBarModel = SDActionBarModel(
    button = this?.button.asModel(),
    clickLog = this?.clickLog?.asModel(),
)

private fun StoreContributorHeaderResponse?.asModel(): SDHeaderModel = SDHeaderModel(
    title = this?.title.asModel(),
    subTitle = this?.subTitle?.asModel(),
    trailingAction = this?.trailingAction?.asModel(),
)

private fun StoreContributorCardResponse.asModel(): SDCardModel {
    val normalizedType = type.orEmpty().uppercase()
    val descriptionValue = description
    val subTitlesValue = subTitles.orEmpty()
    return when {
        normalizedType == "CALLOUT_CARD" || normalizedType.contains("DESCRIPTION") || descriptionValue != null -> SDCardModel.DescriptionCard(
            type = type.orEmpty(),
            cardId = cardId.orEmpty(),
            title = title.asModel(),
            description = descriptionValue.asModel(),
            style = style?.asModel(),
            clickLog = clickLog?.asModel(),
        )

        normalizedType.contains("HISTORY") ||
            subTitlesValue.isNotEmpty() ||
            subTitleChip != null ||
            metadata != null ||
            image != null -> SDCardModel.HistoryCard(
            type = type.orEmpty(),
            cardId = cardId.orEmpty(),
            title = title.asModel(),
            subTitles = subTitlesValue.map { it.asModel() },
            subTitleChip = subTitleChip?.asModel(),
            image = image?.asModel(),
            metadata = metadata?.asModel(),
            style = style?.asModel(),
            clickLog = clickLog?.asModel(),
        )

        else -> SDCardModel.Unknown(
            type = type.orEmpty(),
            cardId = cardId.orEmpty(),
            title = title.asModel(),
            style = style?.asModel(),
            clickLog = clickLog?.asModel(),
        )
    }
}

private fun StoreContributorTextResponse?.asModel(): SDTextModel = SDTextModel(
    text = this?.text.orEmpty(),
    isHtml = this?.isHtml ?: false,
    fontColor = this?.fontColor,
    fontWeight = this?.fontWeight,
)

private fun StoreContributorImageResponse.asModel(): SDImageModel = SDImageModel(
    url = url.orEmpty(),
    style = style?.asModel(),
)

private fun StoreContributorImageStyleResponse.asModel(): SDImageStyleModel = SDImageStyleModel(
    width = width,
    height = height,
)

private fun StoreContributorButtonResponse?.asModel(): SDButtonModel {
    val linkModel = this?.link?.takeIf { !it.link.isNullOrBlank() }?.asModel()
    return SDButtonModel(
        text = this?.text.asModel(),
        image = this?.image?.takeIf { !it.url.isNullOrBlank() }?.asModel(),
        imageAlignment = this?.imageAlignment,
        link = linkModel,
        customAction = if (linkModel == null) this?.customAction?.asModelOrNull() else null,
        style = this?.style?.asModel(),
        clickLog = this?.clickLog?.asModel(),
    )
}

private fun StoreContributorChipResponse.asModel(): SDChipModel = SDChipModel(
    image = image?.takeIf { !it.url.isNullOrBlank() }?.asModel(),
    text = text.asModel(),
    additionalText = additionalText?.asModel(),
    style = style?.asModel(),
    imageAlignment = imageAlignment,
    contentSpacing = contentSpacing,
)

private fun StoreContributorCustomActionResponse.asModelOrNull(): SDCustomActionModel? {
    val action = actionType?.takeIf(String::isNotBlank) ?: return null
    return SDCustomActionModel(
        actionType = action,
        extraParams = extraParams.orEmpty().mapValues { it.value.asClickLogValue() },
    )
}

private fun StoreContributorClickLogResponse.asModel(): SDClickLogModel = SDClickLogModel(
    eventType = eventType.orEmpty(),
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asClickLogValue() },
)

private fun SDPageViewLogResponse.asModelOrNull(): SDViewLogModel? {
    val screen = screenName?.takeIf(String::isNotBlank) ?: return null
    return SDViewLogModel(
        screenName = screen,
        eventType = eventType.orEmpty(),
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
            val doubleValue = number.toDouble()
            if (!primitive.asString.contains('.') && doubleValue == doubleValue.toLong().toDouble()) {
                val longValue = number.toLong()
                if (longValue in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
                    SDClickLogValue.IntValue(longValue.toInt())
                } else {
                    SDClickLogValue.LongValue(longValue)
                }
            } else {
                SDClickLogValue.DoubleValue(doubleValue)
            }
        }
        else -> SDClickLogValue.StringValue(primitive.asString)
    }
}

private fun StoreContributorLinkResponse.asModel(): SDLinkModel = SDLinkModel(
    type = type.orEmpty(),
    link = link.orEmpty(),
)

private fun StoreContributorSurfaceStyleResponse.asModel(): SDSurfaceStyleModel = SDSurfaceStyleModel(
    backgroundColor = backgroundColor,
    border = border?.asModel(),
)

private fun StoreContributorBorderResponse.asModel(): SDBorderModel = SDBorderModel(
    color = color,
    width = width,
)

private fun StoreContributorCursorResponse.asModel(): SDCursorModel = SDCursorModel(
    nextCursor = nextCursor,
    hasMore = hasMore ?: false,
)
