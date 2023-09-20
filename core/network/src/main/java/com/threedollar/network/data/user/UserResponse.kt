package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("device")
    val device: Device = Device(),
    @SerializedName("marketingConsent")
    val marketingConsent: String = "",
    @SerializedName("medal")
    val medal: Medal = Medal(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("userId")
    val userId: Int = 0
)