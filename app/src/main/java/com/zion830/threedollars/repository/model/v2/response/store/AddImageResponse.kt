package com.zion830.threedollars.repository.model.v2.response


import com.google.gson.annotations.SerializedName

data class AddImageResponse(
    @SerializedName("data")
    val data: List<ImageInfo> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class ImageInfo(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("imageId")
    val imageId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("url")
    val url: String = ""
)