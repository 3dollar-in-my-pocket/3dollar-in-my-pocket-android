package com.zion830.threedollars.datasource.model.v4.user.request

import com.google.gson.annotations.SerializedName

data class ConnectAccountRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("socialType")
    val socialType: String,
)