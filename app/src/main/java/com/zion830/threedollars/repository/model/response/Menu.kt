package com.zion830.threedollars.repository.model.response

import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: String = ""
)