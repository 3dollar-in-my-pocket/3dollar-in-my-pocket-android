package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class StoreDisplayItemsRequest(
    @SerializedName("itemTypes")
    val itemTypes: List<String>,
)
