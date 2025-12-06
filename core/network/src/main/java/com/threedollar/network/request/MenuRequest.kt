package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class MenuRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String? = null
)