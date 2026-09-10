package com.threedollar.common.serverdriven.model

data class StoreDetailScreenModel(
    val sections: List<StoreDetailSectionModel>,
    val viewLog: SDViewLogModel,
)

sealed interface StoreDetailSectionModel {
    val type: String

    data class AdMob(
        override val type: String,
        val cards: List<StoreDetailAdMobCardModel>,
    ) : StoreDetailSectionModel
    data class AppearanceDay(
        override val type: String,
        val header: SDHeaderModel,
        val items: List<StoreDetailAppearanceItemModel>,
    ) : StoreDetailSectionModel
    data class Callout(
        override val type: String,
        val content: StoreDetailCalloutContentModel,
    ) : StoreDetailSectionModel
    data class Coupon(
        override val type: String,
        val header: SDHeaderModel?,
        val cards: List<StoreDetailCouponCardModel>,
    ) : StoreDetailSectionModel
    data class Cta(
        override val type: String,
        val content: StoreDetailContentModel,
    ) : StoreDetailSectionModel
    data class Edit(
        override val type: String,
        val actionBars: List<StoreActionBarModel>,
        val map: StoreDetailMapModel? = null,
    ) : StoreDetailSectionModel
    data class Image(
        override val type: String,
        val header: SDHeaderModel,
        val cards: List<StoreDetailImageCardModel>,
    ) : StoreDetailSectionModel
    data class InfoV1(
        override val type: String,
        val header: SDHeaderModel,
        val informationCard: StoreDetailInformationCardModel?,
        val menuCard: StoreDetailMenuCardModel?,
    ) : StoreDetailSectionModel
    data class InfoV2(
        override val type: String,
        val header: SDHeaderModel,
        val imageGallery: StoreDetailImageGalleryModel?,
        val detailCard: StoreDetailDetailCardModel?,
        val accountCards: List<StoreDetailAccountCopyCardModel>,
        val menuListCard: StoreDetailMenuListCardModel?,
    ) : StoreDetailSectionModel
    data class Map(
        override val type: String,
        val location: SDLocationModel,
        val footerLeft: StoreActionBarModel?,
        val footerRight: StoreActionBarModel,
    ) : StoreDetailSectionModel
    data class Margin(
        override val type: String,
        val height: Int,
    ) : StoreDetailSectionModel
    data class Post(
        override val type: String,
        val header: SDHeaderModel,
        val cards: List<StoreDetailPostCardModel>,
    ) : StoreDetailSectionModel
    data class Preview(
        override val type: String,
        val header: HomeListCardHeaderModel,
        val metadata: HomeListCardMetadataModel,
        val contributorActionBar: StoreActionBarModel?,
        val actionBars: List<StoreActionBarModel>,
        val images: List<SDImageModel>,
        val bodies: List<StoreDetailBodyModel>,
        val style: SDSurfaceStyleModel,
        val additionalInfos: StoreSectionAdditionalInfosModel,
    ) : StoreDetailSectionModel
    data class RelatedStores(
        override val type: String,
        val header: SDHeaderModel,
        val cards: List<StoreDetailRelatedStoreCardModel>,
        val references: List<StoreDetailExperimentReferenceModel>,
        val impressionLog: SDImpressionLogModel,
    ) : StoreDetailSectionModel
    data class Review(
        override val type: String,
        val header: SDHeaderModel,
        val summary: StoreDetailSummaryModel,
        val cards: List<StoreDetailReviewCardModel>,
        val more: StoreActionBarModel?,
    ) : StoreDetailSectionModel
    data class Tab(
        override val type: String,
        val tabs: List<StoreActionBarModel>,
    ) : StoreDetailSectionModel
    data class Visit(
        override val type: String,
        val header: SDHeaderModel,
        val summary: StoreDetailVisitSummaryModel,
        val history: StoreDetailHistoryModel,
    ) : StoreDetailSectionModel
}

data class StoreDetailContentModel(
    val title: SDTextModel,
    val subTitle: SDTextModel? = null,
    val footerLeftButton: SDButtonModel? = null,
)

