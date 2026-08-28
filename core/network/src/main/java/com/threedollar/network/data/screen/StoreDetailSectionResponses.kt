package com.threedollar.network.data.screen

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class StoreDetailContentResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("subTitle") val subTitle: SDTextResponse? = null,
    @SerializedName("footerLeftButton") val footerLeftButton: SDButtonResponse? = null,
)

data class StoreDetailBodyResponse(
    @SerializedName("text") val text: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailContentSectionResponse(
    @SerializedName("content") val content: StoreDetailContentResponse? = null,
)

data class StoreDetailPreviewSectionResponse(
    @SerializedName("header") val header: HomeListCardHeaderResponse? = null,
    @SerializedName("metadata") val metadata: HomeListCardMetadataResponse? = null,
    @SerializedName("contributorActionBar") val contributorActionBar: StoreActionBarResponse? = null,
    @SerializedName("actionBars") val actionBars: List<StoreActionBarResponse>? = null,
    @SerializedName("images") val images: List<SDImageResponse>? = null,
    @SerializedName("bodies") val bodies: List<StoreDetailBodyResponse>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("additionalInfos") val additionalInfos: StoreSectionAdditionalInfosResponse? = null,
)

data class StoreDetailTabSectionResponse(
    @SerializedName("tabs") val tabs: List<StoreActionBarResponse>? = null,
)

data class StoreDetailMapSectionResponse(
    @SerializedName("location") val location: SDLocationResponse? = null,
    @SerializedName("footerLeft") val footerLeft: StoreActionBarResponse? = null,
    @SerializedName("footerRight") val footerRight: StoreActionBarResponse? = null,
)

data class StoreDetailEditSectionResponse(
    @SerializedName("actionBars") val actionBars: List<StoreActionBarResponse>? = null,
)

data class StoreDetailHeaderResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("subTitle") val subTitle: SDTextResponse? = null,
    @SerializedName("trailingAction") val trailingAction: SDButtonResponse? = null,
)

data class StoreDetailAppearanceDaySectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("items") val items: List<StoreDetailAppearanceItemResponse>? = null,
)

data class StoreDetailAppearanceItemResponse(
    @SerializedName("leadingText") val leadingText: SDTextResponse? = null,
    @SerializedName("primaryText") val primaryText: SDTextResponse? = null,
    @SerializedName("secondaryText") val secondaryText: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailInfoV1SectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("informationCard") val informationCard: StoreDetailInformationCardResponse? = null,
    @SerializedName("menuCard") val menuCard: StoreDetailMenuCardResponse? = null,
)

