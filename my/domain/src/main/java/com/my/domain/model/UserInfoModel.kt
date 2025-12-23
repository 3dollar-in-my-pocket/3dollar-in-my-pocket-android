package com.my.domain.model

data class UserInfoModel(
    val userId: String = "",
    val name: String = "",
    val socialType: String = "",
    val medal: MedalModel? = null,
    val activity: UserActivityModel = UserActivityModel(),
    val settings: UserSettingsModel = UserSettingsModel()
)

data class MedalModel(
    val medalId: String,
    val name: String,
    val iconUrl: String,
    val introduction: String
)

data class UserActivityModel(
    val reviewsCount: Int = 0,
    val storesCount: Int = 0,
    val pollsCount: Int = 0,
    val medalCount: Int = 0,
    val favoriteStoresCount: Int = 0,
    val existsFavoriteFolder: Boolean = false,
    val totalFeedbacksCounts: Int = 0
)

data class UserSettingsModel(
    val enableActivitiesPush: Boolean = false,
    val marketingConsent: String = ""
)