package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class SaveImagesResponse(
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("imageId")
    val imageId: Int? = 0,
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("url")
    val url: String? = ""
)