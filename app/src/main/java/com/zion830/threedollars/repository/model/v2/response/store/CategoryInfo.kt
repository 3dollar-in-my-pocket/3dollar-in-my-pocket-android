package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryInfo(
    @SerializedName("category")
    val category: String = "BUNGEOPPANG",
    @SerializedName("name")
    val name: String = "붕어빵",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("isNew")
    val isNew: Boolean = false
): Serializable