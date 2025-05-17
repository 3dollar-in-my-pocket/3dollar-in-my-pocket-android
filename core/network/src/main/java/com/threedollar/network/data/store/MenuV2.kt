package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class MenuV2(
    @SerializedName("name") val name: String = "",
    @SerializedName("price") val price: Int = 0,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("description") val description: String = "",
    @SerializedName("category") val category: Category = Category()
)