package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class FaqTag(
    @SerializedName("displayOrder")
    val displayOrder: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)