package com.zion830.threedollars.repository.model.v2.response

import com.google.gson.annotations.SerializedName


data class PopupsResponse(
    @SerializedName("data")
    val data: List<Popups> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class Popups(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("linkUrl")
    val linkUrl: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = ""
)