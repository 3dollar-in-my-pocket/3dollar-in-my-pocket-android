package com.threedollar.domain.my.model

data class UserInfoModel(
    val userId: String,
    val name: String,
    val socialType: String,
    val medal: MedalModel?,
    val activity: UserActivityModel,
    val settings: UserSettingsModel
)

data class MedalModel(
    val medalId: String,
    val name: String,
    val iconUrl: String,
    val introduction: String
)

data class UserActivityModel(
    val reviewsCount: Int,
    val storesCount: Int,
    val pollsCount: Int,
    val medalCount: Int,
    val favoriteStoresCount: Int,
    val existsFavoriteFolder: Boolean,
    val totalFeedbacksCounts: Int
)

data class UserSettingsModel(
    val enableActivitiesPush: Boolean,
    val marketingConsent: String
)