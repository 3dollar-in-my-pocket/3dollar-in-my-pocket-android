package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: String
)