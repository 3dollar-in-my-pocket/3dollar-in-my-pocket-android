package com.threedollar.network.data.visit_history


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyVisitHistory(
    @Json(name = "contents")
    val contents: List<VisitHistoryContent>? = listOf(),
    @Json(name = "nextCursor")
    val nextCursor: Int? = 0
)