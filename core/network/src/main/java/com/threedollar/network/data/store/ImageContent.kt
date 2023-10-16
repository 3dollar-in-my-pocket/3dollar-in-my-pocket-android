package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class ImageContent(
    @SerializedName("image")
    val image: Image? = Image()
)