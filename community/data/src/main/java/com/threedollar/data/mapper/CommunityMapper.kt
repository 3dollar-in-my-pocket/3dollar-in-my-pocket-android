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
import com.threedollar.network.data.Reason
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse


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