package com.threedollar.data.screen

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
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
import com.threedollar.common.serverdriven.model.StoreDetailBodyModel
import com.threedollar.common.serverdriven.model.StoreDetailAdMobCardModel
import com.threedollar.common.serverdriven.model.StoreDetailAccountCopyCardModel
import com.threedollar.common.serverdriven.model.StoreDetailAppearanceItemModel
import com.threedollar.common.serverdriven.model.StoreDetailContentModel
import com.threedollar.common.serverdriven.model.StoreDetailCouponCardModel
import com.threedollar.common.serverdriven.model.StoreDetailDetailCardModel
import com.threedollar.common.serverdriven.model.StoreDetailDetailRowModel
import com.threedollar.common.serverdriven.model.StoreDetailImageGalleryModel
import com.threedollar.common.serverdriven.model.StoreDetailImageMenuItemModel
import com.threedollar.common.serverdriven.model.StoreDetailInformationCardModel
import com.threedollar.common.serverdriven.model.StoreDetailInformationRowModel
import com.threedollar.common.serverdriven.model.StoreDetailMenuCardModel
import com.threedollar.common.serverdriven.model.StoreDetailMenuGroupModel
import com.threedollar.common.serverdriven.model.StoreDetailMenuListCardModel
import com.threedollar.common.serverdriven.model.StoreDetailHistoryModel
import com.threedollar.common.serverdriven.model.StoreDetailImageCardModel
import com.threedollar.common.serverdriven.model.StoreDetailPostCardModel
import com.threedollar.common.serverdriven.model.StoreDetailRatingModel
import com.threedollar.common.serverdriven.model.StoreDetailReplyModel
import com.threedollar.common.serverdriven.model.StoreDetailRelatedStoreCardModel
import com.threedollar.common.serverdriven.model.StoreDetailExperimentReferenceModel
import com.threedollar.common.serverdriven.model.StoreDetailReviewCardModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSelectableTextItemModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.serverdriven.model.StoreDetailSummaryModel
import com.threedollar.common.serverdriven.model.StoreDetailStoreReferenceModel
import com.threedollar.common.serverdriven.model.StoreDetailTextMenuItemModel
import com.threedollar.common.serverdriven.model.StoreDetailToggleActionModel
import com.threedollar.common.serverdriven.model.StoreDetailVisitSummaryModel
import com.threedollar.common.serverdriven.model.StoreSectionAdditionalInfosModel
import com.threedollar.network.data.screen.HomeListCardHeaderResponse
import com.threedollar.network.data.screen.HomeListCardMetadataResponse
import com.threedollar.network.data.screen.SDBorderResponse
import com.threedollar.network.data.screen.SDButtonResponse
import com.threedollar.network.data.screen.SDChipResponse
import com.threedollar.network.data.screen.SDClickLogResponse
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
import com.threedollar.network.data.screen.StoreDetailAdMobCardResponse
import com.threedollar.network.data.screen.StoreDetailAdMobSectionResponse
import com.threedollar.network.data.screen.StoreDetailContentResponse
import com.threedollar.network.data.screen.StoreDetailContentSectionResponse
import com.threedollar.network.data.screen.StoreDetailCouponCardResponse
import com.threedollar.network.data.screen.StoreDetailCouponSectionResponse
import com.threedollar.network.data.screen.StoreDetailAppearanceDaySectionResponse
import com.threedollar.network.data.screen.StoreDetailAppearanceItemResponse
import com.threedollar.network.data.screen.StoreDetailChipGroupRowResponse
import com.threedollar.network.data.screen.StoreDetailDetailCardResponse
import com.threedollar.network.data.screen.StoreDetailEditSectionResponse
import com.threedollar.network.data.screen.StoreDetailHeaderResponse
import com.threedollar.network.data.screen.StoreDetailInfoV1SectionResponse
import com.threedollar.network.data.screen.StoreDetailInfoV2SectionResponse
import com.threedollar.network.data.screen.StoreDetailImageCardResponse
import com.threedollar.network.data.screen.StoreDetailImageSectionResponse
import com.threedollar.network.data.screen.StoreDetailInformationCardResponse
import com.threedollar.network.data.screen.StoreDetailInlineOptionRowResponse
import com.threedollar.network.data.screen.StoreDetailLinkRowResponse
import com.threedollar.network.data.screen.StoreDetailMapSectionResponse
import com.threedollar.network.data.screen.StoreDetailMenuCardResponse
import com.threedollar.network.data.screen.StoreDetailPostCardResponse
import com.threedollar.network.data.screen.StoreDetailPostSectionResponse
import com.threedollar.network.data.screen.StoreDetailPreviewSectionResponse
import com.threedollar.network.data.screen.StoreDetailReviewCardResponse
import com.threedollar.network.data.screen.StoreDetailReviewSectionResponse
import com.threedollar.network.data.screen.StoreDetailRelatedStoresSectionResponse
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import com.threedollar.network.data.screen.StoreDetailSummaryResponse
import com.threedollar.network.data.screen.StoreDetailTabSectionResponse
import com.threedollar.network.data.screen.StoreDetailTextRowResponse
import com.threedollar.network.data.screen.StoreDetailToggleActionResponse
import com.threedollar.network.data.screen.StoreDetailTrailingTextRowResponse
import com.threedollar.network.data.screen.StoreDetailVisitSectionResponse
import com.threedollar.network.data.screen.StoreDetailVisitSummaryResponse
import com.threedollar.network.data.screen.StoreSectionAdditionalInfosResponse

