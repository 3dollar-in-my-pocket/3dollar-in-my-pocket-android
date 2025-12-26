package com.threedollar.domain.home.data.user

data class MedalModel(
    val acquisitionModel: AcquisitionModel? = AcquisitionModel(),
    val createdAt: String? = "",
    val disableIconUrl: String? = "",
    val iconUrl: String? = "",
    val introduction: String? = "",
    val medalId: Int? = 0,
    val name: String? = "",
    val updatedAt: String? = ""
)