package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class PostStoreVisitRequest(
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("type")
    val type: String? = ""
)