fun StoreDetailScreenResponse.asStoreDetailModelOrNull(): StoreDetailScreenModel? {
    val sectionValues = sections ?: return null
    val viewLogValue = viewLog?.asStoreDetailViewLogOrNull() ?: return null
    return StoreDetailScreenModel(
        sections = sectionValues.mapNotNull(JsonObject::asStoreDetailSectionModelOrNull),
        viewLog = viewLogValue,
    )
}

private fun JsonObject.asStoreDetailSectionModelOrNull(): StoreDetailSectionModel? {
    val type = get("type")
        ?.takeIf { it.isJsonPrimitive }
        ?.asString
        ?.uppercase()
        ?: return null
    return when (type) {
        "AD_MOB" -> decodeOrNull<StoreDetailAdMobSectionResponse>()?.let { response ->
            val cards = response.cards?.mapNotNull(StoreDetailAdMobCardResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.AdMob(type = type, cards = cards)
        }
        "APPEARANCE_DAY" -> decodeOrNull<StoreDetailAppearanceDaySectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val items = response.items?.mapNotNull(StoreDetailAppearanceItemResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.AppearanceDay(type = type, header = header, items = items)
        }
        "CALLOUT" -> decodeOrNull<StoreDetailContentSectionResponse>()
            ?.content
            ?.asModelOrNull()
            ?.let { StoreDetailSectionModel.Callout(type, it) }
        "COUPON" -> decodeOrNull<StoreDetailCouponSectionResponse>()?.let { response ->
            val cards = response.cards?.mapNotNull(StoreDetailCouponCardResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.Coupon(
                type = type,
                header = response.header?.asModelOrNull(),
                cards = cards,
            )
        }
        "CTA" -> decodeOrNull<StoreDetailContentSectionResponse>()
            ?.content
            ?.asModelOrNull()
            ?.let { StoreDetailSectionModel.Cta(type, it) }
        "EDIT" -> decodeOrNull<StoreDetailEditSectionResponse>()
            ?.actionBars
            ?.mapNotNull(StoreActionBarResponse::asModelOrNull)
            ?.let { StoreDetailSectionModel.Edit(type, it) }
        "IMAGE" -> decodeOrNull<StoreDetailImageSectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val cards = response.cards?.mapNotNull(StoreDetailImageCardResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.Image(type = type, header = header, cards = cards)
        }
        "INFO_V1" -> decodeOrNull<StoreDetailInfoV1SectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            StoreDetailSectionModel.InfoV1(
                type = type,
                header = header,
                informationCard = response.informationCard?.asModelOrNull(),
                menuCard = response.menuCard?.asModelOrNull(),
            )
        }
        "INFO_V2" -> decodeOrNull<StoreDetailInfoV2SectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val accountCards = response.accountCards?.mapNotNull { card ->
                val title = card.title?.asModel() ?: return@mapNotNull null
                val account = card.account?.asModel() ?: return@mapNotNull null
                val copyButton = card.copyButton?.asModel() ?: return@mapNotNull null
                val style = card.style?.asModel() ?: return@mapNotNull null
                StoreDetailAccountCopyCardModel(title, account, copyButton, style)
            } ?: return@let null
            StoreDetailSectionModel.InfoV2(
                type = type,
                header = header,
                imageGallery = response.imageGallery?.images?.let { images ->
                    StoreDetailImageGalleryModel(images.mapNotNull(SDImageResponse::asModelOrNull))
                },
                detailCard = response.detailCard?.asModelOrNull(),
                accountCards = accountCards,
                menuListCard = response.menuListCard?.let { card ->
                    val items = card.items?.mapNotNull { item ->
                        val primary = item.primaryText?.asModel() ?: return@mapNotNull null
                        StoreDetailImageMenuItemModel(
                            image = item.image?.asModelOrNull(),
                            primaryText = primary,
                            secondaryText = item.secondaryText?.asModel(),
                        )
                    } ?: return@let null
                    val style = card.style?.asModel() ?: return@let null
                    StoreDetailMenuListCardModel(items = items, style = style)
                },
            )
        }
        "MAP" -> decodeOrNull<StoreDetailMapSectionResponse>()?.let { response ->
            val location = response.location?.asModelOrNull() ?: return@let null
            val footerRight = response.footerRight?.asModelOrNull() ?: return@let null
            StoreDetailSectionModel.Map(
                type = type,
                location = location,
                footerLeft = response.footerLeft?.asModelOrNull(),
                footerRight = footerRight,
            )
        }
        "POST" -> decodeOrNull<StoreDetailPostSectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val cards = response.cards?.mapNotNull(StoreDetailPostCardResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.Post(type = type, header = header, cards = cards)
        }
        "PREVIEW" -> decodeOrNull<StoreDetailPreviewSectionResponse>()?.asModelOrNull(type)
        "RELATED_STORES" -> decodeOrNull<StoreDetailRelatedStoresSectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val cards = response.cards?.mapNotNull { card ->
                val id = card.cardId?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                val image = card.image?.asModelOrNull() ?: return@mapNotNull null
                val title = card.title?.asModel() ?: return@mapNotNull null
                val metrics = card.metricLabel?.map(SDChipResponse::asModel) ?: return@mapNotNull null
                val contexts = card.contextLabel?.map(SDChipResponse::asModel) ?: return@mapNotNull null
                val style = card.style?.asModel() ?: return@mapNotNull null
                val refs = card.refs?.mapNotNull { reference ->
                    val storeId = reference.storeId?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    val storeType = reference.storeType?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    StoreDetailStoreReferenceModel(storeId = storeId, storeType = storeType)
                } ?: return@mapNotNull null
                StoreDetailRelatedStoreCardModel(
                    cardId = id,
                    image = image,
                    title = title,
                    metricLabel = metrics,
                    contextLabel = contexts,
                    link = card.link?.asModel(),
                    style = style,
                    refs = refs,
                    clickLog = card.clickLog?.asModel(),
                )
            } ?: return@let null
            val references = response.reference?.mapNotNull { reference ->
                val key = reference.experimentKey?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                val variant = reference.variant?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                StoreDetailExperimentReferenceModel(experimentKey = key, variant = variant)
            } ?: return@let null
            val impression = response.impressionLog?.asModel() ?: return@let null
            StoreDetailSectionModel.RelatedStores(
                type = type,
                header = header,
                cards = cards,
                references = references,
                impressionLog = impression,
            )
        }
        "REVIEW" -> decodeOrNull<StoreDetailReviewSectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val summary = response.summary?.asModelOrNull() ?: return@let null
            val cards = response.cards?.mapNotNull(StoreDetailReviewCardResponse::asModelOrNull) ?: return@let null
            StoreDetailSectionModel.Review(
                type = type,
                header = header,
                summary = summary,
                cards = cards,
                more = response.more?.asModelOrNull(),
            )
        }
        "TAB" -> decodeOrNull<StoreDetailTabSectionResponse>()
            ?.tabs
            ?.mapNotNull(StoreActionBarResponse::asModelOrNull)
            ?.let { StoreDetailSectionModel.Tab(type, it) }
        "VISIT" -> decodeOrNull<StoreDetailVisitSectionResponse>()?.let { response ->
            val header = response.header?.asModelOrNull() ?: return@let null
            val summary = response.summary?.asModelOrNull() ?: return@let null
            val historyResponse = response.history ?: return@let null
            val historyItems = historyResponse.items?.map(SDChipResponse::asModel) ?: return@let null
            val historyStyle = historyResponse.style?.asModel() ?: return@let null
            StoreDetailSectionModel.Visit(
                type = type,
                header = header,
                summary = summary,
                history = StoreDetailHistoryModel(
                    items = historyItems,
                    moreText = historyResponse.moreText?.asModel(),
                    style = historyStyle,
                ),
            )
        }
        else -> {
            runCatching { Log.d(TAG, "Skipping unknown store detail section: $type") }
            null
        }
    }
}

