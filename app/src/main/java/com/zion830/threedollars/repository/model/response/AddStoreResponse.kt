package com.zion830.threedollars.repository.model.response

import com.google.gson.annotations.SerializedName


data class AddStoreResponse(
    @SerializedName("storeId")
    val storeId: Int? = 0
)