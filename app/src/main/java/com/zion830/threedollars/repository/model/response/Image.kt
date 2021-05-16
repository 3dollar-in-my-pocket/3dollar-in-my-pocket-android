package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("url")
    val url: String = ""
)