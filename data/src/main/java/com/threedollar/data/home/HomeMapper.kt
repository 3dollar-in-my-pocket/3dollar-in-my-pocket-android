package com.threedollar.data.home

import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.place.PlaceModel
import com.threedollar.domain.home.data.store.AccountModel
import com.threedollar.domain.home.data.store.AccountNumberModel
import com.threedollar.domain.home.data.store.ActivitiesStatus
import com.threedollar.domain.home.data.store.AdditionalInfo
import com.threedollar.domain.home.data.store.AddressModel
import com.threedollar.domain.home.data.store.AppearanceDayModel
import com.threedollar.domain.home.data.store.AroundStoreModel
import com.threedollar.domain.home.data.store.BankModel
import com.threedollar.domain.home.data.store.BossStoreDetailModel
import com.threedollar.domain.home.data.store.BossStoreModel
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ClassificationModel
import com.threedollar.domain.home.data.store.CommentModel
import com.threedollar.domain.home.data.store.CommentStatus
import com.threedollar.domain.home.data.store.CommentWriter
import com.threedollar.domain.home.data.store.ContactNumberModel
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.CountsModel
import com.threedollar.domain.home.data.store.CreatorModel
import com.threedollar.domain.home.data.store.CursorModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.DeleteResultModel
import com.threedollar.domain.home.data.store.EditStoreReviewModel
import com.threedollar.domain.home.data.store.ExtraModel
import com.threedollar.domain.home.data.store.FavoriteModel
import com.threedollar.domain.home.data.store.FeedbackExistsModel
import com.threedollar.domain.home.data.store.FeedbackModel
import com.threedollar.domain.home.data.store.FeedbackType
import com.threedollar.domain.home.data.store.FoodTruckReviewModel
import com.threedollar.domain.home.data.store.HistoriesContentModel
import com.threedollar.domain.home.data.store.HistoriesModel
import com.threedollar.domain.home.data.store.ImageContentModel
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.domain.home.data.store.ImagesModel
import com.threedollar.domain.home.data.store.LocationModel
import com.threedollar.domain.home.data.store.MarkerModel
import com.threedollar.domain.home.data.store.MenuModel
import com.threedollar.domain.home.data.store.MenuV3Model
import com.threedollar.domain.home.data.store.NewsPostModel
import com.threedollar.domain.home.data.store.OpenStatusModel
import com.threedollar.domain.home.data.store.OpeningHoursModel
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.PostUserStoreModel
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.data.store.ReportReasonsModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewModel
import com.threedollar.domain.home.data.store.ReviewReportModel
import com.threedollar.domain.home.data.store.ReviewStatusType
import com.threedollar.domain.home.data.store.ReviewWriterModel
import com.threedollar.domain.home.data.store.ReviewsModel
import com.threedollar.domain.home.data.store.SalesType
import com.threedollar.domain.home.data.store.SaveImagesModel
import com.threedollar.domain.home.data.store.SectionModel
import com.threedollar.domain.home.data.store.UploadFileModel
import com.threedollar.domain.home.data.store.SectionTypeModel
import com.threedollar.domain.home.data.store.StatusType
import com.threedollar.domain.home.data.store.StickerModel
import com.threedollar.domain.home.data.store.StoreMarkerImageModel
import com.threedollar.domain.home.data.store.StoreModel
import com.threedollar.domain.home.data.store.TagsModel
import com.threedollar.domain.home.data.store.UserStoreDetailModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.home.data.store.VisitCountsModel
import com.threedollar.domain.home.data.store.VisitModel
import com.threedollar.domain.home.data.store.VisitorModel
import com.threedollar.domain.home.data.store.VisitsModel
import com.threedollar.domain.home.data.store.WriterType
import com.threedollar.domain.home.data.user.AcquisitionModel
import com.threedollar.domain.home.data.user.DeviceModel
import com.threedollar.domain.home.data.user.MedalModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.threedollar.domain.home.request.MenuModelRequest
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.PlaceRequest
import com.threedollar.domain.home.request.PlaceType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.threedollar.common.utils.toDefaultInt
import com.threedollar.network.data.Reason
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackExistsResponse
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
import com.threedollar.network.data.store.ContactNumber
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
import com.threedollar.network.data.store.MenuV2
import com.threedollar.network.data.store.MenuV3
import com.threedollar.network.data.store.NewsPost
import com.threedollar.network.data.store.OpenStatus
import com.threedollar.network.data.store.OpeningHours
import com.threedollar.network.data.store.PostUserStoreResponse
import com.threedollar.network.data.store.RepresentativeImage
import com.threedollar.network.data.store.Review
import com.threedollar.network.data.store.ReviewReport
import com.threedollar.network.data.store.ReviewWriter
import com.threedollar.network.data.store.Reviews
import com.threedollar.network.data.store.SaveImagesResponse
import com.threedollar.network.data.store.Section
import com.threedollar.network.data.store.SectionType
import com.threedollar.network.data.store.Sticker
import com.threedollar.network.data.store.UploadFileResponse
import com.threedollar.network.data.store.Store
import com.threedollar.network.data.store.StoreMarkerImageResponse
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
    isVerifiedStore = isVerifiedStore ?: false
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
    storeId = storeId,
    isOwner = isOwner,
    name = name,
    rating = rating,
    location = location?.asModel() ?: LocationModel(),
    address = address.asModel(),
    representativeImages = representativeImages.map { it.asModel() },
    introduction = introduction ?: "",
    snsUrl = snsUrl ?: "",
    menus = menus.map { it.asModel() },
    appearanceDays = appearanceDays.map { it.asModel() },
    categories = categories.map { it.asModel() },
    accountNumbers = accountNumbers.map { it.asModel() },
    contactsNumbers = contactsNumbers.map { it.asModel() },
    activitiesStatus = ActivitiesStatus.from(activitiesStatus.name),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ContactNumber.asModel() = ContactNumberModel(
    number = number,
    description = description ?: "",
)

