package com.threedollar.data.screen

import com.google.gson.JsonElement
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.element.SDImageAlignment
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetViewType
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCategoryFilterModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsBodyBlockModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsBodyHeadingModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSummaryModel
import com.threedollar.common.sdui.model.section.home.SDHomeTabAppearanceModel
import com.threedollar.network.data.screen.HomeBottomSheetTabAppearanceResponse
import com.threedollar.network.data.screen.HomeBottomSheetTabResponse
import com.threedollar.network.data.screen.HomeCurationCardResponse
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationCategoryFilterResponse
import com.threedollar.network.data.screen.HomeCurationItemResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import com.threedollar.network.data.screen.HomeNewsBodyBlockResponse
import com.threedollar.network.data.screen.HomeNewsItemResponse
import com.threedollar.network.data.screen.HomeNewsSectionResponse
import com.threedollar.network.data.screen.SDButtonResponse
import com.threedollar.network.data.screen.SDChipResponse
import com.threedollar.network.data.screen.SDClickLogResponse
import com.threedollar.network.data.screen.SDHeaderResponse
import com.threedollar.network.data.screen.SDImageResponse
import com.threedollar.network.data.screen.SDImpressionLogResponse
import com.threedollar.network.data.screen.SDLinkResponse
import com.threedollar.network.data.screen.SDSurfaceStyleResponse
import com.threedollar.network.data.screen.SDTextResponse

fun HomeCurationSectionResponse.asCurationModel(): SDHomeCurationSectionModel = SDHomeCurationSectionModel(
    items = items.orEmpty().map { it.asCurationItemModel() },
    impressionLog = impressionLog?.asSduiLogModel(),
)

/** 모르는 `type` 이나 `cardId` 가 없는 항목은 그릴 수 없으므로 뺀다. */
fun HomeNewsSectionResponse.asNewsModel(): SDHomeNewsSectionModel = SDHomeNewsSectionModel(
    header = header?.asSduiModel(),
    items = items.orEmpty().mapNotNull { it.asNewsItemModelOrNull() },
    nextCursor = cursor?.nextCursor,
    hasMore = cursor?.hasMore ?: false,
    impressionLog = impressionLog?.asSduiLogModel(),
)

private fun HomeNewsItemResponse.asNewsItemModelOrNull(): SDHomeNewsItemModel? {
    val id = cardId?.takeIf { it.isNotBlank() } ?: return null
    return when (type) {
        "CONTENT_CARD" -> SDHomeNewsItemModel.ContentCard(
            cardId = id,
            badge = header?.badge?.asSduiModel(),
            trailingText = header?.trailingText?.asSduiModel(),
            summary = header?.summary?.let { summary ->
                SDHomeNewsSummaryModel(
                    leadingImage = summary.leadingImage?.asSduiImageOrNull(),
                    overline = summary.overline?.asSduiModel(),
                    title = summary.title?.asSduiModel(),
                    metadata = summary.metadata.orEmpty().map { it.asSduiModel() },
                )
            },
            bodyBlocks = bodyBlocks.orEmpty().map { it.asNewsBodyBlockModel() },
            link = link?.takeIf { !it.link.isNullOrBlank() }?.asSduiModel(),
            clickLog = clickLog?.asSduiLogModel(),
            impressionLog = impressionLog?.asSduiLogModel(),
        )

        "ADMOB_CARD" -> SDHomeNewsItemModel.AdMob(
            card = SDAdMobCardModel(
                cardId = id,
                height = height ?: 0,
                adUnitId = adUnitId,
                clickLog = clickLog?.asSduiLogModel(),
                impressionLog = impressionLog?.asSduiLogModel(),
            ),
        )

        else -> null
    }
}

private fun HomeNewsBodyBlockResponse.asNewsBodyBlockModel(): SDHomeNewsBodyBlockModel = SDHomeNewsBodyBlockModel(
    heading = heading?.let { heading ->
        SDHomeNewsBodyHeadingModel(
            leadingImage = heading.leadingImage?.asSduiImageOrNull(),
            title = heading.title?.asSduiModel(),
            trailingImages = heading.trailingImages.orEmpty().mapNotNull { it.asSduiImageOrNull() },
        )
    },
    images = images.orEmpty().mapNotNull { it.asSduiImageOrNull() },
    textLeadingImage = textLeadingImage?.asSduiImageOrNull(),
    text = text?.asSduiModel(),
    style = style?.asSduiModel(),
)

