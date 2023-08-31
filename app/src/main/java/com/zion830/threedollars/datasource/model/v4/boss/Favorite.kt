package com.zion830.threedollars.datasource.model.v4.boss


import com.google.gson.annotations.SerializedName

data class Favorite(
    @SerializedName("isFavorite")
    val isFavorite: Boolean = false,
    @SerializedName("totalSubscribersCount")
    val totalSubscribersCount: Int = 0,
)