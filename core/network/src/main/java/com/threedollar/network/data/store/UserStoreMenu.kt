package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class UserStoreMenu(
    @SerializedName("category")
    val category: Category = Category(),
    @SerializedName("menuId")
    val menuId: Int = 0,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("price")
    val price: String? = ""
)