private fun SDImageResponse.asSduiImageOrNull(): SDImageModel? = takeIf { !it.url.isNullOrBlank() }?.asSduiModel()

fun HomeCurationCardsResponse.asCurationCardsModel(): SDHomeCurationCardsModel = SDHomeCurationCardsModel(
    cards = cards.orEmpty().mapNotNull { it.asCurationCardModelOrNull() },
)

internal fun HomeBottomSheetTabResponse.asCurationTabModelOrNull(): SDHomeBottomSheetTabModel? {
    val id = tabId?.takeIf { it.isNotBlank() } ?: return null
    val supportedViewType = viewType?.takeIf { it in SDHomeBottomSheetViewType.supported } ?: return null
    val selectedModel = selected?.asSduiModel() ?: return null
    val unselectedModel = unselected?.asSduiModel() ?: return null
    return SDHomeBottomSheetTabModel(
        tabId = id,
        viewType = supportedViewType,
        selected = selectedModel,
        unselected = unselectedModel,
        defaultSelected = defaultSelected ?: false,
        clickLog = clickLog?.asSduiLogModel(),
    )
}

private fun HomeBottomSheetTabAppearanceResponse.asSduiModel(): SDHomeTabAppearanceModel = SDHomeTabAppearanceModel(
    title = title?.asSduiModel(),
    style = style?.asSduiModel(),
)

private fun HomeCurationItemResponse.asCurationItemModel(): SDHomeCurationItemModel = when (type) {
    "CAROUSEL" -> SDHomeCurationItemModel.Carousel(
        carouselId = carouselId.orEmpty(),
        header = header?.asSduiModel(),
        defaultCategoryId = defaultCategoryId.orEmpty(),
        categoryFilters = categoryFilters.orEmpty().mapNotNull { it.asCurationCategoryModelOrNull() },
        cards = cards.orEmpty().mapNotNull { it.asCurationCardModelOrNull() },
    )

    "ADMOB_CARD" -> SDHomeCurationItemModel.AdMob(
        card = SDAdMobCardModel(
            cardId = cardId.orEmpty(),
            height = height ?: 0,
            adUnitId = adUnitId,
            clickLog = clickLog?.asSduiLogModel(),
            impressionLog = impressionLog?.asSduiLogModel(),
        ),
    )

    else -> SDHomeCurationItemModel.Unknown(rawType = type)
}

private fun HomeCurationCategoryFilterResponse.asCurationCategoryModelOrNull(): SDHomeCurationCategoryFilterModel? {
    val id = categoryId?.takeIf { it.isNotBlank() } ?: return null
    return SDHomeCurationCategoryFilterModel(
        categoryId = id,
        selected = selected?.asSduiModel(),
        unselected = unselected?.asSduiModel(),
        clickLog = clickLog?.asSduiLogModel(),
    )
}

private fun HomeCurationCardResponse.asCurationCardModelOrNull(): SDCardModel? = when (type) {
    "IMAGE_PREVIEW_CARD" -> ImagePreviewCardModel(
        cardId = cardId.orEmpty(),
        image = image?.asSduiModel(),
        title = title?.asSduiModel(),
        metricLabel = metricLabel.orEmpty().map { it.asSduiModel() },
        contextLabel = contextLabel.orEmpty().map { it.asSduiModel() },
        link = link?.asSduiModel(),
        style = style?.let { ImagePreviewCardModel.Style(backgroundColor = it.backgroundColor.orEmpty()) },
        refs = refs.orEmpty().map { ImagePreviewCardModel.Ref(type = it.type, storeId = it.storeId, storeType = it.storeType) },
        clickLog = clickLog?.asSduiLogModel(),
        impressionLog = impressionLog?.asSduiLogModel(),
    )

    "ADMOB_CARD" -> SDAdMobCardModel(
        cardId = cardId.orEmpty(),
        height = height ?: 0,
        adUnitId = adUnitId,
        clickLog = clickLog?.asSduiLogModel(),
        impressionLog = impressionLog?.asSduiLogModel(),
    )

    "AD_CARD" -> sduiAdCardModel(
        cardId = cardId,
        title = title,
        subTitle = subTitle,
        labels = labels,
        image = image,
        trailingIcon = trailingIcon,
        link = link,
        clickLog = clickLog,
        impressionLog = impressionLog,
    )

    else -> null
}

