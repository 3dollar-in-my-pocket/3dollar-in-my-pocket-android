package com.threedollar.data.mapper

import com.threedollar.common.utils.toDefaultInt
import com.threedollar.data.mapper.GetPollListResponseMapper.toMapper
import com.threedollar.data.mapper.GetPollResponseMapper.toMapper
import com.threedollar.data.mapper.GetUserPollListResponseMapper.toMapper
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import zion830.com.common.base.BaseResponse

fun PollCreateApiResponse?.toMapper(): PollId {
    return PollId(this?.id.orEmpty())
}

fun GetPollResponse?.toMapper(): PollItem {
    return PollItem(
        meta = this?.meta.toMapper(), poll = this?.poll.toMapper(), pollWriter = this?.pollWriter.toMapper()
    )
}

fun BaseResponse<String>.toMapper(): DefaultResponse {
    return DefaultResponse(data = data.orEmpty(), message = message.orEmpty(), resultCode = resultCode.orEmpty())
}

fun PollCategoryApiResponse?.toMapper(): List<Category> {
    return this?.categories.orEmpty().map { it.toMapper() }
}

fun PollCategoryApiResponse.Category.toMapper(): Category {
    return Category(categoryId.orEmpty(), title.orEmpty())
}

fun GetPollListResponse?.toMapper(): PollList {
    return PollList(pollItems = this?.contents.orEmpty().map { it.toMapper() }, cursor = this?.cursor.toMapper())
}

fun PollPolicyApiResponse?.toMapper(): CreatePolicy {
    return CreatePolicy(this?.createPolicy?.currentCount.toDefaultInt(), this?.createPolicy?.limitCount.toDefaultInt())
}

fun GetUserPollListResponse?.toMapper(): UserPollItemList {
    return UserPollItemList(this?.poll.toMapper(), this?.meta.toMapper())
}

fun PollCommentCreateApiResponse?.toMapper(): CommentId {
    return CommentId(this?.id.orEmpty())
}

fun GetPollCommentListResponse?.toMapper(): PollCommentList {
    return this.toMapper()
}