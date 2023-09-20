package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName


data class Extra(
    @SerializedName("rating")
    val rating: Int? = null,
    @SerializedName("reviewsCount")
    val reviewsCount: Int = 0,
    @SerializedName("tags")
    val tags: Tags = Tags(),
    @SerializedName("visitCounts")
    val visitCounts: VisitCounts? = null
)