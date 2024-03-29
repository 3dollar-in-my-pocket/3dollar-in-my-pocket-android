package com.threedollar.network.data.kakao


import com.google.gson.annotations.SerializedName

data class SameName(
    @SerializedName("region")
    val region: List<Any> = listOf(),
    @SerializedName("keyword")
    val keyword: String = "",
    @SerializedName("selected_region")
    val selectedRegion: String = ""
)