private inline fun <reified T> JsonObject.decodeOrNull(): T? = runCatching {
    StoreDetailGson.fromJson(this, T::class.java)
}.getOrNull()

private fun StoreDetailContentResponse.asModelOrNull(): StoreDetailContentModel? {
    val titleModel = title?.asModel() ?: return null
    return StoreDetailContentModel(
        title = titleModel,
        subTitle = subTitle?.asModel(),
        footerLeftButton = footerLeftButton?.asModel(),
    )
}

private fun StoreDetailPreviewSectionResponse.asModelOrNull(type: String): StoreDetailSectionModel.Preview? {
    val headerValue = header ?: return null
    val metadataValue = metadata ?: return null
    val actionBarValues = actionBars ?: return null
    val imageValues = images ?: return null
    val bodyValues = bodies ?: return null
    val styleValue = style ?: return null
    val additionalInfoValue = additionalInfos ?: return null
    return StoreDetailSectionModel.Preview(
        type = type,
        header = headerValue.asModel(),
        metadata = metadataValue.asModel(),
        contributorActionBar = contributorActionBar?.asModelOrNull(),
        actionBars = actionBarValues.mapNotNull(StoreActionBarResponse::asModelOrNull),
        images = imageValues.mapNotNull(SDImageResponse::asModelOrNull),
        bodies = bodyValues.mapNotNull { body ->
            val text = body.text?.asModel() ?: return@mapNotNull null
            val style = body.style?.asModel() ?: return@mapNotNull null
            StoreDetailBodyModel(text = text, style = style)
        },
        style = styleValue.asModel(),
        additionalInfos = additionalInfoValue.asModel(),
    )
}

