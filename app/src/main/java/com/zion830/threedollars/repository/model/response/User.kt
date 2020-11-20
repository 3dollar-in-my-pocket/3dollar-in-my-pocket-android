package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialId")
    val socialId: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("state")
    val state: Boolean = false
)