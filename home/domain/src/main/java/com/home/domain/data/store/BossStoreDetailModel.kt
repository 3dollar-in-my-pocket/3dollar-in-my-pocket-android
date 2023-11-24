package com.home.domain.data.store


data class BossStoreDetailModel(
    val distanceM: Int = 0,
    val favoriteModel: FavoriteModel = FavoriteModel(),
    val feedbackModels: List<FeedbackModel> = listOf(),
    val openStatusModel: OpenStatusModel = OpenStatusModel(),
    val store: BossStoreModel = BossStoreModel(),
    val tags: TagsModel = TagsModel()
)