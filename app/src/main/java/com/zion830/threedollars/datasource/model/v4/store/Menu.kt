package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("menuId")
    val menuId: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)