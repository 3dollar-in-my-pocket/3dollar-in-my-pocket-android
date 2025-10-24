package com.threedollar.domain.home.data.store

import java.io.Serializable

data class ClassificationModel(
    val description: String = "",
    val type: String = ""
) : Serializable