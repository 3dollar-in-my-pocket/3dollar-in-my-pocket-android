package com.zion830.threedollars.datasource.model.v2.response

import com.google.gson.annotations.SerializedName

interface AdAndStoreItem

data class PopupsResponse(
    @SerializedName("data")
    val data: List<Popups> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = "",
)

data class Popups(
    @SerializedName("advertisementId")
    val advertisementId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("subTitle")
    val subTitle: String? = "",
    @SerializedName("extraContent")
    val extraContent: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("imageWidth")
    val imageWidth: Int = 0,
    @SerializedName("imageHeight")
    val imageHeight: Int = 0,
    @SerializedName("linkUrl")
    val linkUrl: String? = "",
    @SerializedName("bgColor")
    val bgColor: String? = "",
    @SerializedName("fontColor")
    val fontColor: String? = "",
) : AdAndStoreItem