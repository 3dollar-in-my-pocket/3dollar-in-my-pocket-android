package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class BossStoreResponse(
    @SerializedName("distanceM")
    val distanceM: Int? = 0,
    @SerializedName("favorite")
    val favorite: Favorite? = Favorite(),
    @SerializedName("feedbacks")
    val feedbacks: List<Feedback>? = listOf(),
    @SerializedName("openStatus")
    val openStatus: OpenStatus? = OpenStatus(),
    @SerializedName("store")
    val store: BossStore = BossStore(),
    @SerializedName("tags")
    val tags: Tags? = Tags(),
    @SerializedName("newsPosts")
    val newsPosts: ContentsWithCursorWithTotalCountResponse<NewsPost>? = ContentsWithCursorWithTotalCountResponse(),
    @SerializedName("reviews")
    val reviews: ContentsWithCursorWithTotalCountResponse<StoreReviewDetailResponse>? = ContentsWithCursorWithTotalCountResponse()
)