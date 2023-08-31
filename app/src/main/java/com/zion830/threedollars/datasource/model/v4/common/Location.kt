package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude")
    val latitude: Int = 0,
    @SerializedName("longitude")
    val longitude: Int = 0,
)