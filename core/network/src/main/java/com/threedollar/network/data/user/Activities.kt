package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class Activities(
    @SerializedName("createStoreCount")
    val createStoreCount: Int = 0,
    @SerializedName("writeReviewCount")
    val writeReviewCount: Int = 0,
    @SerializedName("visitStoreCount")
    val visitStoreCount: Int = 0,
    @SerializedName("favoriteStoreCount")
    val favoriteStoreCount: Int = 0
)
