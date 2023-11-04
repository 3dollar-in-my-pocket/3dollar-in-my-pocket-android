package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Tags(
    @SerializedName("isNew")
    val isNew: Boolean? = false
)