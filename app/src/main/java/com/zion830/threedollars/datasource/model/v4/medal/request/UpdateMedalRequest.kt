package com.zion830.threedollars.datasource.model.v4.medal.request

import com.google.gson.annotations.SerializedName

data class UpdateMedalRequest(
    @SerializedName("medalId")
    val medalId: Int
)