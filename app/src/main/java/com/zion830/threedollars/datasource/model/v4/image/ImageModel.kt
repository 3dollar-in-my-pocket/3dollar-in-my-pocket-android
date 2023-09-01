package com.zion830.threedollars.datasource.model.v4.image


import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("image")
    val image: Image = Image()
)