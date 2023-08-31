package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("distanceM")
    val distanceM: Int = 0,
    @SerializedName("extra")
    val extra: Extra = Extra(),
    @SerializedName("store")
    val store: Store = Store(),
)