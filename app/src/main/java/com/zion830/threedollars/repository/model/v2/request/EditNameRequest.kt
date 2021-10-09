package com.zion830.threedollars.repository.model.v2.request

import com.google.gson.annotations.SerializedName

data class EditNameRequest(
    @SerializedName("name")
    val name: Int = 0
)