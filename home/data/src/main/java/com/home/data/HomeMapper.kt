package com.home.data

import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.place.PlaceModel
import com.home.domain.data.store.AccountModel
import com.home.domain.data.store.AccountNumberModel
import com.home.domain.data.store.AdditionalInfo
import com.home.domain.data.store.AddressModel
import com.home.domain.data.store.AppearanceDayModel
import com.home.domain.data.store.AroundStoreModel
import com.home.domain.data.store.BankModel
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.BossStoreModel
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.ClassificationModel
import com.home.domain.data.store.CommentModel
import com.home.domain.data.store.CommentStatus
import com.home.domain.data.store.CommentWriter
import com.home.domain.data.store.ContentModel
import com.home.domain.data.store.CountsModel
import com.home.domain.data.store.CreatorModel
import com.home.domain.data.store.CursorModel
import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.DeleteResultModel
import com.home.domain.data.store.EditStoreReviewModel
import com.home.domain.data.store.ExtraModel
import com.home.domain.data.store.FavoriteModel
import com.home.domain.data.store.FeedbackModel
import com.home.domain.data.store.FeedbackType
import com.home.domain.data.store.FoodTruckReviewModel
import com.home.domain.data.store.HistoriesContentModel
import com.home.domain.data.store.HistoriesModel
import com.home.domain.data.store.ImageContentModel
import com.home.domain.data.store.ImagesModel
import com.home.domain.data.store.LocationModel
import com.home.domain.data.store.MarkerModel
import com.home.domain.data.store.MenuModel
import com.home.domain.data.store.NewsPostModel
import com.home.domain.data.store.OpenStatusModel
import com.home.domain.data.store.OpeningHoursModel
import com.home.domain.data.store.PaymentType
import com.home.domain.data.store.PostUserStoreModel
import com.home.domain.data.store.ReasonModel
import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewModel
import com.home.domain.data.store.ReviewReportModel
import com.home.domain.data.store.ReviewStatusType
import com.home.domain.data.store.ReviewWriterModel
import com.home.domain.data.store.ReviewsModel
import com.home.domain.data.store.SalesType
import com.home.domain.data.store.SaveImagesModel
import com.home.domain.data.store.SectionModel
import com.home.domain.data.store.SectionTypeModel
import com.home.domain.data.store.StatusType
import com.home.domain.data.store.StickerModel
import com.home.domain.data.store.StoreMarkerImageModel
import com.home.domain.data.store.StoreModel
import com.home.domain.data.store.StoreNearExistsModel
import com.home.domain.data.store.TagsModel
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.data.store.UserStoreMenuModel
import com.home.domain.data.store.UserStoreModel
import com.home.domain.data.store.VisitCountsModel
import com.home.domain.data.store.VisitModel
import com.home.domain.data.store.VisitorModel
import com.home.domain.data.store.VisitsModel
import com.home.domain.data.store.WriterType
import com.home.domain.data.user.AcquisitionModel
import com.home.domain.data.user.DeviceModel
import com.home.domain.data.user.MedalModel
import com.home.domain.data.user.UserModel
import com.home.domain.request.FilterConditionsTypeModel
import com.home.domain.request.MenuModelRequest
import com.home.domain.request.OpeningHourRequest
import com.home.domain.request.PlaceRequest
import com.home.domain.request.PlaceType
import com.home.domain.request.ReportReviewModelRequest
import com.home.domain.request.UserStoreModelRequest
import com.threedollar.common.utils.toDefaultInt
import com.threedollar.network.data.Reason
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.store.Account
import com.threedollar.network.data.store.AccountNumber
import com.threedollar.network.data.store.AdditionalInfoResponse
import com.threedollar.network.data.store.Address
import com.threedollar.network.data.store.AppearanceDay
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.Bank
import com.threedollar.network.data.store.BossStore
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.store.Category
import com.threedollar.network.data.store.Classification
import com.threedollar.network.data.store.CommentItemResponse
import com.threedollar.network.data.store.Content
import com.threedollar.network.data.store.ContentListCommentResponse
import com.threedollar.network.data.store.Counts
import com.threedollar.network.data.store.Creator
import com.threedollar.network.data.store.Cursor
import com.threedollar.network.data.store.DeleteResultResponse
import com.threedollar.network.data.store.EditStoreReviewResponse
import com.threedollar.network.data.store.Extra
import com.threedollar.network.data.store.Favorite
import com.threedollar.network.data.store.Feedback
import com.threedollar.network.data.store.Histories
import com.threedollar.network.data.store.HistoriesContent
import com.threedollar.network.data.store.Image
import com.threedollar.network.data.store.Images
import com.threedollar.network.data.store.Location
import com.threedollar.network.data.store.Marker
import com.threedollar.network.data.store.Menu
import com.threedollar.network.data.store.NewsPost
import com.threedollar.network.data.store.OpenStatus
import com.threedollar.network.data.store.OpeningHours
import com.threedollar.network.data.store.PostUserStoreResponse
import com.threedollar.network.data.store.Review
import com.threedollar.network.data.store.ReviewReport
import com.threedollar.network.data.store.ReviewWriter
import com.threedollar.network.data.store.Reviews
import com.threedollar.network.data.store.SaveImagesResponse
import com.threedollar.network.data.store.Section
import com.threedollar.network.data.store.SectionType
import com.threedollar.network.data.store.Sticker
import com.threedollar.network.data.store.Store
import com.threedollar.network.data.store.StoreMarkerImageResponse
import com.threedollar.network.data.store.StoreNearExistResponse
import com.threedollar.network.data.store.StoreReviewDetailResponse
import com.threedollar.network.data.store.Tags
import com.threedollar.network.data.store.UserStore
import com.threedollar.network.data.store.UserStoreMenu
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.network.data.store.Visit
import com.threedollar.network.data.store.VisitCounts
import com.threedollar.network.data.store.Visitor
import com.threedollar.network.data.store.Visits
import com.threedollar.network.data.store.WriterResponse
import com.threedollar.network.data.user.Acquisition
import com.threedollar.network.data.user.Device
import com.threedollar.network.data.user.Medal
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.FilterConditionsType
import com.threedollar.network.request.MenuRequest
import com.threedollar.network.request.ReportReviewRequest
import com.threedollar.network.request.UserStoreRequest

