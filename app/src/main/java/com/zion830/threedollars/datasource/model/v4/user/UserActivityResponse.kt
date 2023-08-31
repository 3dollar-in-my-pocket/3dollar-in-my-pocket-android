package com.zion830.threedollars.datasource.model.v4.user


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.medal.MedalResponse

data class UserActivityResponse(
    @SerializedName("activity")
    val activity: Activity = Activity(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("medal")
    val medal: MedalResponse = MedalResponse(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("userId")
    val userId: Int = 0,
)