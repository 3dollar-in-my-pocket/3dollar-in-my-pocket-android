package com.threedollar.network.data.screen

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class StoreDetailScreenResponse(
    @SerializedName("sections")
    val sections: List<JsonObject>? = null,
    @SerializedName("viewLog")
    val viewLog: SDPageViewLogResponse? = null,
)
