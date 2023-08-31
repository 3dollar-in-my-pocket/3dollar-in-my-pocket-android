package com.zion830.threedollars.datasource.model.v4.user


import com.google.gson.annotations.SerializedName

data class Activity(
    @SerializedName("favoriteStoresCount")
    val favoriteStoresCount: Int = 0,
    @SerializedName("medalsCounts")
    val medalsCounts: Int = 0,
    @SerializedName("reviewsCount")
    val reviewsCount: Int = 0,
    @SerializedName("storeVisitsCount")
    val storeVisitsCount: Int = 0,
    @SerializedName("storesCount")
    val storesCount: Int = 0,
)