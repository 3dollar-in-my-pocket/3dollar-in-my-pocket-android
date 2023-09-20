package com.home.data

import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
import com.home.domain.data.user.AcquisitionModel
import com.home.domain.data.user.DeviceModel
import com.home.domain.data.user.MedalModel
import com.home.domain.data.user.UserModel
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.store.*
import com.threedollar.network.data.user.Acquisition
import com.threedollar.network.data.user.Device
import com.threedollar.network.data.user.Medal
import com.threedollar.network.data.user.UserResponse

fun AdvertisementResponse.asModel() = AdvertisementModel(
    advertisementId = advertisementId,
    bgColor = bgColor,
    extraContent = extraContent,
    fontColor = fontColor,
    imageHeight = imageHeight,
    imageUrl = imageUrl,
    imageWidth = imageWidth,
    linkUrl = linkUrl,
    subTitle = subTitle
)

fun UserResponse.asModel() = UserModel(
    createdAt = createdAt,
    deviceModel = device.asModel(),
    marketingConsent = marketingConsent,
    medalModel = medal.asModel(),
    name = name,
    socialType = socialType,
    updatedAt = updatedAt,
    userId = userId
)

fun Device.asModel() = DeviceModel(
    isSetupNotification = isSetupNotification
)

fun Medal.asModel() = MedalModel(
    acquisitionModel = acquisition.asModel(),
    createdAt = createdAt,
    disableIconUrl = disableIconUrl,
    iconUrl = iconUrl,
    introduction = introduction,
    medalId = medalId,
    name = name,
    updatedAt = updatedAt
)

fun Acquisition.asModel() = AcquisitionModel(
    description = description
)

fun Account.asModel() = AccountModel(
    accountId = accountId,
    accountType = accountType
)

fun Address.asModel() = AddressModel(
    fullAddress = fullAddress
)

fun AroundStoreResponse.asModel() = AroundStoreModel(
    contentModels = contents.map { it.asModel() },
    cursorModel = cursor.asModel()
)

fun Category.asModel() = CategoryModel(
    categoryId = categoryId,
    classificationModel = classification.asModel(),
    description = description,
    disableImageUrl = disableImageUrl,
    imageUrl = imageUrl,
    isNew = isNew,
    name = name,
)

fun Classification.asModel() = ClassificationModel(
    description = description,
    type = type
)

fun Content.asModel() = ContentModel(
    distanceM = distanceM,
    extraModel = extra.asModel(),
    storeModel = store.asModel()
)

fun Cursor.asModel() = CursorModel(
    hasMore = hasMore,
    nextCursor = nextCursor
)

fun Extra.asModel() = ExtraModel(
    rating = rating,
    reviewsCount = reviewsCount,
    tagsModel = tags.asModel(),
    visitCountsModel = visitCounts?.asModel(),
)

fun Location.asModel() = LocationModel(
    latitude = latitude,
    longitude = longitude
)

fun Store.asModel() = StoreModel(
    accountModel = account.asModel(),
    addressModel = address.asModel(),
    categories = categories.map { it.asModel() },
    createdAt = createdAt,
    isDeleted = isDeleted,
    locationModel = location.asModel(),
    storeId = storeId,
    storeName = storeName,
    storeType = storeType,
    updatedAt = updatedAt,
)

fun Tags.asModel() = TagsModel(
    isNew = isNew
)

fun VisitCounts.asModel() = VisitCountsModel(
    existsCounts = existsCounts,
    isCertified = isCertified,
    notExistsCounts = notExistsCounts
)