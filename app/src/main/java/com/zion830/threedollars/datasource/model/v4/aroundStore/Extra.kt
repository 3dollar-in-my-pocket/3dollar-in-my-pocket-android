package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class Extra(
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("reviewsCount")
    val reviewsCount: Int = 0,
    @SerializedName("tags")
    val tags: Tags = Tags(),
    @SerializedName("visitCounts")
    val visitCounts: VisitCounts = VisitCounts(),
)