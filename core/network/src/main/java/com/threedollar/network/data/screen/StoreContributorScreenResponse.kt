package com.threedollar.network.data.screen

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class StoreContributorScreenResponse(
    @SerializedName("sections")
    val sections: List<StoreContributorSectionResponse>? = emptyList(),
    @SerializedName("viewLog")
    val viewLog: SDPageViewLogResponse? = null,
)

data class StoreContributorHistoriesResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("cards")
    val cards: List<StoreContributorCardResponse>? = emptyList(),
    @SerializedName("cursor")
    val cursor: StoreContributorCursorResponse? = null,
)

data class StoreContributorSectionResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("actionBar")
    val actionBar: StoreContributorActionBarResponse? = null,
    @SerializedName("header")
    val header: StoreContributorHeaderResponse? = null,
    @SerializedName("cards")
    val cards: List<StoreContributorCardResponse>? = emptyList(),
    @SerializedName("cursor")
    val cursor: StoreContributorCursorResponse? = null,
)

data class StoreContributorActionBarResponse(
    @SerializedName("button")
    val button: StoreContributorButtonResponse? = null,
    @SerializedName("clickLog")
    val clickLog: StoreContributorClickLogResponse? = null,
)

data class StoreContributorHeaderResponse(
    @SerializedName("title")
    val title: StoreContributorTextResponse? = null,
    @SerializedName("subTitle")
    val subTitle: StoreContributorTextResponse? = null,
    @SerializedName("trailingAction")
    val trailingAction: StoreContributorButtonResponse? = null,
)

data class StoreContributorCardResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("cardId")
    val cardId: String? = null,
    @SerializedName("title")
    val title: StoreContributorTextResponse? = null,
    @SerializedName("description")
    val description: StoreContributorTextResponse? = null,
    @SerializedName("subTitles")
    val subTitles: List<StoreContributorTextResponse>? = emptyList(),
    @SerializedName("subTitleChip")
    val subTitleChip: StoreContributorChipResponse? = null,
    @SerializedName("image")
    val image: StoreContributorImageResponse? = null,
    @SerializedName("metadata")
    val metadata: StoreContributorTextResponse? = null,
    @SerializedName("style")
    val style: StoreContributorSurfaceStyleResponse? = null,
    @SerializedName("clickLog")
    val clickLog: StoreContributorClickLogResponse? = null,
)

data class StoreContributorTextResponse(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("isHtml")
    val isHtml: Boolean? = false,
    @SerializedName("fontColor")
    val fontColor: String? = null,
    @SerializedName("fontWeight")
    val fontWeight: String? = null,
)

data class StoreContributorImageResponse(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("style")
    val style: StoreContributorImageStyleResponse? = null,
)

data class StoreContributorImageStyleResponse(
    @SerializedName("width")
    val width: Double? = null,
    @SerializedName("height")
    val height: Double? = null,
)

data class StoreContributorButtonResponse(
    @SerializedName("text")
    val text: StoreContributorTextResponse? = null,
    @SerializedName("image")
    val image: StoreContributorImageResponse? = null,
    @SerializedName("imageAlignment")
    val imageAlignment: String? = null,
    @SerializedName("link")
    val link: StoreContributorLinkResponse? = null,
    @SerializedName("customAction")
    val customAction: StoreContributorCustomActionResponse? = null,
    @SerializedName("style")
    val style: StoreContributorSurfaceStyleResponse? = null,
    @SerializedName("clickLog")
    val clickLog: StoreContributorClickLogResponse? = null,
)

data class StoreContributorChipResponse(
    @SerializedName("image")
    val image: StoreContributorImageResponse? = null,
    @SerializedName("text")
    val text: StoreContributorTextResponse? = null,
    @SerializedName("additionalText")
    val additionalText: StoreContributorTextResponse? = null,
    @SerializedName("style")
    val style: StoreContributorSurfaceStyleResponse? = null,
    @SerializedName("imageAlignment")
    val imageAlignment: String? = null,
    @SerializedName("contentSpacing")
    val contentSpacing: Double? = null,
)

data class StoreContributorCustomActionResponse(
    @SerializedName("actionType")
    val actionType: String? = null,
    @SerializedName("extraParams")
    val extraParams: Map<String, JsonElement>? = null,
)

data class StoreContributorClickLogResponse(
    @SerializedName("eventType")
    val eventType: String? = null,
    @SerializedName("screenName")
    val screenName: String? = null,
    @SerializedName("objectType")
    val objectType: String? = null,
    @SerializedName("objectId")
    val objectId: String? = null,
    @SerializedName("extraParameters")
    val extraParameters: Map<String, JsonElement>? = null,
)

data class StoreContributorLinkResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("link")
    val link: String? = null,
)

data class StoreContributorSurfaceStyleResponse(
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    @SerializedName("border")
    val border: StoreContributorBorderResponse? = null,
)

data class StoreContributorBorderResponse(
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("width")
    val width: Double? = null,
)

data class StoreContributorCursorResponse(
    @SerializedName("nextCursor")
    val nextCursor: String? = null,
    @SerializedName("hasMore")
    val hasMore: Boolean? = false,
)
