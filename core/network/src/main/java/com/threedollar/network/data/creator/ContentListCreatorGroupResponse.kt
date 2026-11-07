package com.threedollar.network.data.creator

import com.google.gson.annotations.SerializedName

data class ContentListCreatorGroupResponse(
    @SerializedName("contents")
    val contents: List<CreatorGroupResponse>? = null,
)

data class CreatorGroupResponse(
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("creatorNames")
    val creatorNames: List<String>? = null,
)
