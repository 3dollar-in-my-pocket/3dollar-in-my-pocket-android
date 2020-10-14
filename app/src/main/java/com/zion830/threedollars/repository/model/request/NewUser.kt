package com.zion830.threedollars.repository.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    @SerialName("name")
    val name: String,
    @SerialName("socialId")
    val socialId: String?,
    @SerialName("socialType")
    val socialType: String = "KAKAO"
)