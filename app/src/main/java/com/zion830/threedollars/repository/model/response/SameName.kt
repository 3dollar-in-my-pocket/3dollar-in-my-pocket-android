package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SameName(
    @SerializedName("keyword")
    val keyword: String = "",
    @SerializedName("region")
    val region: List<Any> = listOf(),
    @SerializedName("selected_region")
    val selectedRegion: String = ""
)