fun AdvertisementResponse.Advertisement.asModel(): AdvertisementModelV2 {
    return AdvertisementModelV2(
        advertisementId = advertisementId ?: 0,
        background = AdvertisementModelV2.Background(
            color = background?.color ?: "",
        ),
        extra = AdvertisementModelV2.Extra(
            content = extra?.content ?: "",
            fontColor = extra?.fontColor ?: "",
        ),
        image = AdvertisementModelV2.Image(
            height = image?.height ?: 0,
            url = image?.url ?: "",
            width = image?.width ?: 0,
        ),
        link = AdvertisementModelV2.Link(
            type = link?.type ?: "",
            url = link?.url ?: "",
        ),
        metadata = AdvertisementModelV2.MetaData(
            exposureIndex = metadata?.exposureIndex ?: 0,
        ),
        subTitle = AdvertisementModelV2.SubTitle(
            content = subTitle?.content ?: "",
            fontColor = subTitle?.fontColor ?: "",
        ),
        title = AdvertisementModelV2.Title(
            content = title?.content ?: "",
            fontColor = title?.fontColor ?: "",
        ),
    )
}

fun UserResponse.asModel() = UserModel(
    createdAt = createdAt,
    deviceModel = device.asModel(),
    marketingConsent = marketingConsent,
    medalModel = medal.asModel(),
    name = name,
    socialType = socialType,
    updatedAt = updatedAt,
    userId = userId,
)

fun Device.asModel() = DeviceModel(
    isSetupNotification = isSetupNotification,
)

fun Medal.asModel() = MedalModel(
    acquisitionModel = acquisition?.asModel(),
    createdAt = createdAt,
    disableIconUrl = disableIconUrl,
    iconUrl = iconUrl,
    introduction = introduction,
    medalId = medalId,
    name = name,
    updatedAt = updatedAt,
)

fun Acquisition.asModel() = AcquisitionModel(
    description = description,
)

