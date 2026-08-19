package com.threedollar.network.data.screen

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class HomeFilterScreenResponse(
    @SerializedName("sections")
    val sections: List<HomeFilterSectionResponse>? = emptyList(),
    @SerializedName("configuration")
    val configuration: HomeFilterConfigurationResponse? = null,
    @SerializedName("viewLog")
    val viewLog: HomeFilterViewLogResponse? = null,
)

data class HomeFilterConfigurationResponse(
    @SerializedName("initialMapZoomLevel")
    val initialMapZoomLevel: Double? = null,
)

data class HomeFilterViewLogResponse(
    @SerializedName("screenName")
    val screenName: String? = null,
)

data class HomeFilterSectionResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("bars")
    val bars: List<HomeFilterBarResponse>? = emptyList(),
)

data class HomeFilterBarResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("categoriesFilter")
    val categoriesFilter: HomeFilterChipResponse? = null,
    @SerializedName("categoriesFilterClickLog")
    val categoriesFilterClickLog: HomeFilterClickLogResponse? = null,
    @SerializedName("currentCategoryFilter")
    val currentCategoryFilter: HomeFilterCurrentCategoryResponse? = null,
    @SerializedName("paramKey")
    val paramKey: String? = null,
    @SerializedName("options")
    val options: List<HomeFilterRadioOptionResponse>? = emptyList(),
    @SerializedName("button")
    val button: HomeFilterButtonResponse? = null,
    @SerializedName("clickLog")
    val clickLog: HomeFilterClickLogResponse? = null,
)

data class HomeFilterChipResponse(
    @SerializedName("image")
    val image: HomeFilterImageResponse? = null,
    @SerializedName("text")
    val text: HomeFilterTextResponse? = null,
    @SerializedName("additionalText")
    val additionalText: HomeFilterTextResponse? = null,
    @SerializedName("style")
    val style: HomeFilterChipStyleResponse? = null,
)

data class HomeFilterChipStyleResponse(
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    @SerializedName("border")
    val border: HomeFilterBorderResponse? = null,
)

data class HomeFilterButtonResponse(
    @SerializedName("text")
    val text: HomeFilterTextResponse? = null,
    @SerializedName("image")
    val image: HomeFilterImageResponse? = null,
    @SerializedName("link")
    val link: HomeFilterLinkResponse? = null,
    @SerializedName("style")
    val style: HomeFilterButtonStyleResponse? = null,
)

data class HomeFilterButtonStyleResponse(
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    @SerializedName("border")
    val border: HomeFilterBorderResponse? = null,
)

data class HomeFilterCurrentCategoryResponse(
    @SerializedName("fontColor")
    val fontColor: String? = null,
    @SerializedName("style")
    val style: HomeFilterSurfaceStyleResponse? = null,
    @SerializedName("clickLog")
    val clickLog: HomeFilterClickLogResponse? = null,
)

data class HomeFilterRadioOptionResponse(
    @SerializedName("chip")
    val chip: HomeFilterChipResponse? = null,
    @SerializedName("paramValue")
    val paramValue: String? = null,
    @SerializedName("clickLog")
    val clickLog: HomeFilterClickLogResponse? = null,
)

data class HomeFilterTextResponse(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("isHtml")
    val isHtml: Boolean? = false,
    @SerializedName("fontColor")
    val fontColor: String? = null,
)

data class HomeFilterImageResponse(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("style")
    val style: HomeFilterImageStyleResponse? = null,
)

data class HomeFilterImageStyleResponse(
    @SerializedName("width")
    val width: Double? = null,
    @SerializedName("height")
    val height: Double? = null,
)

data class HomeFilterLinkResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("link")
    val link: String? = null,
)

data class HomeFilterSurfaceStyleResponse(
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    @SerializedName("border")
    val border: HomeFilterBorderResponse? = null,
)

data class HomeFilterBorderResponse(
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("width")
    val width: Double? = null,
)

data class HomeFilterClickLogResponse(
    @SerializedName("screenName")
    val screenName: String? = null,
    @SerializedName("objectType")
    val objectType: String? = null,
    @SerializedName("objectId")
    val objectId: String? = null,
    @SerializedName("extraParameters")
    val extraParameters: Map<String, JsonElement>? = null,
)
