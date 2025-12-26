package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyReportedStoresResponse(
    @Json(name = "contents")
    val contents: List<MyReportedContent>? = emptyList(),
    @SerializedName("cursor")
    val cursor: Cursor? = Cursor()
)