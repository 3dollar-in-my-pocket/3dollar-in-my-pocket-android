package com.zion830.threedollars.repository.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("socialId")
    val socialId: String,
    @SerialName("socialType")
    val socialType: String,
    @SerialName("state")
    val state: Boolean
)