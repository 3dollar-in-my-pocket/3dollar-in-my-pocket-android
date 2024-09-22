package com.threedollar.network.data.visit_history


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.threedollar.network.data.store.Cursor

@JsonClass(generateAdapter = true)
data class MyVisitHistoryResponseV2(
    @Json(name = "contents")
    val contents: List<MyVisitHistoryV2>? = emptyList(),
    @SerializedName("cursor")
    val cursor: Cursor? = Cursor()
)