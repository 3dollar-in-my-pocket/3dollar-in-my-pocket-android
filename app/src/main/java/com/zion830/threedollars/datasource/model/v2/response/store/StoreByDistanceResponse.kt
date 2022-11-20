package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class StoreByDistanceResponse(
    @SerializedName("data")
    val data: StoreByDistance = StoreByDistance(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)