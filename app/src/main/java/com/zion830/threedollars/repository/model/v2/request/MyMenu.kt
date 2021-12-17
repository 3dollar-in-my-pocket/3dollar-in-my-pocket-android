package com.zion830.threedollars.repository.model.v2.request


import com.google.gson.annotations.SerializedName

data class MyMenu(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: String = ""
)