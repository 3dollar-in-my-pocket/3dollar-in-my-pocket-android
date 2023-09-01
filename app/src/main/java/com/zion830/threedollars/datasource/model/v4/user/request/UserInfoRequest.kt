package com.zion830.threedollars.datasource.model.v4.user.request

import com.google.gson.annotations.SerializedName

data class UserInfoRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("marketingConsent")
    val marketingConsent: String,
)