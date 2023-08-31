package com.zion830.threedollars.datasource.model.v4.nearExists

import com.google.gson.annotations.SerializedName

data class NearExistResponse(
    @SerializedName("isExists")
    val isExists: Boolean,
)