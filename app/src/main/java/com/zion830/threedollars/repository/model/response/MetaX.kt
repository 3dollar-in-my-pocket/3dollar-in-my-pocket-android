package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class MetaX(
    @SerializedName("is_end")
    val isEnd: Boolean = false,
    @SerializedName("pageable_count")
    val pageableCount: Int = 0,
    @SerializedName("same_name")
    val sameName: SameName = SameName(),
    @SerializedName("total_count")
    val totalCount: Int = 0
)