fun Account.asModel() = AccountModel(
    accountId = accountId ?: "",
    accountType = accountType ?: "",
)

fun Address.asModel() = AddressModel(
    fullAddress = fullAddress ?: "",
)

fun AroundStoreResponse.asModel() = AroundStoreModel(
    contentModels = contents?.map { it.asModel() } ?: listOf(),
    cursorModel = cursor?.asModel() ?: CursorModel(),
)

fun Category.asModel() = CategoryModel(
    categoryId = categoryId ?: "",
    classificationModel = classification?.asModel() ?: ClassificationModel(),
    description = description ?: "",
    imageUrl = imageUrl ?: "",
    isNew = isNew ?: false,
    name = name ?: "",
)

fun Classification.asModel() = ClassificationModel(
    description = description ?: "",
    type = type ?: "",
)

fun Content.asModel() = ContentModel(
    storeModel = store?.asModel() ?: StoreModel(),
    markerModel = marker?.asModel(),
    openStatusModel = openStatus.asModel(),
    distanceM = distanceM ?: 0,
    extraModel = extra?.asModel() ?: ExtraModel(),
)

fun Marker.asModel() = MarkerModel(
    selected = selected.asModel(),
    unSelected = unSelected.asModel()
)

fun StoreMarkerImageResponse.asModel() = StoreMarkerImageModel(
    imageUrl = imageUrl,
    width = width,
    height = height
)

fun Cursor.asModel() = CursorModel(
    hasMore = hasMore ?: false,
    nextCursor = nextCursor,
    totalCount = totalCount.toDefaultInt()
)

fun Extra.asModel() = ExtraModel(
    rating = rating ?: 0.0,
    reviewsCount = reviewsCount ?: 0,
    tagsModel = tags?.asModel() ?: TagsModel(),
    visitCountsModel = visitCounts?.asModel() ?: VisitCountsModel(),
)

fun Location.asModel() = LocationModel(
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
)

fun Store.asModel() = StoreModel(
    accountModel = account?.asModel() ?: AccountModel(),
    addressModel = address?.asModel() ?: AddressModel(),
    categories = categories?.map { it.asModel() } ?: listOf(),
    createdAt = createdAt ?: "",
    isDeleted = isDeleted ?: false,
    locationModel = location?.asModel() ?: LocationModel(),
    activitiesStatus = if (activitiesStatus == "RECENT_ACTIVITY") FilterConditionsTypeModel.RECENT_ACTIVITY else FilterConditionsTypeModel.NO_RECENT_ACTIVITY,
    storeId = storeId ?: "",
    storeName = storeName ?: "",
    storeType = storeType ?: "",
    updatedAt = updatedAt ?: "",
)

fun Tags.asModel() = TagsModel(
    isNew = isNew ?: false,
)

fun VisitCounts.asModel() = VisitCountsModel(
    existsCounts = existsCounts ?: 0,
    isCertified = isCertified ?: false,
    notExistsCounts = notExistsCounts ?: 0,
)

fun AppearanceDay.asModel() = AppearanceDayModel(
    dayOfTheWeek = dayOfTheWeek?.asDayOfTheWeekType() ?: DayOfTheWeekType.SUNDAY,
    locationDescription = locationDescription ?: "",
    openingHoursModel = openingHours?.asModel() ?: OpeningHoursModel(),
)

fun OpeningHours.asModel() = OpeningHoursModel(
    endTime = endTime ?: "",
    startTime = startTime ?: "",
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
    updatedAt = updatedAt ?: "",
    contactsNumber = contactsNumbers?.firstOrNull()?.number,
    isOwner = isOwner ?: false,
    accountNumbers = accountNumbers?.map { it.asModel() } ?: listOf(),
)

fun AccountNumber.asModel() = AccountNumberModel(
    bank = bank?.asModel() ?: BankModel(),
    accountHolder = accountHolder ?: "",
    accountNumber = accountNumber ?: "",
    description = description ?: "",
)

fun Bank.asModel() = BankModel(
    key = key ?: "",
    description = description ?: "",
)

fun Menu.asModel() = MenuModel(
    imageUrl = imageUrl,
    name = name ?: "",
    price = price ?: 0,
)

