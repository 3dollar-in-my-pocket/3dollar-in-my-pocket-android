package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class AddStoreResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)