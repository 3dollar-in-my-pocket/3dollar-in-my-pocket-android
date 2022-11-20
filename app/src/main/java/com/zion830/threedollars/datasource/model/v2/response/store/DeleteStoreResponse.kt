package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class DeleteStoreResponse(
    @SerializedName("data")
    val data: DeleteResult = DeleteResult(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class DeleteResult(
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false
)