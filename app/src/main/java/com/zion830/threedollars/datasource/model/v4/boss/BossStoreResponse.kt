package com.zion830.threedollars.datasource.model.v4.boss


import com.google.gson.annotations.SerializedName

data class BossStoreResponse(
    @SerializedName("distanceM")
    val distanceM: Int = 0,
    @SerializedName("favorite")
    val favorite: Favorite = Favorite(),
    @SerializedName("feedbacks")
    val feedbacks: List<Feedback> = listOf(),
    @SerializedName("openStatus")
    val openStatus: OpenStatus = OpenStatus(),
    @SerializedName("store")
    val store: Store = Store(),
)