private fun StoreDetailHeaderResponse.asModelOrNull() = title?.asModel()?.let { titleModel ->
    com.threedollar.common.serverdriven.model.SDHeaderModel(
        title = titleModel,
        subTitle = subTitle?.asModel(),
        trailingAction = trailingAction?.asModel(),
    )
}

private fun StoreDetailAppearanceItemResponse.asModelOrNull(): StoreDetailAppearanceItemModel? {
    val leading = leadingText?.asModel() ?: return null
    val primary = primaryText?.asModel() ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailAppearanceItemModel(
        leadingText = leading,
        primaryText = primary,
        secondaryText = secondaryText?.asModel(),
        style = styleModel,
    )
}

private fun StoreDetailCouponCardResponse.asModelOrNull(): StoreDetailCouponCardModel? {
    val id = cardId?.takeIf(String::isNotBlank) ?: return null
    val titleModel = title?.asModel() ?: return null
    val subTitleModel = subTitle?.asModel() ?: return null
    val trailing = trailingButton?.asModel() ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailCouponCardModel(
        cardId = id,
        badge = badge?.asModel(),
        title = titleModel,
        subTitle = subTitleModel,
        trailingButton = trailing,
        style = styleModel,
        clickLog = clickLog?.asModel(),
    )
}

private fun StoreDetailSummaryResponse.asModelOrNull(): StoreDetailSummaryModel? {
    val titleModel = title?.asModel() ?: return null
    val ratingModel = rating?.asModel() ?: return null
    val styleModel = style?.asModel() ?: return null
    val starsResponse = stars ?: return null
    val starImages = starsResponse.images?.mapNotNull(SDImageResponse::asModelOrNull) ?: return null
    return StoreDetailSummaryModel(
        title = titleModel,
        stars = StoreDetailRatingModel(
            images = starImages,
            style = starsResponse.style?.asModel(),
        ),
        rating = ratingModel,
        style = styleModel,
    )
}

