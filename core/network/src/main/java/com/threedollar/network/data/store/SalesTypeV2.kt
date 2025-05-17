package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class SalesTypeV2(
    @SerializedName("type") val type: SalesTypeType = SalesTypeType.ROAD,
    @SerializedName("description") val description: String = ""
)

enum class SalesTypeType { ROAD, STORE, CONVENIENCE_STORE, FOOD_TRUCK}
