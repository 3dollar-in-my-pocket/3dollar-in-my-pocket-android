package com.zion830.threedollars.repository.model.v2.request


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewVisitHistory(
    @Json(name = "storeId")
    val storeId: Int? = 0,
    @Json(name = "type")
    val type: String? = ""
)