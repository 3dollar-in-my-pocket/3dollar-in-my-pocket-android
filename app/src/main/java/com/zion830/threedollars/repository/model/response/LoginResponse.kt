package com.zion830.threedollars.repository.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("state")
    val state: Boolean,
    @SerialName("token")
    val token: String,
    @SerialName("userId")
    val userId: Int
)