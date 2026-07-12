package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class StoreDisplayItemsResponse(
    @SerializedName("contents")
    val contents: List<StoreDisplayItemResponse>? = listOf(),
)

data class StoreDisplayItemResponse(
    @SerializedName("itemType")
    val itemType: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("isVisible")
    val isVisible: Boolean? = false,
    @SerializedName("trigger")
    val trigger: StoreDisplayTriggerResponse? = null,
)

data class StoreDisplayTriggerResponse(
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("displayAfterSeconds")
    val displayAfterSeconds: Double? = 0.0,
    @SerializedName("displayDurationSeconds")
    val displayDurationSeconds: Double? = null,
    @SerializedName("conditions")
    val conditions: StoreDisplayTriggerConditionsResponse? = null,
)

data class StoreDisplayTriggerConditionsResponse(
    @SerializedName("sessionViewCountRange")
    val sessionViewCountRange: SessionViewCountRangeResponse? = null,
)

data class SessionViewCountRangeResponse(
    @SerializedName("min")
    val min: Int? = null,
    @SerializedName("max")
    val max: Int? = null,
)
