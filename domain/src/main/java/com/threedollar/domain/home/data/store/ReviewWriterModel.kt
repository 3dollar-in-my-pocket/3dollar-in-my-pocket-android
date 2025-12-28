package com.threedollar.domain.home.data.store


import com.threedollar.domain.home.data.user.MedalModel

data class ReviewWriterModel(
    val medal: MedalModel = MedalModel(),
    val name: String = "",
    val socialType: String? = null,
    val userId: Int? = null
)