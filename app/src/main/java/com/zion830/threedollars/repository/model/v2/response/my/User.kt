package com.zion830.threedollars.repository.model.v2.response.my


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.LoginType

data class User(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("userId")
    val userId: Int = 0
) {
    fun isKakaoUser() = socialType == LoginType.KAKAO.socialName
}