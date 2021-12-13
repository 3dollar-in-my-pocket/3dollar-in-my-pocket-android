package com.zion830.threedollars.repository.model.v2.response.my


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zion830.threedollars.repository.model.v2.response.visit_history.Activity

@JsonClass(generateAdapter = true)
data class UserActivityData(
    @Json(name = "activity")
    val activity: Activity? = Activity(),
    @Json(name = "medal")
    val medal: Medal? = Medal(),
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "socialType")
    val socialType: String? = "",
    @Json(name = "userId")
    val userId: Int? = 0
)

@JsonClass(generateAdapter = true)
data class Medal(
    val iconUrl: String? = "",
    val medalId: Int? = 0,
    val name: String? = "",
)