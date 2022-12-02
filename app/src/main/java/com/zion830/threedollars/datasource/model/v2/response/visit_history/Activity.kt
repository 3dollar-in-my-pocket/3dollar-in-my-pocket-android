package com.zion830.threedollars.datasource.model.v2.response.visit_history


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Activity(
    @Json(name = "medalsCounts")
    val medalsCounts: Int? = 0,
    @Json(name = "reviewsCount")
    val reviewsCount: Int? = 0,
    @Json(name = "storesCount")
    val storesCount: Int? = 0
)