package com.threedollar.network.data.place


import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("addressName")
    val addressName: String? = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("placeId")
    val placeId: String = "",
    @SerializedName("placeName")
    val placeName: String = "",
    @SerializedName("roadAddressName")
    val roadAddressName: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)