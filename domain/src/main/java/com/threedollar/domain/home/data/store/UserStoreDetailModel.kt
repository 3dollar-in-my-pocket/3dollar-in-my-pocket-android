package com.threedollar.domain.home.data.store

data class UserStoreDetailModel(
    val creator: CreatorModel = CreatorModel(),
    val distanceM: Int = 0,
    val favorite: FavoriteModel = FavoriteModel(),
    val images: ImagesModel = ImagesModel(),
    val reviews: ReviewsModel = ReviewsModel(),
    val store: UserStoreModel = UserStoreModel(),
    val tags: TagsModel = TagsModel(),
    val visits: VisitsModel = VisitsModel(),
)