fun RepresentativeImage.asModel() = ImageModel(
    imageUrl = imageUrl,
    width = width,
    height = height,
    ratio = ratio
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
    store = store.asModel(),
    tags = tags?.asModel() ?: TagsModel(),
    newsPosts = newsPosts?.contents?.map { it.asModel() } ?: listOf(),
    reviews = reviews?.contents?.map { it.asModel() } ?: listOf(),
    reviewTotalCount = reviews?.cursor?.totalCount ?: 0,
    hasMoreReviews = reviews?.cursor?.hasMore ?: false
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
    ratio = ratio ?: 0.0f,
    emoji = emoji ?: "",
    description = description ?: "",
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
    rating = rating ?: 0.0f,
    reviewId = reviewId ?: 0,
    status = status?.asReviewStatusType() ?: ReviewStatusType.POSTED,
    updatedAt = updatedAt ?: "",
    isOwner = isOwner ?: false,
    images = images.map { it.asModel() },
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
    menus = menusV3?.map { it.asModel() } ?: listOf(),
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

    "FOOD_TRUCK" -> {
        SalesType.FOOD_TRUCK
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

fun MenuV3.asModel() = UserStoreMenuModel(
    category = category.asModel(),
    name = name,
    price = price,
    count = count?.toIntOrNull()
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

fun UploadFileResponse.asModel() = UploadFileModel(
    imageUrl = imageUrl,
    width = width,
    height = height,
    ratio = ratio
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

fun UserStoreModelRequest.asRequest() = UserStoreRequest(
    latitude = latitude,
    longitude = longitude,
    storeName = storeName,
    salesType = salesType,
    appearanceDays = appearanceDays?.map { it.name },
    openingHours = openingHours?.asRequest(),
    paymentMethods = paymentMethods?.map { it.name },
    menuRequests = menuRequests?.map { it.asRequest() },
)

fun OpeningHourRequest.asRequest() = com.threedollar.network.request.OpeningHourRequest(
    startTime = startTime,
    endTime = endTime,
)

fun MenuModelRequest.asRequest() = MenuRequest(
    name = name,
    count = count,
    price = price,
    category = category,
    description = description,
)

fun PostUserStoreResponse.asModel() = PostUserStoreModel(
    storeId = storeId ?: 0,
    isOwner = isOwner ?: false,
    name = name ?: "",
    salesType = salesTypeV2?.type?.name?.asSalesType() ?: SalesType.NONE,
    salesTypeDescription = salesTypeV2?.description ?: "",
    rating = rating ?: 0.0,
    location = LocationModel(
        latitude = location?.latitude ?: 0.0,
        longitude = location?.longitude ?: 0.0
    ),
    address = address.asModel(),
    categories = categories?.map { it.asModel() } ?: listOf(),
    appearanceDays = appearanceDays ?: listOf(),
    openingHours = openingHours?.asModel(),
    paymentMethods = paymentMethods ?: listOf(),
    menus = menusV3?.map { it.asMenuV3Model() } ?: listOf(),
    isDeleted = isDeleted ?: false,
    activitiesStatus = activitiesStatus ?: "",
    createdAt = createdAt ?: "",
    updatedAt = updatedAt ?: "",
)

fun MenuV2.asMenuV3Model() = MenuV3Model(
    name = name,
    price = price,
    count = count,
    description = description,
    category = category.asModel()
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

fun FeedbackExistsResponse.asModel() = FeedbackExistsModel(
    exists = exists
)