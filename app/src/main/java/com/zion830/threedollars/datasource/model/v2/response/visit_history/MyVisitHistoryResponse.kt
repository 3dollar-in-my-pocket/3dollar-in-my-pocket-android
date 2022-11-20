package com.zion830.threedollars.datasource.model.v2.response.visit_history


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyVisitHistoryResponse(
    @Json(name = "data")
    val data: MyVisitHistory? = MyVisitHistory(),
    @Json(name = "message")
    val message: String? = "",
    @Json(name = "resultCode")
    val resultCode: String? = ""
)