package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("imageUrl")
    val imageUrl: String? = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: Int = 0
)