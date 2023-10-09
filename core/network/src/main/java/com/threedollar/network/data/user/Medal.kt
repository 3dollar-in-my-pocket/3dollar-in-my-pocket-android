package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class Medal(
    @SerializedName("acquisition")
    val acquisition: Acquisition? = Acquisition(),
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("disableIconUrl")
    val disableIconUrl: String? = "",
    @SerializedName("iconUrl")
    val iconUrl: String? = "",
    @SerializedName("introduction")
    val introduction: String? = "",
    @SerializedName("medalId")
    val medalId: Int? = 0,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = ""
)