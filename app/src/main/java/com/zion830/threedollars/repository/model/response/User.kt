package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialId")
    val socialId: String = "",
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("state")
    val state: Boolean? = null,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("withdrawal")
    val withdrawal: Boolean? = null
)