data class StoreDetailCalloutContentModel(
    val image: SDImageModel,
    val text: SDTextModel,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailMapModel(
    val location: SDLocationModel,
    val footerLeft: StoreActionBarModel?,
    val footerRight: StoreActionBarModel,
)

data class StoreDetailBodyModel(
    val text: SDTextModel,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailAppearanceItemModel(
    val leadingText: SDTextModel,
    val primaryText: SDTextModel,
    val secondaryText: SDTextModel?,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailInformationCardModel(
    val rows: List<StoreDetailInformationRowModel>,
    val style: SDSurfaceStyleModel,
)

sealed interface StoreDetailInformationRowModel {
    val type: String

    data class ChipGroup(
        override val type: String,
        val label: SDTextModel,
        val chips: List<SDChipModel>,
    ) : StoreDetailInformationRowModel

    data class InlineOption(
        override val type: String,
        val label: SDTextModel,
        val items: List<StoreDetailSelectableTextItemModel>,
    ) : StoreDetailInformationRowModel

    data class TrailingText(
        override val type: String,
        val label: SDTextModel,
        val value: SDTextModel,
    ) : StoreDetailInformationRowModel
}

data class StoreDetailSelectableTextItemModel(
    val text: SDTextModel,
    val isSelected: Boolean,
)

data class StoreDetailMenuCardModel(
    val groups: List<StoreDetailMenuGroupModel>,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailMenuGroupModel(
    val header: SDChipModel,
    val items: List<StoreDetailTextMenuItemModel>,
)

data class StoreDetailTextMenuItemModel(
    val primaryText: SDTextModel,
    val secondaryText: SDTextModel?,
)

data class StoreDetailImageGalleryModel(
    val images: List<SDImageModel>,
)

data class StoreDetailDetailCardModel(
    val rows: List<StoreDetailDetailRowModel>,
    val style: SDSurfaceStyleModel,
)

sealed interface StoreDetailDetailRowModel {
    val type: String

    data class Link(
        override val type: String,
        val label: SDTextModel,
        val value: SDTextModel,
        val link: SDLinkModel,
    ) : StoreDetailDetailRowModel

    data class Text(
        override val type: String,
        val title: SDTextModel,
        val body: SDTextModel,
    ) : StoreDetailDetailRowModel
}

data class StoreDetailAccountCopyCardModel(
    val title: SDTextModel,
    val account: SDChipModel,
    val copyButton: SDButtonModel,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailMenuListCardModel(
    val items: List<StoreDetailImageMenuItemModel>,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailImageMenuItemModel(
    val image: SDImageModel?,
    val primaryText: SDTextModel,
    val secondaryText: SDTextModel?,
)

data class StoreDetailCouponCardModel(
    val cardId: String,
    val badge: SDChipModel?,
    val title: SDTextModel,
    val subTitle: SDTextModel,
    val trailingButton: SDButtonModel,
    val style: SDSurfaceStyleModel,
    val clickLog: SDClickLogModel?,
)

data class StoreDetailRatingModel(
    val images: List<SDImageModel>,
    val style: SDSurfaceStyleModel?,
)

data class StoreDetailSummaryModel(
    val title: SDTextModel,
    val stars: StoreDetailRatingModel,
    val rating: SDTextModel,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailVisitSummaryModel(
    val chips: List<SDChipModel>,
    val ratingSummary: StoreDetailSummaryModel?,
)

data class StoreDetailHistoryModel(
    val items: List<SDChipModel>,
    val moreText: SDTextModel?,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailToggleActionModel(
    val selected: SDButtonModel,
    val unselected: SDButtonModel,
    val isSelected: Boolean,
)

data class StoreDetailPostCardModel(
    val cardId: String,
    val header: SDChipModel,
    val images: List<SDImageModel>,
    val body: SDTextModel,
    val like: StoreDetailToggleActionModel?,
    val link: SDLinkModel?,
    val style: SDSurfaceStyleModel,
    val clickLog: SDClickLogModel?,
)

data class StoreDetailReviewCardModel(
    val cardId: String,
    val header: SDHeaderModel,
    val metadata: List<SDChipModel>,
    val stars: StoreDetailRatingModel,
    val images: List<SDImageModel>,
    val body: SDTextModel,
    val like: StoreDetailToggleActionModel?,
    val reply: StoreDetailReplyModel?,
    val link: SDLinkModel?,
    val style: SDSurfaceStyleModel,
    val clickLog: SDClickLogModel?,
)

data class StoreDetailReplyModel(
    val header: SDHeaderModel,
    val body: SDTextModel,
    val style: SDSurfaceStyleModel,
)

data class StoreDetailAdMobCardModel(
    val cardId: String,
    val clickLog: SDClickLogModel,
    val impressionLog: SDImpressionLogModel,
)

data class StoreDetailImageCardModel(
    val cardId: String,
    val image: SDImageModel,
    val title: SDTextModel?,
    val subTitle: SDTextModel?,
    val link: SDLinkModel?,
    val customAction: SDCustomActionModel?,
    val style: SDSurfaceStyleModel,
    val clickLog: SDClickLogModel?,
)

data class StoreDetailRelatedStoreCardModel(
    val cardId: String,
    val image: SDImageModel,
    val title: SDTextModel,
    val metricLabel: List<SDChipModel>,
    val contextLabel: List<SDChipModel>,
    val link: SDLinkModel?,
    val style: SDSurfaceStyleModel,
    val refs: List<StoreDetailStoreReferenceModel>,
    val clickLog: SDClickLogModel?,
)

data class StoreDetailStoreReferenceModel(
    val storeId: String,
    val storeType: String,
)

data class StoreDetailExperimentReferenceModel(
    val experimentKey: String,
    val variant: String,
)
