package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class MenuRequest(
    @SerializedName("category")
    val category: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: String
)