internal fun sduiAdCardModel(
    cardId: String?,
    title: SDTextResponse?,
    subTitle: SDTextResponse?,
    labels: List<SDChipResponse>?,
    image: SDImageResponse?,
    trailingIcon: SDImageResponse?,
    link: SDLinkResponse?,
    clickLog: SDClickLogResponse?,
    impressionLog: SDImpressionLogResponse?,
): SDAdCardModel = SDAdCardModel(
    cardId = cardId.orEmpty(),
    title = title?.asSduiModel(),
    subTitle = subTitle?.asSduiModel(),
    labels = labels.orEmpty().map { it.asSduiModel() },
    image = image?.takeIf { !it.url.isNullOrBlank() }?.asSduiModel(),
    trailingIcon = trailingIcon?.takeIf { !it.url.isNullOrBlank() }?.asSduiModel(),
    link = link?.takeIf { !it.link.isNullOrBlank() }?.asSduiModel(),
    clickLog = clickLog?.asSduiLogModel(),
    impressionLog = impressionLog?.asSduiLogModel(),
)

private fun SDHeaderResponse.asSduiModel(): SDHeaderModel = SDHeaderModel(
    title = title?.asSduiModel(),
    subTitle = subTitle?.asSduiModel(),
    trailingAction = trailingAction?.asSduiModel(),
)

private fun SDButtonResponse.asSduiModel(): SDButtonModel = SDButtonModel(
    text = text?.asSduiModel(),
    image = image?.asSduiModel(),
    imageAlignment = SDImageAlignment.entries.firstOrNull { it.name == imageAlignment },
    link = link?.asSduiModel(),
    customAction = customAction?.let { action ->
        SDCustomActionModel(
            actionType = SDCustomActionType.entries.firstOrNull { it.name == action.actionType },
            extraParams = action.extraParams?.mapValues { it.value.asSduiValue() },
        )
    },
    style = style?.asSduiModel(),
)

private fun SDChipResponse.asSduiModel(): SDChipModel = SDChipModel(
    image = image?.asSduiModel(),
    text = text?.asSduiModel(),
    additionalText = additionalText?.asSduiModel(),
    imageAlignment = SDImageAlignment.entries.firstOrNull { it.name == imageAlignment },
    contentSpacing = contentSpacing?.toFloat(),
    style = style?.asSduiModel(),
)

private fun SDTextResponse.asSduiModel(): SDTextModel = SDTextModel(
    text = text,
    isHtml = isHtml,
    fontColor = fontColor,
)

private fun SDImageResponse.asSduiModel(): SDImageModel = SDImageModel(
    url = url,
    style = style?.let { imageStyle ->
        val width = imageStyle.width ?: return@let null
        val height = imageStyle.height ?: return@let null
        SDImageModel.Style(width = width.toFloat(), height = height.toFloat(), dimmed = imageStyle.dimmed)
    },
)

private fun SDLinkResponse.asSduiModel(): SDLink = SDLink(
    type = SDLinkType.entries.firstOrNull { it.name == type },
    link = link,
)

private fun SDSurfaceStyleResponse.asSduiModel(): SDSurfaceStyleModel = SDSurfaceStyleModel(
    backgroundColor = backgroundColor,
    border = border?.let { SDSurfaceStyleModel.Border(color = it.color, width = it.width?.toFloat()) },
)

private fun SDClickLogResponse.asSduiLogModel(): SDLogModel = SDLogModel(
    eventType = eventType,
    screenName = screenName,
    objectType = objectType,
    objectId = objectId,
    extraParameters = extraParameters?.mapValues { it.value.asSduiValue() },
)

private fun SDImpressionLogResponse.asSduiLogModel(): SDLogModel = SDLogModel(
    eventType = eventType,
    screenName = screenName,
    objectType = objectType,
    objectId = objectId,
    extraParameters = extraParameters?.mapValues { it.value.asSduiValue() },
)

private fun JsonElement.asSduiValue(): Any? = when {
    isJsonNull -> null
    isJsonObject -> asJsonObject.entrySet().associate { it.key to it.value.asSduiValue() }
    isJsonArray -> asJsonArray.map { it.asSduiValue() }
    asJsonPrimitive.isBoolean -> asBoolean
    asJsonPrimitive.isNumber -> asString.toLongOrNull() ?: asDouble
    else -> asString
}
