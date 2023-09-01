package com.zion830.threedollars.datasource.model.v4.visit


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.medal.MedalResponse

data class Visitor(
    @SerializedName("medal")
    val medal: MedalResponse = MedalResponse(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("userId")
    val userId: Int = 0,
)