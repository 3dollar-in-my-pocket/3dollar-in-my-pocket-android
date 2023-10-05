package com.home.data

import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
import com.home.domain.data.user.AcquisitionModel
import com.home.domain.data.user.DeviceModel
import com.home.domain.data.user.MedalModel
import com.home.domain.data.user.UserModel
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
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

fun AppearanceDay.asModel() = AppearanceDayModel(
    dayOfTheWeek = dayOfTheWeek.asDayOfTheWeekType(),
    locationDescription = locationDescription,
    openingHoursModel = openingHours.asModel()
)

fun OpeningHours.asModel() = OpeningHoursModel(
    endTime = endTime,
    startTime = startTime
)

fun BossStore.asModel() = BossStoreModel(
    address = address.asModel(),
    appearanceDayModels = appearanceDays.map { it.asModel() },
    categories = categories.map { it.asModel() },
    createdAt = createdAt,
    imageUrl = imageUrl,
    introduction = introduction,
    location = location?.asModel(),
    menuModels = menus.map { it.asModel() },
    name = name,
    snsUrl = snsUrl,
    storeId = storeId,
    updatedAt = updatedAt
)

fun Menu.asModel() = MenuModel(
    imageUrl = imageUrl,
    name = name,
    price = price
)

fun BossStoreResponse.asModel() = BossStoreDetailModel(
    distanceM = distanceM,
    favoriteModel = favorite.asModel(),
    feedbackModels = feedbacks.map { it.asModel() },
    openStatusModel = openStatus.asModel(),
    store = store.asModel(),
    tags = tags.asModel()
)

fun Favorite.asModel() = FavoriteModel(
    isFavorite = isFavorite,
    totalSubscribersCount = totalSubscribersCount
)

fun Feedback.asModel() = FeedbackModel(
    count = count,
    feedbackType = feedbackType.asFeedbackType(),
    ratio = ratio
)

fun OpenStatus.asModel() = OpenStatusModel(
    openStartDateTime = openStartDateTime,
    status = status.asStatusType()
)

fun String.asDayOfTheWeekType() = when (this) {
    "MONDAY" -> {
        DayOfTheWeekType.MONDAY
    }

    "TUESDAY" -> {
        DayOfTheWeekType.TUESDAY
    }

    "WEDNESDAY" -> {
        DayOfTheWeekType.WEDNESDAY
    }

    "THURSDAY" -> {
        DayOfTheWeekType.THURSDAY
    }

    "FRIDAY" -> {
        DayOfTheWeekType.FRIDAY
    }

    "SATURDAY" -> {
        DayOfTheWeekType.SATURDAY
    }

    else -> {
        DayOfTheWeekType.SUNDAY
    }
}

fun String.asFeedbackType() = when (this) {
    "HANDS_ARE_FAST" -> {
        FeedbackType.HANDS_ARE_FAST
    }

    "FOOD_IS_DELICIOUS" -> {
        FeedbackType.FOOD_IS_DELICIOUS
    }

    "HYGIENE_IS_CLEAN" -> {
        FeedbackType.HYGIENE_IS_CLEAN
    }

    "BOSS_IS_KIND" -> {
        FeedbackType.BOSS_IS_KIND
    }

    "CAN_PAY_BY_CARD" -> {
        FeedbackType.CAN_PAY_BY_CARD
    }

    "GOOD_VALUE_FOR_MONEY" -> {
        FeedbackType.GOOD_VALUE_FOR_MONEY
    }

    "GOOD_TO_EAT_IN_ONE_BITE" -> {
        FeedbackType.GOOD_TO_EAT_IN_ONE_BITE
    }

    else -> {
        FeedbackType.GOT_A_BONUS
    }
}

fun String.asStatusType() = when (this) {
    "OPEN" -> {
        StatusType.OPEN
    }

    else -> {
        StatusType.CLOSED
    }
}

fun FeedbackCountResponse.asModel(feedbackTypeResponseList: List<FeedbackTypeResponse>): FoodTruckReviewModel {

    val feedbackType = feedbackTypeResponseList.find { type ->
        (type as? FeedbackTypeResponse)?.feedbackType == this.feedbackType
    }
    return FoodTruckReviewModel(
        count = count,
        feedbackType = this.feedbackType,
        ratio = ratio,
        description = feedbackType?.description,
        emoji = feedbackType?.emoji
    )
}

fun UserStoreResponse.asModel(): UserStoreDetailModel = UserStoreDetailModel(
    creator = creator?.asModel(),
    distanceM = distanceM,
    favorite = favorite?.asModel(),
    images = images?.asModel(),
    reviews = reviews?.asModel(),
    store = store?.asModel(),
    tags = tags?.asModel(),
    visits = visits?.asModel()

)

fun Creator.asModel(): CreatorModel = CreatorModel(
    medal = medal.asModel(),
    name = name,
    socialType = socialType,
    userId = userId
)

fun Images.asModel(): ImagesModel = ImagesModel(
    contents = contents.map { it.asModel() },
    cursor = cursor.asModel()
)

fun ImageContent.asModel() = ImageContentModel(
    createdAt = createdAt,
    imageId = imageId,
    updatedAt = updatedAt,
    url = url
)

fun Reviews.asModel() = ReviewsModel(
    contents = contents.map { it.asModel() }
)

fun ReviewContent.asModel() = ReviewContentModel(
    review = review.asModel(),
    reviewReport = reviewReport.asModel(),
    reviewWriter = reviewWriter.asModel()

)

fun Review.asModel() = ReviewModel(
    contents = contents,
    createdAt = createdAt,
    rating = rating,
    reviewId = reviewId,
    status = status,
    updatedAt = updatedAt
)

fun ReviewReport.asModel() = ReviewReportModel(
    reportedByMe = reportedByMe
)

fun ReviewWriter.asModel() = ReviewWriterModel(
    medal = medal.asModel(),
    name = name,
    socialType = socialType,
    userId = userId
)

fun UserStore.asModel() = UserStoreModel(
    address = address.asModel(),
    appearanceDays = appearanceDays,
    categories = categories.map { it.asModel() },
    createdAt = createdAt,
    location = location.asModel(),
    menus = menus.map { it.asModel() },
    name = name,
    paymentMethods = paymentMethods,
    rating = rating,
    salesType = salesType,
    storeId = storeId,
    updatedAt = updatedAt
)

fun UserStoreMenu.asModel() = UserStoreMenuModel(
    category = category.asModel(),
    menuId = menuId,
    name = name,
    price = price
)

fun Visits.asModel() = VisitsModel(
    counts = counts.asModel(),
    histories = histories.asModel()
)

fun Counts.asModel() = CountsModel(
    existsCounts = existsCounts,
    isCertified = isCertified,
    notExistsCounts = notExistsCounts
)

fun Histories.asModel() = HistoriesModel(
    contents = contents.map { it.asModel() },
    cursor = cursor.asModel()
)

fun HistoriesContent.asModel() = HistoriesContentModel(
    visit = visit.asModel(),
    visitor = visitor.asModel()
)

fun Visit.asModel() = VisitModel(
    createdAt = createdAt,
    type = type,
    updatedAt = updatedAt,
    visitDate = visitDate,
    visitId = visitId
)

fun Visitor.asModel() = VisitorModel(
    medal = medal.asModel(),
    name = name,
    socialType = socialType,
    userId = userId
)