private fun StoreDetailVisitSummaryResponse.asModelOrNull(): StoreDetailVisitSummaryModel? {
    val chipModels = chips?.map(SDChipResponse::asModel).orEmpty()
    val ratingSummary = StoreDetailSummaryResponse(
        title = title,
        stars = stars,
        rating = rating,
        style = style,
    ).asModelOrNull()
    if (chips == null && ratingSummary == null) return null
    return StoreDetailVisitSummaryModel(
        chips = chipModels,
        ratingSummary = ratingSummary,
    )
}

private fun StoreDetailToggleActionResponse.asModelOrNull(): StoreDetailToggleActionModel? {
    val selectedButton = selected?.asModel() ?: return null
    val unselectedButton = unselected?.asModel() ?: return null
    val selectedValue = isSelected ?: return null
    return StoreDetailToggleActionModel(
        selected = selectedButton,
        unselected = unselectedButton,
        isSelected = selectedValue,
    )
}

private fun StoreDetailPostCardResponse.asModelOrNull(): StoreDetailPostCardModel? {
    val id = cardId?.takeIf(String::isNotBlank) ?: return null
    val headerModel = header?.asModel() ?: return null
    val imageModels = images?.mapNotNull(SDImageResponse::asModelOrNull) ?: return null
    val bodyModel = body?.asModel() ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailPostCardModel(
        cardId = id,
        header = headerModel,
        images = imageModels,
        body = bodyModel,
        like = like?.asModelOrNull(),
        link = link?.asModel(),
        style = styleModel,
        clickLog = clickLog?.asModel(),
    )
}

private fun StoreDetailReviewCardResponse.asModelOrNull(): StoreDetailReviewCardModel? {
    val id = cardId?.takeIf(String::isNotBlank) ?: return null
    val headerModel = header?.asModelOrNull() ?: return null
    val metadataModels = metadata?.map(SDChipResponse::asModel) ?: return null
    val starsResponse = stars ?: return null
    val starImages = starsResponse.images?.mapNotNull(SDImageResponse::asModelOrNull) ?: return null
    val imageModels = images?.mapNotNull(SDImageResponse::asModelOrNull) ?: return null
    val bodyModel = body?.asModel() ?: return null
    val styleModel = style?.asModel() ?: return null
    val replyModel = reply?.let { response ->
        val replyHeader = response.header?.asModelOrNull() ?: return@let null
        val replyBody = response.body?.asModel() ?: return@let null
        val replyStyle = response.style?.asModel() ?: return@let null
        StoreDetailReplyModel(replyHeader, replyBody, replyStyle)
    }
    return StoreDetailReviewCardModel(
        cardId = id,
        header = headerModel,
        metadata = metadataModels,
        stars = StoreDetailRatingModel(starImages, starsResponse.style?.asModel()),
        images = imageModels,
        body = bodyModel,
        like = like?.asModelOrNull(),
        reply = replyModel,
        link = link?.asModel(),
        style = styleModel,
        clickLog = clickLog?.asModel(),
    )
}

private fun StoreDetailAdMobCardResponse.asModelOrNull(): StoreDetailAdMobCardModel? {
    val id = cardId?.takeIf(String::isNotBlank) ?: return null
    val click = clickLog?.asModel() ?: return null
    val impression = impressionLog?.asModel() ?: return null
    return StoreDetailAdMobCardModel(cardId = id, clickLog = click, impressionLog = impression)
}

private fun StoreDetailImageCardResponse.asModelOrNull(): StoreDetailImageCardModel? {
    val id = cardId?.takeIf(String::isNotBlank) ?: return null
    val imageModel = image?.asModelOrNull() ?: return null
    val styleModel = style?.asModel() ?: return null
    val linkModel = link?.asModel()
    return StoreDetailImageCardModel(
        cardId = id,
        image = imageModel,
        title = title?.asModel(),
        subTitle = subTitle?.asModel(),
        link = linkModel,
        customAction = if (linkModel == null) customAction?.asModelOrNull() else null,
        style = styleModel,
        clickLog = clickLog?.asModel(),
    )
}

private fun StoreDetailInformationCardResponse.asModelOrNull(): StoreDetailInformationCardModel? {
    val rowModels = rows?.mapNotNull(JsonObject::asInformationRowModelOrNull) ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailInformationCardModel(rows = rowModels, style = styleModel)
}

