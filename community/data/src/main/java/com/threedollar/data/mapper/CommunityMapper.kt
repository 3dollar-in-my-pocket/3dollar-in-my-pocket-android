package com.threedollar.data.mapper

import com.home.domain.data.store.ReasonModel
import com.home.domain.data.store.ReportReasonsModel
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.toDefaultInt
import com.threedollar.data.mapper.GetPollCommentListResponseMapper.toMapper
import com.threedollar.data.mapper.GetPollListResponseMapper.toMapper
import com.threedollar.data.mapper.GetPollResponseMapper.toMapper
import com.threedollar.data.mapper.GetPopularStoresResponseMapper.toMapper
import com.threedollar.data.mapper.GetUserPollListResponseMapper.toMapper
import com.threedollar.domain.data.AdvertisementModelV2
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.domain.model.PollCreateModel
import com.threedollar.domain.model.ReportReason
import com.threedollar.domain.model.ReportReasonsGroupType
import com.threedollar.network.data.Reason
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import com.home.domain.request.ReportReasonsGroupType as NetworkReportReasonsGroupType
import com.threedollar.domain.model.ReportReasonsModel as DomainReportReasonsModel

fun AdvertisementResponse.Advertisement.asModel(): AdvertisementModelV2 {
    return AdvertisementModelV2(
        advertisementId = advertisementId ?: 0,
        background = AdvertisementModelV2.Background(
            color = background?.color ?: ""
        ),
        extra = AdvertisementModelV2.Extra(
            content = extra?.content ?: "",
            fontColor = extra?.fontColor ?: ""
        ),
        image = AdvertisementModelV2.Image(
            height = image?.height ?: 0,
            url = image?.url ?: "",
            width = image?.width ?: 0
        ),
        link = AdvertisementModelV2.Link(
            type = link?.type ?: "",
            url = link?.url ?: ""
        ),
        metadata = AdvertisementModelV2.MetaData(
            exposureIndex = metadata?.exposureIndex ?: 0
        ),
        subTitle = AdvertisementModelV2.SubTitle(
            content = subTitle?.content ?: "",
            fontColor = subTitle?.fontColor ?: ""
        ),
        title = AdvertisementModelV2.Title(
            content = title?.content ?: "",
            fontColor = title?.fontColor ?: ""
        )
    )
}

fun PollCreateApiResponse?.toPollIdMapper(): PollId {
    return PollId(this?.id.orEmpty())
}

fun GetPollResponse?.toPollItemMapper(): PollItem {
    return PollItem(
        meta = this?.meta.toMapper(), poll = this?.poll.toMapper(), pollWriter = this?.pollWriter.toMapper()
    )
}

fun BaseResponse<String>.toDefaultResponseMapper(): DefaultResponse {
    return DefaultResponse(data = data.orEmpty(), message = message.orEmpty(), resultCode = resultCode.orEmpty())
}

fun PollCategoryApiResponse?.toListCategoryMapper(): List<Category> {
    return this?.categories.orEmpty().map { it.toCategoryMapper() }
}

fun PollCategoryApiResponse.Category.toCategoryMapper(): Category {
    return Category(categoryId.orEmpty(), title.orEmpty(), content.orEmpty())
}

fun GetPollListResponse?.toPollListMapper(): PollList {
    return PollList(pollItems = this?.contents.orEmpty().map { it.toMapper() }, cursor = this?.cursor.toMapper())
}

fun PollPolicyApiResponse?.toCreatePolicyMapper(): CreatePolicy {
    return CreatePolicy(
        this?.createPolicy?.currentCount.toDefaultInt(),
        this?.createPolicy?.limitCount.toDefaultInt(),
        this?.createPolicy?.pollRetentionDays.toDefaultInt()
    )
}

fun GetUserPollListResponse?.toUserPollItemListMapper(): UserPollItemList {
    return UserPollItemList(this?.poll.toMapper(), this?.meta.toMapper())
}

fun PollCommentCreateApiResponse?.toCommentIdMapper(): CommentId {
    return CommentId(this?.id.orEmpty())
}

fun GetPollCommentListResponse?.toPollCommentListMapper(): PollCommentList {
    return PollCommentList(this?.contents.orEmpty().map { it.toMapper() }, this?.cursor.toMapper())
}

fun GetPopularStoresResponse?.toPopularStoresMapper(): PopularStores {
    return PopularStores(this?.contents.orEmpty().map { it.toMapper() }, this?.cursor.toMapper())
}

fun GetNeighborhoodsResponse?.toNeighborhoodsMapper(): Neighborhoods {
    return Neighborhoods(this?.neighborhoods.orEmpty().map { neighborhood ->
        Neighborhoods.Neighborhood(neighborhood.description.orEmpty(), neighborhood.districts.orEmpty().map {
            Neighborhoods.Neighborhood.District(it.description.orEmpty(), it.district.orEmpty())
        }, neighborhood.province.orEmpty())
    })
}

fun ReportReasonsResponse?.asModel() = ReportReasonsModel(
    reasonModels = this?.reasons?.map { it.asModel() } ?: listOf()
)

fun Reason.asModel() = ReasonModel(
    description = description ?: "",
    hasReasonDetail = hasReasonDetail ?: false,
    type = type ?: ""
)

fun PollCreateModel.toNetworkRequest(): PollCreateApiRequest {
    return PollCreateApiRequest(
        categoryId = categoryId,
        options = options.map { PollCreateApiRequest.Option(it) },
        startDateTime = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
        title = title
    )
}

fun ReportReasonsModel.toDomainModel(): DomainReportReasonsModel {
    return DomainReportReasonsModel(
        reasons = reasonModels.map { reason ->
            ReportReason(
                id = reason.type,
                title = reason.description,
                description = reason.description,
                hasDetail = reason.hasReasonDetail
            )
        }
    )
}

fun ReportReasonsGroupType.toNetworkGroupType(): NetworkReportReasonsGroupType {
    return when (this) {
        ReportReasonsGroupType.POLL -> NetworkReportReasonsGroupType.POLL
        ReportReasonsGroupType.POLL_COMMENT -> NetworkReportReasonsGroupType.POLL_COMMENT
        ReportReasonsGroupType.STORE_REVIEW -> NetworkReportReasonsGroupType.REVIEW
        ReportReasonsGroupType.BOSS_STORE_REVIEW -> NetworkReportReasonsGroupType.STORE
    }
}