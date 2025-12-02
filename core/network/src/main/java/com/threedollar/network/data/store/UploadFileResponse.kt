package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class UploadFileResponse(
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("width") val width: Int = 0,
    @SerializedName("height") val height: Int = 0,
    @SerializedName("ratio") val ratio: Double = 0.0
)