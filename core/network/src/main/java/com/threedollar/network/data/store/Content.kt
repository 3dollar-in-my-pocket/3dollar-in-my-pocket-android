package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("distanceM")
    val distanceM: Int? = 0,
    @SerializedName("extra")
    val extra: Extra? = Extra(),
    @SerializedName("store")
    val store: Store? = Store()
)