package com.zion830.threedollars.datasource.model.v4.image


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class ImageResponse(
    @SerializedName("contents")
    val imageModels: List<ImageModel> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor()
)