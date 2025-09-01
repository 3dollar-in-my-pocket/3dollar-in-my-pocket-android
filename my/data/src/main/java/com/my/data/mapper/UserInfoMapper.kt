package com.my.data.mapper

import com.my.domain.model.UserInfoModel
import com.my.domain.model.MedalModel
import com.my.domain.model.UserActivityModel
import com.my.domain.model.UserInfoUpdateModel
import com.my.domain.model.UserSettingsModel
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.request.PatchUserInfoRequest

object UserInfoMapper {

    fun toDomainModel(response: UserWithDetailApiResponse): UserInfoModel {
        return UserInfoModel(
            userId = response.userId.toString(),
            name = response.name,
            socialType = response.socialType,
            medal = response.representativeMedal.takeIf { it.medalId != null && it.medalId != 0 }?.let { medal ->
                MedalModel(
                    medalId = medal.medalId.toString(),
                    name = medal.name ?: "",
                    iconUrl = medal.iconUrl ?: "",
                    introduction = medal.introduction ?: ""
                )
            },
            activity = UserActivityModel(
                reviewsCount = response.activities.writeReviewCount,
                storesCount = response.activities.createStoreCount,
                pollsCount = 0, // Poll count not available in Activities
                favoriteStoresCount = response.activities.favoriteStoreCount,
                existsFavoriteFolder = response.activities.favoriteStoreCount > 0,
                totalFeedbacksCounts = response.activities.visitStoreCount
            ),
            settings = UserSettingsModel(
                enableActivitiesPush = response.settings.enableActivitiesPush,
                marketingConsent = response.settings.marketingConsent
            )
        )
    }

    fun toNetworkRequest(domainModel: UserInfoUpdateModel): PatchUserInfoRequest {
        return PatchUserInfoRequest(
            name = domainModel.name,
            representativeMedalId = domainModel.representativeMedalId
        )
    }
}