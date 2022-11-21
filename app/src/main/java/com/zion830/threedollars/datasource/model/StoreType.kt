package com.zion830.threedollars.datasource.model

import com.google.gson.annotations.SerializedName

enum class StoreType {
    @SerializedName("ROAD")
    ROAD,

    @SerializedName("STORE")
    STORE,

    @SerializedName("CONVENIENCE_STORE")
    CONVENIENCE_STORE;
}