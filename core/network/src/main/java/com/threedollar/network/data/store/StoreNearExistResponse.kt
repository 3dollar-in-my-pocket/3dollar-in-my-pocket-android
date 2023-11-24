package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class StoreNearExistResponse(
    @SerializedName("isExists")
    val isExists: Boolean
)