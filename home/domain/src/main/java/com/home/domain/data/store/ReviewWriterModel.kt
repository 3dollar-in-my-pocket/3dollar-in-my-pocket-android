package com.home.domain.data.store


import com.home.domain.data.user.MedalModel

data class ReviewWriterModel(
    val medal: MedalModel = MedalModel(),
    val name: String = "",
    val socialType: String? = null,
    val userId: Int? = null
)