private fun JsonObject.asInformationRowModelOrNull(): StoreDetailInformationRowModel? {
    val type = stringTypeOrNull() ?: return null
    return when (type) {
        "CHIP_GROUP" -> decodeOrNull<StoreDetailChipGroupRowResponse>()?.let { response ->
            val label = response.label?.asModel() ?: return@let null
            val chips = response.chips?.map(SDChipResponse::asModel) ?: return@let null
            StoreDetailInformationRowModel.ChipGroup(type = type, label = label, chips = chips)
        }
        "INLINE_OPTION" -> decodeOrNull<StoreDetailInlineOptionRowResponse>()?.let { response ->
            val label = response.label?.asModel() ?: return@let null
            val items = response.items?.mapNotNull { item ->
                val text = item.text?.asModel() ?: return@mapNotNull null
                val selected = item.isSelected ?: return@mapNotNull null
                StoreDetailSelectableTextItemModel(text = text, isSelected = selected)
            } ?: return@let null
            StoreDetailInformationRowModel.InlineOption(type = type, label = label, items = items)
        }
        "TRAILING_TEXT" -> decodeOrNull<StoreDetailTrailingTextRowResponse>()?.let { response ->
            val label = response.label?.asModel() ?: return@let null
            val value = response.value?.asModel() ?: return@let null
            StoreDetailInformationRowModel.TrailingText(type = type, label = label, value = value)
        }
        else -> null
    }
}

private fun StoreDetailMenuCardResponse.asModelOrNull(): StoreDetailMenuCardModel? {
    val groupModels = groups?.mapNotNull { group ->
        val header = group.header?.asModel() ?: return@mapNotNull null
        val items = group.items?.mapNotNull { item ->
            val primary = item.primaryText?.asModel() ?: return@mapNotNull null
            StoreDetailTextMenuItemModel(
                primaryText = primary,
                secondaryText = item.secondaryText?.asModel(),
            )
        } ?: return@mapNotNull null
        StoreDetailMenuGroupModel(header = header, items = items)
    } ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailMenuCardModel(groups = groupModels, style = styleModel)
}

private fun StoreDetailDetailCardResponse.asModelOrNull(): StoreDetailDetailCardModel? {
    val rowModels = rows?.mapNotNull(JsonObject::asDetailRowModelOrNull) ?: return null
    val styleModel = style?.asModel() ?: return null
    return StoreDetailDetailCardModel(rows = rowModels, style = styleModel)
}

private fun JsonObject.asDetailRowModelOrNull(): StoreDetailDetailRowModel? {
    val type = stringTypeOrNull() ?: return null
    return when (type) {
        "LINK" -> decodeOrNull<StoreDetailLinkRowResponse>()?.let { response ->
            val label = response.label?.asModel() ?: return@let null
            val value = response.value?.asModel() ?: return@let null
            val link = response.link?.asModel() ?: return@let null
            StoreDetailDetailRowModel.Link(type = type, label = label, value = value, link = link)
        }
        "TEXT" -> decodeOrNull<StoreDetailTextRowResponse>()?.let { response ->
            val title = response.title?.asModel() ?: return@let null
            val body = response.body?.asModel() ?: return@let null
            StoreDetailDetailRowModel.Text(type = type, title = title, body = body)
        }
        else -> null
    }
}

private fun JsonObject.stringTypeOrNull(): String? = get("type")
    ?.takeIf { it.isJsonPrimitive }
    ?.asString
    ?.uppercase()

private fun HomeListCardHeaderResponse.asModel(): HomeListCardHeaderModel = HomeListCardHeaderModel(
    title = title?.asModel(),
    badge = badge?.asModelOrNull(),
)

private fun HomeListCardMetadataResponse.asModel(): HomeListCardMetadataModel = HomeListCardMetadataModel(
    primary = primary.orEmpty().map(SDChipResponse::asModel),
    secondary = secondary.orEmpty().map(SDChipResponse::asModel),
    separator = separator?.asModelOrNull(),
)

private fun StoreSectionAdditionalInfosResponse.asModel(): StoreSectionAdditionalInfosModel =
    StoreSectionAdditionalInfosModel(
        type = type ?: "EMPTY",
        isSubscriber = isSubscriber ?: false,
        storeId = storeId,
        storeType = storeType,
    )

