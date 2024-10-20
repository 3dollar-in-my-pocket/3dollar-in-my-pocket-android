package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.store.StoreInfo

data class NearStoreResponse(
    @SerializedName("data")
    val data: List<StoreInfo> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)