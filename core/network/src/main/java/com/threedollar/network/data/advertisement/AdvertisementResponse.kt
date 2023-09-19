package com.threedollar.network.data.advertisement

import com.google.gson.annotations.SerializedName

data class AdvertisementResponse(
    @SerializedName("advertisementId")
    val advertisementId: Int = 0,
    @SerializedName("bgColor")
    val bgColor: String? = "",
    @SerializedName("extraContent")
    val extraContent: String? = "",
    @SerializedName("fontColor")
    val fontColor: String? = "",
    @SerializedName("imageHeight")
    val imageHeight: Int = 0,
    @SerializedName("imageUrl")
    val imageUrl: String? = "",
    @SerializedName("imageWidth")
    val imageWidth: Int = 0,
    @SerializedName("linkUrl")
    val linkUrl: String? = "",
    @SerializedName("subTitle")
    val subTitle: String? = "",
    @SerializedName("title")
    val title: String? = ""
)