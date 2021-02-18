package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("state")
    val state: Boolean,
    @SerializedName("token")
    val token: String,
    @SerializedName("userId")
    val userId: Int
)