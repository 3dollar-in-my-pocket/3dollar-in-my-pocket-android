package com.zion830.threedollars.datasource.model.v4.visit.request


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VisitRequest(
    @Json(name = "storeId")
    val storeId: Int? = 0,
    @Json(name = "type")
    val type: String? = ""
)