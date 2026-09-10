package com.threedollar.network.data.screen

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName

data class HomeListSectionResponse(
    @SerializedName("cards")
    val cards: List<HomeListCardResponse>? = emptyList(),
    @SerializedName("cursor")
    val cursor: SDCursorResponse? = null,
    @SerializedName("focusBounds")
    val focusBounds: SDLocationBoundsResponse? = null,
)

data class SDLocationBoundsResponse(
    @SerializedName("southWest")
    val southWest: SDLocationResponse? = null,
    @SerializedName("northEast")
    val northEast: SDLocationResponse? = null,
)

data class HomeListCardResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("cardId")
    val cardId: String? = null,
    @SerializedName("header")
    val header: HomeListCardHeaderResponse? = null,
    @SerializedName("metadata")
    val metadata: HomeListCardMetadataResponse? = null,
    @SerializedName("images")
    val images: List<SDImageResponse>? = emptyList(),
    @SerializedName("bodies")
    val bodies: List<SDTextResponse>? = emptyList(),
    @SerializedName("marker")
    val marker: HomeListMarkerResponse? = null,
    @SerializedName("link")
    val link: SDLinkResponse? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog")
    val clickLog: SDClickLogResponse? = null,
    @SerializedName("impressionLog")
    val impressionLog: SDImpressionLogResponse? = null,
    @SerializedName("refs")
    val refs: List<HomeListStoreReferenceResponse>? = emptyList(),
)

data class HomeListStoreReferenceResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("storeId")
    val storeId: String? = null,
    @SerializedName("storeType")
    val storeType: String? = null,
)

data class HomeListCardHeaderResponse(
    @SerializedName("title")
    val title: SDTextResponse? = null,
    @SerializedName("badge")
    val badge: SDImageResponse? = null,
)

data class HomeListCardMetadataResponse(
    @SerializedName("primary")
    val primary: List<SDChipResponse>? = emptyList(),
    @SerializedName("secondary")
    val secondary: List<SDChipResponse>? = emptyList(),
    @SerializedName("separator")
    val separator: SDImageResponse? = null,
)

data class HomeListMarkerResponse(
    @SerializedName("focused")
    val focused: SDChipResponse? = null,
    @SerializedName("unfocused")
    val unfocused: SDChipResponse? = null,
    @SerializedName("location")
    val location: SDLocationResponse? = null,
    @SerializedName("link")
    val link: SDLinkResponse? = null,
    @SerializedName("clickLog")
    val clickLog: SDClickLogResponse? = null,
)

data class StoreScreenResponse(
    @SerializedName("sections")
    val sections: List<StoreSectionResponse>? = emptyList(),
    @SerializedName("viewLog")
    val viewLog: SDPageViewLogResponse? = null,
)

data class StoreSectionResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("header")
    val header: HomeListCardHeaderResponse? = null,
    @SerializedName("metadata")
    val metadata: HomeListCardMetadataResponse? = null,
    @SerializedName("additionalInfos")
    val additionalInfos: StoreSectionAdditionalInfosResponse? = null,
    @SerializedName("topActionBars")
    val topActionBars: List<StoreActionBarResponse>? = emptyList(),
    @SerializedName("actionBars")
    val actionBars: List<StoreActionBarResponse>? = emptyList(),
    @SerializedName("images")
    val images: List<SDImageResponse>? = emptyList(),
    @SerializedName("bodies")
    val bodies: List<SDTextResponse>? = emptyList(),
    @SerializedName("style")
    val style: SDSurfaceStyleResponse? = null,
)

data class StoreSectionAdditionalInfosResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("isSubscriber")
    val isSubscriber: Boolean? = false,
    @SerializedName("storeId")
    val storeId: String? = null,
    @SerializedName("storeType")
    val storeType: String? = null,
)

data class StoreActionBarResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("button")
    val button: SDButtonResponse? = null,
    @SerializedName("clickLog")
    val clickLog: SDClickLogResponse? = null,
)

