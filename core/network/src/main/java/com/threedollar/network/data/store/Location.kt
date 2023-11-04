package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude")
    val latitude: Double? = 0.0,
    @SerializedName("longitude")
    val longitude: Double? = 0.0
)