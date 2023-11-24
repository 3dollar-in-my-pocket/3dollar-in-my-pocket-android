package com.threedollar.network.data.kakao


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("same_name")
    val sameName: SameName = SameName(),
    @SerializedName("pageable_count")
    val pageableCount: Int = 0,
    @SerializedName("total_count")
    val totalCount: Int = 0,
    @SerializedName("is_end")
    val isEnd: Boolean = false
)