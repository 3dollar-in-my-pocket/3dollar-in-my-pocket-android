package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName


data class Extra(
    @SerializedName("reviewsCount")
    val reviewsCount: Int? = 0,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("visitCounts")
    val visitCounts: VisitCounts? = null,
    @SerializedName("tags")
    val tags: Tags? = Tags()
)