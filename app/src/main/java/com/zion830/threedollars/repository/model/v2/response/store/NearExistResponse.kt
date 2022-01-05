package com.zion830.threedollars.repository.model.v2.response.store

import com.google.gson.annotations.SerializedName

data class NearExistResponse(
    @SerializedName("data")
    val nearExist: NearExist,
    @SerializedName("message")
    val message: String,
    @SerializedName("resultCode")
    val resultCode: String
)

data class NearExist(
    @SerializedName("isExists")
    val isExists: Boolean
)