package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Marker(
    @SerializedName("selected")
    val selected: StoreMarkerImageResponse = StoreMarkerImageResponse(),
    @SerializedName("unselected")
    val unSelected: StoreMarkerImageResponse = StoreMarkerImageResponse()
)