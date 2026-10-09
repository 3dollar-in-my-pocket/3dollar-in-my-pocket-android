package com.threedollar.network.data.screen

import com.google.gson.annotations.SerializedName

data class HomeCurationSectionResponse(
    @SerializedName("items")
    val items: List<HomeCurationItemResponse>? = emptyList(),
)

data class HomeCurationItemResponse(
    @SerializedName("type") val type: String? = null,
    @SerializedName("carouselId") val carouselId: String? = null,
    @SerializedName("header") val header: SDHeaderResponse? = null,
    @SerializedName("defaultCategoryId") val defaultCategoryId: String? = null,
    @SerializedName("categoryFilters") val categoryFilters: List<HomeCurationCategoryFilterResponse>? = emptyList(),
    @SerializedName("cards") val cards: List<HomeCurationCardResponse>? = emptyList(),
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
    @SerializedName("impressionLog") val impressionLog: SDImpressionLogResponse? = null,
)

data class HomeCurationCategoryFilterResponse(
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("selected") val selected: SDChipResponse? = null,
    @SerializedName("unselected") val unselected: SDChipResponse? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
)

data class HomeCurationCardsResponse(
    @SerializedName("cards") val cards: List<HomeCurationCardResponse>? = emptyList(),
)

data class HomeCurationCardResponse(
    @SerializedName("type") val type: String? = null,
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("image") val image: SDImageResponse? = null,
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("metricLabel") val metricLabel: List<SDChipResponse>? = emptyList(),
    @SerializedName("contextLabel") val contextLabel: List<SDChipResponse>? = emptyList(),
    @SerializedName("link") val link: SDLinkResponse? = null,
    @SerializedName("style") val style: SDSurfaceStyleResponse? = null,
    @SerializedName("refs") val refs: List<HomeCurationCardReferenceResponse>? = emptyList(),
    @SerializedName("height") val height: Int? = null,
    @SerializedName("clickLog") val clickLog: SDClickLogResponse? = null,
    @SerializedName("impressionLog") val impressionLog: SDImpressionLogResponse? = null,
)

data class HomeCurationCardReferenceResponse(
    @SerializedName("type") val type: String? = null,
    @SerializedName("storeId") val storeId: String? = null,
    @SerializedName("storeType") val storeType: String? = null,
)

data class SDHeaderResponse(
    @SerializedName("title") val title: SDTextResponse? = null,
    @SerializedName("subTitle") val subTitle: SDTextResponse? = null,
    @SerializedName("trailingAction") val trailingAction: SDButtonResponse? = null,
)
