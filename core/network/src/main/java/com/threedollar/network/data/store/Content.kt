package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("store")
    val store: Store? = Store(),
    @SerializedName("marker")
    val marker: Marker? = null,
    @SerializedName("openStatus")
    val openStatus: OpenStatus = OpenStatus(),
    @SerializedName("distanceM")
    val distanceM: Int? = 0,
    @SerializedName("extra")
    val extra: Extra? = Extra()
)