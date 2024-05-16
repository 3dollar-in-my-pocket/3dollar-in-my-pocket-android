package com.threedollar.network.data.place


import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("contents")
    val contents: List<Content>,
    @SerializedName("cursor")
    val cursor: Cursor
)