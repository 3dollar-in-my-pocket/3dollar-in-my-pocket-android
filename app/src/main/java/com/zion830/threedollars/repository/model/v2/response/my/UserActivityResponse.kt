package com.zion830.threedollars.repository.model.v2.response.my


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserActivityResponse(
    @Json(name = "data")
    val data: UserActivityData? = UserActivityData(),
    @Json(name = "message")
    val message: String? = "",
    @Json(name = "resultCode")
    val resultCode: String? = ""
)