private fun StoreActionBarResponse.asModelOrNull(): StoreActionBarModel? {
    val buttonModel = button?.asModel() ?: return null
    return StoreActionBarModel(
        type = type.orEmpty(),
        button = buttonModel,
        clickLog = clickLog?.asModel(),
    )
}

private fun SDButtonResponse.asModel(): SDButtonModel {
    val linkModel = link?.takeIf { !it.link.isNullOrBlank() }?.asModel()
    return SDButtonModel(
        text = text?.asModel() ?: SDTextModel(text = "", isHtml = false),
        image = image?.asModelOrNull(),
        imageAlignment = imageAlignment,
        link = linkModel,
        customAction = if (linkModel == null) customAction?.asModelOrNull() else null,
        style = style?.asModel(),
        clickLog = clickLog?.asModel(),
    )
}

private fun SDChipResponse.asModel(): SDChipModel = SDChipModel(
    image = image?.asModelOrNull(),
    text = text?.asModel() ?: SDTextModel(text = "", isHtml = false),
    additionalText = additionalText?.asModel(),
    style = style?.asModel(),
    imageAlignment = imageAlignment,
    contentSpacing = contentSpacing,
)

private fun SDTextResponse.asModel(): SDTextModel = SDTextModel(
    text = text.orEmpty(),
    isHtml = isHtml ?: false,
    fontColor = fontColor,
    fontWeight = fontWeight,
)

private fun SDImageResponse.asModelOrNull(): SDImageModel? {
    val imageUrl = url?.takeIf(String::isNotBlank) ?: return null
    return SDImageModel(url = imageUrl.withKnownStoreDetailIconPath(), style = style?.asModel())
}

private fun String.withKnownStoreDetailIconPath(): String {
    val storageRoot = "https://storage.threedollars.co.kr/"
    if (!startsWith(storageRoot)) return this
    val relativeUrl = removePrefix(storageRoot)
    return when (relativeUrl.substringBefore('?').substringBefore('#')) {
        "copy.png", "zoom_3x.png", "deletion.png", "Edit_fill.png", "heart_fill.png", "heart_line.png" -> {
            // These server URLs omit /app/; preserve any query and fragment verbatim.
            "${storageRoot}app/$relativeUrl"
        }
        else -> this
    }
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
    val action = actionType?.takeIf(String::isNotBlank) ?: return null
    return SDCustomActionModel(
        actionType = action,
        extraParams = extraParams.orEmpty().mapValues { it.value.asStoreDetailLogValue() },
    )
}

private fun SDLocationResponse.asModelOrNull(): SDLocationModel? {
    val latitudeValue = latitude ?: return null
    val longitudeValue = longitude ?: return null
    return SDLocationModel(latitude = latitudeValue, longitude = longitudeValue)
}

private fun SDSurfaceStyleResponse.asModel(): SDSurfaceStyleModel = SDSurfaceStyleModel(
    backgroundColor = backgroundColor,
    border = border?.asModel(),
)

private fun SDBorderResponse.asModel(): SDBorderModel = SDBorderModel(
    color = color,
    width = width,
)

private fun SDClickLogResponse.asModel(): SDClickLogModel = SDClickLogModel(
    eventType = eventType.orEmpty(),
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asStoreDetailLogValue() },
)

private fun SDImpressionLogResponse.asModel(): SDImpressionLogModel = SDImpressionLogModel(
    eventType = eventType.orEmpty(),
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asStoreDetailLogValue() },
)

private fun SDPageViewLogResponse.asStoreDetailViewLogOrNull(): SDViewLogModel? {
    val screen = screenName?.takeIf(String::isNotBlank) ?: return null
    return SDViewLogModel(
        screenName = screen,
        eventType = eventType.orEmpty(),
        objectType = objectType.orEmpty(),
        objectId = objectId.orEmpty(),
        extraParameters = extraParameters.orEmpty().mapValues { it.value.asStoreDetailLogValue() },
    )
}

private fun JsonElement.asStoreDetailLogValue(): SDClickLogValue {
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

private const val TAG = "StoreDetailScreenMapper"
private val StoreDetailGson = Gson()
