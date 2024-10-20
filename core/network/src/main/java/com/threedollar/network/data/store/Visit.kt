package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Visit(
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("visitDate")
    val visitDate: String? = "",
    @SerializedName("visitId")
    val visitId: String? = "",
) {
    fun isExist() = type == "EXISTS"
}