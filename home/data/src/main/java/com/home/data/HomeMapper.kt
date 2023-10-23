package com.home.data

import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
import com.home.domain.data.user.AcquisitionModel
import com.home.domain.data.user.DeviceModel
import com.home.domain.data.user.MedalModel
import com.home.domain.data.user.UserModel
import com.home.domain.request.MenuModelRequest
import com.home.domain.request.UserStoreModelRequest
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.store.*
import com.threedollar.network.data.user.Acquisition
import com.threedollar.network.data.user.Device
import com.threedollar.network.data.user.Medal
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MenuRequest
import com.threedollar.network.request.UserStoreRequest

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
    acquisitionModel = acquisition?.asModel(),
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
    accountId = accountId ?: "",
    accountType = accountType ?: ""
)

fun Address.asModel() = AddressModel(
    fullAddress = fullAddress ?: ""
)

fun AroundStoreResponse.asModel() = AroundStoreModel(
    contentModels = contents?.map { it.asModel() } ?: listOf(),
    cursorModel = cursor?.asModel() ?: CursorModel()
)

fun Category.asModel() = CategoryModel(
    categoryId = categoryId ?: "",
    classificationModel = classification?.asModel() ?: ClassificationModel(),
    description = description ?: "",
    disableImageUrl = disableImageUrl ?: "",
    imageUrl = imageUrl ?: "",
    isNew = isNew ?: false,
    name = name ?: "",
)

fun Classification.asModel() = ClassificationModel(
    description = description ?: "",
    type = type ?: ""
)

fun Content.asModel() = ContentModel(
    distanceM = distanceM ?: 0,
    extraModel = extra?.asModel() ?: ExtraModel(),
    storeModel = store?.asModel() ?: StoreModel()
)

fun Cursor.asModel() = CursorModel(
    hasMore = hasMore ?: false,
    nextCursor = nextCursor
)

fun Extra.asModel() = ExtraModel(
    rating = rating ?: 0.0,
    reviewsCount = reviewsCount ?: 0,
    tagsModel = tags?.asModel() ?: TagsModel(),
    visitCountsModel = visitCounts?.asModel() ?: VisitCountsModel(),
)

fun Location.asModel() = LocationModel(
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0
)

fun Store.asModel() = StoreModel(
    accountModel = account?.asModel() ?: AccountModel(),
    addressModel = address?.asModel() ?: AddressModel(),
    categories = categories?.map { it.asModel() } ?: listOf(),
    createdAt = createdAt ?: "",
    isDeleted = isDeleted ?: false,
    locationModel = location?.asModel() ?: LocationModel(),
    storeId = storeId ?: "",
    storeName = storeName ?: "",
    storeType = storeType ?: "",
    updatedAt = updatedAt ?: "",
)

fun Tags.asModel() = TagsModel(
    isNew = isNew ?: false
)

fun VisitCounts.asModel() = VisitCountsModel(
    existsCounts = existsCounts ?: 0,
    isCertified = isCertified ?: false,
    notExistsCounts = notExistsCounts ?: 0
)

fun AppearanceDay.asModel() = AppearanceDayModel(
    dayOfTheWeek = dayOfTheWeek?.asDayOfTheWeekType() ?: DayOfTheWeekType.SUNDAY,
    locationDescription = locationDescription ?: "",
    openingHoursModel = openingHours?.asModel() ?: OpeningHoursModel()
)

fun OpeningHours.asModel() = OpeningHoursModel(
    endTime = endTime ?: "",
    startTime = startTime ?: ""
)

fun BossStore.asModel() = BossStoreModel(
    address = address?.asModel() ?: AddressModel(),
    appearanceDayModels = appearanceDays?.map { it.asModel() } ?: listOf(),
    categories = categories?.map { it.asModel() } ?: listOf(),
    createdAt = createdAt ?: "",
    imageUrl = imageUrl,
    introduction = introduction,
    location = location?.asModel(),
    menuModels = menus?.map { it.asModel() } ?: listOf(),
    name = name ?: "",
    snsUrl = snsUrl,
    storeId = storeId ?: "",
    updatedAt = updatedAt ?: ""
)

fun Menu.asModel() = MenuModel(
    imageUrl = imageUrl,
    name = name ?: "",
    price = price ?: 0
)

fun BossStoreResponse.asModel() = BossStoreDetailModel(
    distanceM = distanceM ?: 0,
    favoriteModel = favorite?.asModel() ?: FavoriteModel(),
    feedbackModels = feedbacks?.map { it.asModel() } ?: listOf(),
    openStatusModel = openStatus?.asModel() ?: OpenStatusModel(),
    store = store?.asModel() ?: BossStoreModel(),
    tags = tags?.asModel() ?: TagsModel()
)

fun Favorite.asModel() = FavoriteModel(
    isFavorite = isFavorite ?: false,
    totalSubscribersCount = totalSubscribersCount ?: 0
)

fun Feedback.asModel() = FeedbackModel(
    count = count ?: 0,
    feedbackType = feedbackType?.asFeedbackType() ?: FeedbackType.BOSS_IS_KIND,
    ratio = ratio ?: 0.0
)

fun OpenStatus.asModel() = OpenStatusModel(
    openStartDateTime = openStartDateTime,
    status = status?.asStatusType() ?: StatusType.OPEN
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

fun String.asReviewStatusType() = when (this) {
    "POSTED" -> {
        ReviewStatusType.POSTED
    }
    "FILTERED" -> {
        ReviewStatusType.FILTERED
    }
    else -> {
        ReviewStatusType.DELETED
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
    creator = creator?.asModel() ?: CreatorModel(),
    distanceM = distanceM ?: 0,
    favorite = favorite?.asModel() ?: FavoriteModel(),
    images = images?.asModel() ?: ImagesModel(),
    reviews = reviews?.asModel() ?: ReviewsModel(),
    store = store?.asModel() ?: UserStoreModel(),
    tags = tags?.asModel() ?: TagsModel(),
    visits = visits?.asModel() ?: VisitsModel()

)

fun Creator.asModel(): CreatorModel = CreatorModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId
)

fun Images.asModel(): ImagesModel = ImagesModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
    cursor = cursor?.asModel() ?: CursorModel()
)

