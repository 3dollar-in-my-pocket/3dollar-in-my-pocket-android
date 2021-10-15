package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class CategoryInfo(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("isNew")
    val isNew: Boolean = false
)