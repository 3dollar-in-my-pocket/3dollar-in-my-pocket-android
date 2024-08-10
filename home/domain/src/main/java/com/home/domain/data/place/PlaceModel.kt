package com.home.domain.data.place


data class PlaceModel(
    val addressName: String = "",
    val createdAt: String = "",
    val location: Location = Location(),
    val placeId: String = "",
    val placeName: String = "",
    val roadAddressName: String = "",
    val updatedAt: String = "",
) {
    data class Location(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )
}