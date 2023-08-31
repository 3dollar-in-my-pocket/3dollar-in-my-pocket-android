package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.store.StoreResponse

data class NearStoreResponse(
    @SerializedName("data")
    val data: List<StoreResponse> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)