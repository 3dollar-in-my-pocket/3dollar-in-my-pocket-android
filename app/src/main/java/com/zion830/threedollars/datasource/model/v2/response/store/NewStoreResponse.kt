package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.store.StoreInfo

data class NewStoreResponse(
    @SerializedName("data")
    val data: StoreInfo = StoreInfo(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

