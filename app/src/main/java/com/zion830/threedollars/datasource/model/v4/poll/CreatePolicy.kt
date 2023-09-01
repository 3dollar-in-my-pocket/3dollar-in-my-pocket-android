package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName

data class CreatePolicy(
    @SerializedName("currentCount")
    val currentCount: Int = 0,
    @SerializedName("limitCount")
    val limitCount: Int = 0,
)