package com.zion830.threedollars.datasource.model.v2.response.my


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zion830.threedollars.datasource.model.LoginType

data class User(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("userId")
    val userId: Int = 0,
    @SerializedName("medal")
    val medal: Medal? = Medal(),
    @Json(name = "marketingConsent")
    val marketingConsent: String? = "",
    @Json(name = "device")
    val device: Device? = Device()
) {
    @JsonClass(generateAdapter = true)
    data class Device(
        val isSetupNotification: Boolean = true
    )

    fun isKakaoUser() = socialType == LoginType.KAKAO.socialName
}