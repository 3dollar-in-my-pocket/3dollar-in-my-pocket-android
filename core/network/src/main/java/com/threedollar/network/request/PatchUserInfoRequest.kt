package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class PatchUserInfoRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("representativeMedalId")
    val representativeMedalId: Int? = null
)