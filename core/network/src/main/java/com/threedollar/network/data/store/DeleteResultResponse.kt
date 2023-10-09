package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class DeleteResultResponse(
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false
)