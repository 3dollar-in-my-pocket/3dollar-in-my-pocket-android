package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("url")
    val url: String?
)