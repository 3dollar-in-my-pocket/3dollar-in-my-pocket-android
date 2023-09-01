package com.zion830.threedollars.datasource.model.v4.image


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("imageId")
    val imageId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("url")
    val url: String = ""
)