data class StoreDetailInformationCardResponse(
    @SerializedName("rows") val rows: List<JsonObject>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailChipGroupRowResponse(
    @SerializedName("label") val label: SDTextResponse? = null,
    @SerializedName("chips") val chips: List<SDChipResponse>? = null,
)

data class StoreDetailInlineOptionRowResponse(
    @SerializedName("label") val label: SDTextResponse? = null,
    @SerializedName("items") val items: List<StoreDetailSelectableTextItemResponse>? = null,
)

data class StoreDetailSelectableTextItemResponse(
    @SerializedName("text") val text: SDTextResponse? = null,
    @SerializedName("isSelected") val isSelected: Boolean? = null,
)

data class StoreDetailTrailingTextRowResponse(
    @SerializedName("label") val label: SDTextResponse? = null,
    @SerializedName("value") val value: SDTextResponse? = null,
)

data class StoreDetailMenuCardResponse(
    @SerializedName("groups") val groups: List<StoreDetailMenuGroupResponse>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailMenuGroupResponse(
    @SerializedName("header") val header: SDChipResponse? = null,
    @SerializedName("items") val items: List<StoreDetailTextMenuItemResponse>? = null,
)

data class StoreDetailTextMenuItemResponse(
    @SerializedName("primaryText") val primaryText: SDTextResponse? = null,
    @SerializedName("secondaryText") val secondaryText: SDTextResponse? = null,
)

data class StoreDetailInfoV2SectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("imageGallery") val imageGallery: StoreDetailImageGalleryResponse? = null,
    @SerializedName("detailCard") val detailCard: StoreDetailDetailCardResponse? = null,
    @SerializedName("accountCards") val accountCards: List<StoreDetailAccountCopyCardResponse>? = null,
    @SerializedName("menuListCard") val menuListCard: StoreDetailMenuListCardResponse? = null,
)

data class StoreDetailImageGalleryResponse(
    @SerializedName("images") val images: List<SDImageResponse>? = null,
)

data class StoreDetailDetailCardResponse(
    @SerializedName("rows") val rows: List<JsonObject>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailLinkRowResponse(
    @SerializedName("label") val label: SDTextResponse? = null,
    @SerializedName("value") val value: SDTextResponse? = null,
    @SerializedName("link") val link: SDLinkResponse? = null,
)

data class StoreDetailTextRowResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("body") val body: SDTextResponse? = null,
)

data class StoreDetailAccountCopyCardResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("account") val account: SDChipResponse? = null,
    @SerializedName("copyButton") val copyButton: SDButtonResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailMenuListCardResponse(
    @SerializedName("items") val items: List<StoreDetailImageMenuItemResponse>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailImageMenuItemResponse(
    @SerializedName("image") val image: SDImageResponse? = null,
    @SerializedName("primaryText") val primaryText: SDTextResponse? = null,
    @SerializedName("secondaryText") val secondaryText: SDTextResponse? = null,
)

data class StoreDetailCouponSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("cards") val cards: List<StoreDetailCouponCardResponse>? = null,
)

data class StoreDetailCouponCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("badge") val badge: SDChipResponse? = null,
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("subTitle") val subTitle: SDTextResponse? = null,
    @SerializedName("trailingButton") val trailingButton: SDButtonResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class StoreDetailRatingResponse(
    @SerializedName("images") val images: List<SDImageResponse>? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailSummaryResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("stars") val stars: StoreDetailRatingResponse? = null,
    @SerializedName("rating") val rating: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailHistoryResponse(
    @SerializedName("items") val items: List<SDChipResponse>? = null,
    @SerializedName("moreText") val moreText: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailVisitSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("summary") val summary: StoreDetailVisitSummaryResponse? = null,
    @SerializedName("history") val history: StoreDetailHistoryResponse? = null,
)

data class StoreDetailVisitSummaryResponse(
    @SerializedName("chips") val chips: List<SDChipResponse>? = null,
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("stars") val stars: StoreDetailRatingResponse? = null,
    @SerializedName("rating") val rating: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailToggleActionResponse(
    @SerializedName("selected") val selected: SDButtonResponse? = null,
    @SerializedName("unselected") val unselected: SDButtonResponse? = null,
    @SerializedName("isSelected") val isSelected: Boolean? = null,
)

data class StoreDetailPostSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("cards") val cards: List<StoreDetailPostCardResponse>? = null,
)

data class StoreDetailPostCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("header") val header: SDChipResponse? = null,
    @SerializedName("images") val images: List<SDImageResponse>? = null,
    @SerializedName("body") val body: SDTextResponse? = null,
    @SerializedName("like") val like: StoreDetailToggleActionResponse? = null,
    @SerializedName("link") val link: SDLinkResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class StoreDetailReviewSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("summary") val summary: StoreDetailSummaryResponse? = null,
    @SerializedName("cards") val cards: List<StoreDetailReviewCardResponse>? = null,
    @SerializedName("more") val more: StoreActionBarResponse? = null,
)

data class StoreDetailReviewCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("metadata") val metadata: List<SDChipResponse>? = null,
    @SerializedName("stars") val stars: StoreDetailRatingResponse? = null,
    @SerializedName("images") val images: List<SDImageResponse>? = null,
    @SerializedName("body") val body: SDTextResponse? = null,
    @SerializedName("like") val like: StoreDetailToggleActionResponse? = null,
    @SerializedName("reply") val reply: StoreDetailReplyResponse? = null,
    @SerializedName("link") val link: SDLinkResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class StoreDetailReplyResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("body") val body: SDTextResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
)

data class StoreDetailAdMobSectionResponse(
    @SerializedName("cards") val cards: List<StoreDetailAdMobCardResponse>? = null,
)

data class StoreDetailAdMobCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
    @SerializedName("impressionLog") val impressionLog: SDImpressionLogResponse? = null,
)

data class StoreDetailImageSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("cards") val cards: List<StoreDetailImageCardResponse>? = null,
)

data class StoreDetailImageCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("image") val image: SDImageResponse? = null,
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("subTitle") val subTitle: SDTextResponse? = null,
    @SerializedName("link") val link: SDLinkResponse? = null,
    @SerializedName("customAction") val customAction: SDCustomActionResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class StoreDetailRelatedStoresSectionResponse(
    @SerializedName("header") val header: StoreDetailHeaderResponse? = null,
    @SerializedName("cards") val cards: List<StoreDetailRelatedStoreCardResponse>? = null,
    @SerializedName("reference") val reference: List<StoreDetailExperimentReferenceResponse>? = null,
    @SerializedName("impressionLog") val impressionLog: SDImpressionLogResponse? = null,
)

data class StoreDetailRelatedStoreCardResponse(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("image") val image: SDImageResponse? = null,
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("metricLabel") val metricLabel: List<SDChipResponse>? = null,
    @SerializedName("contextLabel") val contextLabel: List<SDChipResponse>? = null,
    @SerializedName("link") val link: SDLinkResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("refs") val refs: List<StoreDetailStoreReferenceResponse>? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class StoreDetailStoreReferenceResponse(
    @SerializedName("storeId") val storeId: String? = null,
    @SerializedName("storeType") val storeType: String? = null,
)

data class StoreDetailExperimentReferenceResponse(
    @SerializedName("experimentKey") val experimentKey: String? = null,
    @SerializedName("variant") val variant: String? = null,
)
