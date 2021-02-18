package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class AddImageResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("url")
    val url: String
)