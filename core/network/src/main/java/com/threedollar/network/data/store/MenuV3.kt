package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class MenuV3(
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("category") val category: Category = Category()
)
