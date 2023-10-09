package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Favorite(
    @SerializedName("isFavorite")
    val isFavorite: Boolean? = false,
    @SerializedName("totalSubscribersCount")
    val totalSubscribersCount: Int? = 0
)