package com.threedollar.domain.home.data.store


data class BossStoreDetailModel(
    val distanceM: Int = 0,
    val favoriteModel: FavoriteModel = FavoriteModel(),
    val feedbackModels: List<FeedbackModel> = listOf(),
    val openStatusModel: OpenStatusModel = OpenStatusModel(),
    val store: BossStoreModel = BossStoreModel(),
    val tags: TagsModel = TagsModel(),
    val newsPosts: List<NewsPostModel> = listOf(),
    val reviews: List<ReviewContentModel> = listOf(),
    val reviewTotalCount: Int = 0,
    val hasMoreReviews: Boolean = false,
)