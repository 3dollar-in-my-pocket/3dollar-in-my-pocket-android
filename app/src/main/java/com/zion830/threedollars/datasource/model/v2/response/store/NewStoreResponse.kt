package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.store.StoreResponse

data class NewStoreResponse(
    @SerializedName("data")
    val data: StoreResponse = StoreResponse(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

