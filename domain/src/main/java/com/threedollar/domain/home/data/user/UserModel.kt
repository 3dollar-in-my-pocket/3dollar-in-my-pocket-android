package com.threedollar.domain.home.data.user

data class UserModel(
    val createdAt: String = "",
    val deviceModel: DeviceModel = DeviceModel(),
    val marketingConsent: String = "",
    val medalModel: MedalModel = MedalModel(),
    val name: String = "",
    val socialType: String = "",
    val updatedAt: String = "",
    val userId: Int = 0
)