fun BossStoreResponse.asModel() = BossStoreDetailModel(
    distanceM = distanceM ?: 0,
    favoriteModel = favorite?.asModel() ?: FavoriteModel(),
    feedbackModels = feedbacks?.map { it.asModel() } ?: listOf(),
    openStatusModel = openStatus?.asModel() ?: OpenStatusModel(),
    store = store?.asModel() ?: BossStoreModel(),
    tags = tags?.asModel() ?: TagsModel(),
    newsPosts = newsPosts?.contents?.map { it.asModel() } ?: listOf(),
    reviews = reviews?.contents?.map { it.asModel() } ?: listOf(),
)

fun NewsPost.asModel(): NewsPostModel = NewsPostModel(
    postId = postId,
    body = body,
    sections = sections.map { it.asModel() },
    isOwner = isOwner,
    stickers = stickers.map { it.asModel() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Section.asModel(): SectionModel = SectionModel(
    sectionType = when (this.sectionType) {
        SectionType.IMAGE -> SectionTypeModel.IMAGE
        else -> SectionTypeModel.UNKNOWN
    },
    url = url,
    ratio = ratio
)

fun Sticker.asModel(): StickerModel = StickerModel(
    stickerId = stickerId,
    emoji = emoji,
    count = count,
    reactedByMe = reactedByMe
)

fun Favorite.asModel() = FavoriteModel(
    isFavorite = isFavorite ?: false,
    totalSubscribersCount = totalSubscribersCount ?: 0,
)

fun Feedback.asModel() = FeedbackModel(
    count = count ?: 0,
    feedbackType = feedbackType?.asFeedbackType() ?: FeedbackType.BOSS_IS_KIND,
    ratio = ratio ?: 0.0,
)

fun OpenStatus.asModel() = OpenStatusModel(
    openStartDateTime = openStartDateTime,
    status = status?.asStatusType() ?: StatusType.NONE,
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
        emoji = feedbackType?.emoji,
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
    visits = visits?.asModel() ?: VisitsModel(),

    )

fun Creator.asModel(): CreatorModel = CreatorModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId,
)

fun Images.asModel(): ImagesModel = ImagesModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
    cursor = cursor?.asModel() ?: CursorModel(),
)

fun Image.asModel() = ImageContentModel(
    createdAt = createdAt ?: "",
    imageId = imageId ?: 0,
    updatedAt = updatedAt ?: "",
    url = url ?: "",
)

fun Reviews.asModel() = ReviewsModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
    cursor = cursor?.asModel() ?: CursorModel(),
)

fun StoreReviewDetailResponse.asModel() = ReviewContentModel(
    review = review?.asModel() ?: ReviewModel(),
    reviewReport = reviewReport?.asModel() ?: ReviewReportModel(),
    reviewWriter = reviewWriter?.asModel() ?: ReviewWriterModel(),
    stickers = stickers?.map { it.asModel() } ?: listOf(),
    comments = comments.asModel()
)


fun ContentListCommentResponse.asModel(): List<CommentModel> =
    contents.map { it.asModel() }

fun CommentItemResponse.asModel(): CommentModel = CommentModel(
    commentId = commentId,
    content = content,
    status = CommentStatus.from(status),
    writer = writer.asModel(),
    isOwner = isOwner,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WriterResponse.asModel(): CommentWriter = CommentWriter(
    writerId = writerId,
    writerType = WriterType.from(writerType),
    name = name,
    additionalInfo = additionalInfo?.asModel() ?: AdditionalInfo()
)

fun AdditionalInfoResponse.asModel(): AdditionalInfo = AdditionalInfo(
    type = type,
    medal = medal.asModel()
)

fun Review.asModel() = ReviewModel(
    contents = contents,
    createdAt = createdAt ?: "",
    rating = rating ?: 0,
    reviewId = reviewId ?: 0,
    status = status?.asReviewStatusType() ?: ReviewStatusType.POSTED,
    updatedAt = updatedAt ?: "",
    isOwner = isOwner ?: false,
)

fun ReviewReport.asModel() = ReviewReportModel(
    reportedByMe = reportedByMe ?: false,
)

fun ReviewWriter.asModel() = ReviewWriterModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId,
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
    updatedAt = updatedAt ?: "",
    openingHoursModel = openingHours?.asModel() ?: OpeningHoursModel(),
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

    "CONVENIENCE_STORE" -> {
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
    price = price,

    )

fun Visits.asModel() = VisitsModel(
    counts = counts?.asModel() ?: CountsModel(),
    histories = histories?.asModel() ?: HistoriesModel(),
)

fun Counts.asModel() = CountsModel(
    existsCounts = existsCounts ?: 0,
    isCertified = isCertified ?: false,
    notExistsCounts = notExistsCounts ?: 0,
)

fun Histories.asModel() = HistoriesModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
    cursor = cursor?.asModel() ?: CursorModel(),
)

