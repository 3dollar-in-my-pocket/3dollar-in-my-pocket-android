package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("socialId")
    val socialId: String,
    @SerializedName("socialType")
    val socialType: String,
    @SerializedName("state")
    val state: Boolean
)