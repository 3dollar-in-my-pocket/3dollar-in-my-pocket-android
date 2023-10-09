package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.user.Medal

data class ReviewWriter(
    @SerializedName("medal")
    val medal: Medal? = Medal(),
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("socialType")
    val socialType: String? = null,
    @SerializedName("userId")
    val userId: Int? = null
)