fun Image.asModel() = ImageContentModel(
    createdAt = createdAt ?: "",
    imageId = imageId ?: 0,
    updatedAt = updatedAt ?: "",
    url = url ?: ""
)

fun Reviews.asModel() = ReviewsModel(
    contents = contents?.map { it.asModel() } ?: listOf()
)

fun ReviewContent.asModel() = ReviewContentModel(
    review = review?.asModel() ?: ReviewModel(),
    reviewReport = reviewReport?.asModel() ?: ReviewReportModel(),
    reviewWriter = reviewWriter?.asModel() ?: ReviewWriterModel()

)

fun Review.asModel() = ReviewModel(
    contents = contents,
    createdAt = createdAt ?: "",
    rating = rating ?: 0,
    reviewId = reviewId ?: 0,
    status = status?.asReviewStatusType() ?: ReviewStatusType.POSTED,
    updatedAt = updatedAt ?: "",
    isOwner = isOwner ?: false
)

fun ReviewReport.asModel() = ReviewReportModel(
    reportedByMe = reportedByMe ?: false
)

fun ReviewWriter.asModel() = ReviewWriterModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId
)

fun UserStore.asModel() = UserStoreModel(
    address = address?.asModel() ?: AddressModel(),
    appearanceDays = appearanceDays?.map { it.asDayOfTheWeekType() } ?: listOf(),
    categories = categories?.map { it.asModel() } ?: listOf(),
    createdAt = createdAt ?: "",
    location = location?.asModel() ?: LocationModel(),
    menus = menus?.map { it.asModel() } ?: listOf(),
    name = name ?: "",
    paymentMethods = paymentMethods?.map { it.asPaymentType() } ?: listOf(),
    rating = rating ?: 0.0,
    salesType = salesType?.asSalesType() ?: SalesType.NONE,
    storeId = storeId ?: 0,
    updatedAt = updatedAt ?: ""
)

fun String.asPaymentType() = when (this) {
    "CARD" -> PaymentType.CARD
    "ACCOUNT_TRANSFER" -> PaymentType.ACCOUNT_TRANSFER
    else -> PaymentType.CASH
}

fun String.asSalesType() = when (this) {
    "ROAD" -> {
        SalesType.ROAD
    }

    "STORE" -> {
        SalesType.STORE
    }

    "CONVENIENCE_STORE " -> {
        SalesType.CONVENIENCE_STORE
    }

    else -> {
        SalesType.NONE
    }
}

fun UserStoreMenu.asModel() = UserStoreMenuModel(
    category = category?.asModel() ?: CategoryModel(),
    menuId = menuId ?: 0,
    name = name,
    price = price
)

fun Visits.asModel() = VisitsModel(
    counts = counts?.asModel() ?: CountsModel(),
    histories = histories?.asModel() ?: HistoriesModel()
)

fun Counts.asModel() = CountsModel(
    existsCounts = existsCounts ?: 0,
    isCertified = isCertified ?: false,
    notExistsCounts = notExistsCounts ?: 0
)

fun Histories.asModel() = HistoriesModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
    cursor = cursor?.asModel() ?: CursorModel()
)

fun HistoriesContent.asModel() = HistoriesContentModel(
    visit = visit?.asModel() ?: VisitModel(),
    visitor = visitor?.asModel() ?: VisitorModel()
)

fun Visit.asModel() = VisitModel(
    createdAt = createdAt ?: "",
    type = type ?: "",
    updatedAt = updatedAt ?: "",
    visitDate = visitDate ?: "",
    visitId = visitId ?: ""
)

fun Visitor.asModel() = VisitorModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId
)

fun DeleteResultResponse.asModel() = DeleteResultModel(
    isDeleted = isDeleted ?: false
)

fun SaveImagesResponse.asModel() = SaveImagesModel(
    createdAt = createdAt ?: "",
    imageId = imageId ?: 0,
    updatedAt = updatedAt ?: "",
    url = url ?: ""
)

fun EditStoreReviewResponse.asModel() = EditStoreReviewModel(
    contents = contents ?: "",
    createdAt = createdAt ?: "",
    rating = rating ?: 0,
    reviewId = reviewId ?: 0,
    status = status ?: "",
    storeId = storeId ?: 0,
    updatedAt = updatedAt ?: "",
    userId = userId ?: 0,
)

fun StoreNearExistResponse.asModel() = StoreNearExistsModel(
    isExists = isExists
)

fun UserStoreModelRequest.asRequest() = UserStoreRequest(
    appearanceDays = appearanceDays.map { it.name },
    latitude = latitude,
    longitude = longitude,
    menuRequests = menuRequests.map { it.asRequest() },
    paymentMethods = paymentMethods.map { it.name },
    storeName = storeName,
    storeType = storeType
)

fun MenuModelRequest.asRequest() = MenuRequest(
    category = category,
    name = name,
    price = price
)

fun PostUserStoreResponse.asModel() = PostUserStoreModel(
    address = address.asModel(),
    categories = categories,
    createdAt = createdAt ?: "",
    isDeleted = isDeleted ?: false,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    rating = rating ?: 0.0,
    salesType = salesType?.asSalesType() ?: SalesType.NONE,
    storeId = storeId ?: 0,
    storeName = storeName ?: "",
    updatedAt = updatedAt ?: "",
    userId = userId ?: 0
)