data class SDTextResponse(
    @SerializedName(value = "text", alternate = ["content"])
    private val rawText: JsonElement? = null,
    @SerializedName("isHtml")
    private val rawIsHtml: Boolean? = false,
    @SerializedName("fontColor")
    private val rawFontColor: String? = null,
    @SerializedName("fontWeight")
    private val rawFontWeight: String? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleResponse? = null,
) {
    val text: String?
        get() = rawText.textContent()

    val isHtml: Boolean?
        get() = rawText.booleanValue("isHtml") ?: rawIsHtml

    val fontColor: String?
        get() = rawText.stringValue("fontColor") ?: rawFontColor

    val fontWeight: String?
        get() = rawText.stringValue("fontWeight") ?: rawFontWeight

    companion object {
        fun fromText(
            text: String?,
            isHtml: Boolean? = false,
            fontColor: String? = null,
            fontWeight: String? = null,
        ): SDTextResponse = SDTextResponse(
            rawText = text?.let(::JsonPrimitive),
            rawIsHtml = isHtml,
            rawFontColor = fontColor,
            rawFontWeight = fontWeight,
        )
    }
}

private fun JsonElement?.textContent(): String? {
    if (this == null || isJsonNull) return null
    if (isJsonPrimitive) return asJsonPrimitive.asString
    if (!isJsonObject) return null
    val jsonObject = asJsonObject
    return jsonObject.get("text").textContent()
        ?: jsonObject.get("content").textContent()
}

private fun JsonElement?.booleanValue(key: String): Boolean? {
    if (this == null || !isJsonObject) return null
    val value = asJsonObject.get(key) ?: return null
    return if (value.isJsonPrimitive && value.asJsonPrimitive.isBoolean) value.asBoolean else null
}

private fun JsonElement?.stringValue(key: String): String? {
    if (this == null || !isJsonObject) return null
    val value = asJsonObject.get(key) ?: return null
    return if (value.isJsonPrimitive) value.asString else null
}

data class SDImageResponse(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("style")
    val style: SDImageStyleResponse? = null,
)

data class SDImageStyleResponse(
    @SerializedName("width")
    val width: Double? = null,
    @SerializedName("height")
    val height: Double? = null,
)

data class SDChipResponse(
    @SerializedName("image")
    val image: SDImageResponse? = null,
    @SerializedName("text")
    val text: SDTextResponse? = null,
    @SerializedName("additionalText")
    val additionalText: SDTextResponse? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleResponse? = null,
    @SerializedName("imageAlignment")
    val imageAlignment: String? = null,
    @SerializedName("contentSpacing")
    val contentSpacing: Double? = null,
)

data class SDButtonResponse(
    @SerializedName("text")
    val text: SDTextResponse? = null,
    @SerializedName("image")
    val image: SDImageResponse? = null,
    @SerializedName("imageAlignment")
    val imageAlignment: String? = null,
    @SerializedName("link")
    val link: SDLinkResponse? = null,
    @SerializedName("customAction")
    val customAction: SDCustomActionResponse? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleResponse? = null,
    @SerializedName("clickLog")
    val clickLog: SDClickLogResponse? = null,
)

data class SDLinkResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName(value = "link", alternate = ["url"])
    val link: String? = null,
)

data class SDCustomActionResponse(
    @SerializedName("actionType")
    val actionType: String? = null,
    @SerializedName("extraParams")
    val extraParams: Map<String, JsonElement>? = null,
)

data class SDLocationResponse(
    @SerializedName(value = "latitude", alternate = ["lat"])
    val latitude: Double? = null,
    @SerializedName(value = "longitude", alternate = ["lng"])
    val longitude: Double? = null,
)

data class SDSurfaceStyleResponse(
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    @SerializedName("border")
    val border: SDBorderResponse? = null,
)

data class SDBorderResponse(
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("width")
    val width: Double? = null,
)

data class SDCursorResponse(
    @SerializedName("nextCursor")
    val nextCursor: String? = null,
    @SerializedName("hasMore")
    val hasMore: Boolean? = false,
)

data class SDClickLogResponse(
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

data class SDImpressionLogResponse(
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

data class SDPageViewLogResponse(
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
