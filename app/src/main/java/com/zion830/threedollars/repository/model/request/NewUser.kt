package com.zion830.threedollars.repository.model.request


import com.google.gson.annotations.SerializedName


data class NewUser(
    @SerializedName("name")
    val name: String,
    @SerializedName("socialId")
    val socialId: String,
    @SerializedName("socialType")
    val socialType: String = "KAKAO"
)