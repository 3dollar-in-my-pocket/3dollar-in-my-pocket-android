package com.zion830.threedollars.datasource.model.v2.request

import com.google.gson.annotations.SerializedName

data class UpdateMedalRequest(
    @SerializedName("medalId")
    val medalId: Int
)