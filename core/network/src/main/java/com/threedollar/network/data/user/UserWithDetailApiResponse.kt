package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class UserWithDetailApiResponse(
    @SerializedName("userId")
    val userId: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("representativeMedal")
    val representativeMedal: Medal = Medal(),
    @SerializedName("ownedMedals")
    val ownedMedals: List<Medal> = listOf(),
    @SerializedName("settings")
    val settings: Settings = Settings(),
    @SerializedName("activities")
    val activities: Activities = Activities(),
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
)