fun HistoriesContent.asModel() = HistoriesContentModel(
    visit = visit?.asModel() ?: VisitModel(),
    visitor = visitor?.asModel() ?: VisitorModel(),
)

fun Visit.asModel() = VisitModel(
    createdAt = createdAt ?: "",
    type = type ?: "",
    updatedAt = updatedAt ?: "",
    visitDate = visitDate ?: "",
    visitId = visitId ?: "",
)

fun Visitor.asModel() = VisitorModel(
    medal = medal?.asModel() ?: MedalModel(),
    name = name ?: "",
    socialType = socialType,
    userId = userId,
)

fun DeleteResultResponse.asModel() = DeleteResultModel(
    isDeleted = isDeleted ?: false,
)

fun SaveImagesResponse.asModel() = SaveImagesModel(
    createdAt = createdAt ?: "",
    imageId = imageId ?: 0,
    updatedAt = updatedAt ?: "",
    url = url ?: "",
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
    isExists = isExists,
)

fun UserStoreModelRequest.asRequest() = UserStoreRequest(
    appearanceDays = appearanceDays.map { it.name },
    latitude = latitude,
    longitude = longitude,
    menuRequests = menuRequests.map { it.asRequest() },
    paymentMethods = paymentMethods.map { it.name },
    openingHours = openingHours?.asRequest(),
    storeName = storeName,
    storeType = storeType,
)

fun OpeningHourRequest.asRequest() = com.threedollar.network.request.OpeningHourRequest(
    startTime = startTime,
    endTime = endTime,
)

fun MenuModelRequest.asRequest() = MenuRequest(
    category = category,
    name = name,
    price = price,
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
    userId = userId ?: 0,
)

fun ReportReviewModelRequest.asRequest() = ReportReviewRequest(
    reason = reason,
    reasonDetail = reasonDetail,
)

fun ReportReasonsResponse.asModel() = ReportReasonsModel(
    reasonModels = reasons?.map { it.asModel() } ?: listOf(),
)

fun Reason.asModel() = ReasonModel(
    description = description ?: "",
    hasReasonDetail = hasReasonDetail ?: false,
    type = type ?: "",
)

fun com.threedollar.network.data.place.Content.asModel() = PlaceModel(
    addressName = addressName ?: "",
    createdAt = createdAt,
    location = PlaceModel.Location(longitude = location.longitude ?: 0.0, latitude = location.latitude ?: 0.0),
    placeId = placeId,
    placeName = placeName,
    roadAddressName = roadAddressName ?: "",
    updatedAt = updatedAt

)

fun PlaceRequest.asRequest() = com.threedollar.network.request.PlaceRequest(
    location = com.threedollar.network.request.PlaceRequest.Location(longitude = location.longitude, latitude = location.latitude),
    placeName = placeName,
    addressName = addressName,
    roadAddressName = roadAddressName
)

fun PlaceType.asType() = when (this) {
    PlaceType.RECENT_SEARCH -> com.threedollar.network.request.PlaceType.RECENT_SEARCH
}

fun FilterConditionsTypeModel.asType() = when (this) {
    FilterConditionsTypeModel.RECENT_ACTIVITY -> FilterConditionsType.RECENT_ACTIVITY
    FilterConditionsTypeModel.NO_RECENT_ACTIVITY -> FilterConditionsType.NO_RECENT_ACTIVITY
}