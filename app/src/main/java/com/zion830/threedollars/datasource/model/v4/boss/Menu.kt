package com.zion830.threedollars.datasource.model.v4.boss


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: Int = 0,
)