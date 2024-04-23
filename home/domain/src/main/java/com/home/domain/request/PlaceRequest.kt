package com.home.domain.request

data class PlaceRequest(
    val location: Location,
    val placeName: String,
    val addressName: String = "",
    val roadAddressName: String = "",
) {
    data class Location(
        val latitude: Double,
        val longitude: Double,
    )
}