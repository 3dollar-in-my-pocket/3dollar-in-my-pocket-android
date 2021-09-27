package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("totalCount")
    val totalCount: Int = 0
)