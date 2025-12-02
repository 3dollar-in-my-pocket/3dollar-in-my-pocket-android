package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class PlaceRequest(
    @SerializedName("location")
    val location: Location,
    @SerializedName("placeName")
    val placeName: String,
    @SerializedName("addressName")
    val addressName: String = "",
    @SerializedName("roadAddressName")
    val roadAddressName: String = "",
) {
    data class Location(
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
    )
}