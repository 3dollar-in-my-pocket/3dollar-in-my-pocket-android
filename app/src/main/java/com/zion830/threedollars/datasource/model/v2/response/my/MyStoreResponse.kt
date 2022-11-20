package com.zion830.threedollars.datasource.model.v2.response.my


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo

data class MyStoreResponse(
    @SerializedName("data")
    val data: MyStore = MyStore(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class MyStore(
    @SerializedName("contents")
    val contents: List<StoreInfo> = listOf(),
    @SerializedName("nextCursor")
    val nextCursor: Int = 0,
    @SerializedName("totalElements")
    val totalElements: Int = 0
)