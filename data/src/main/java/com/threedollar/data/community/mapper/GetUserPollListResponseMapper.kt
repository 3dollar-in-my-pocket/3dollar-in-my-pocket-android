package com.threedollar.data.community.mapper

import com.threedollar.common.utils.toDefaultInt
import com.threedollar.domain.community.data.Cursor
import com.threedollar.domain.community.data.Meta
import com.threedollar.domain.community.data.UserPollItem
import com.threedollar.network.data.poll.response.GetUserPollListResponse


object GetUserPollListResponseMapper {
    fun GetUserPollListResponse.Poll?.toMapper(): UserPollItem {
        return UserPollItem(this?.contents.orEmpty().map { it.toMapper() }, this?.cursor.toMapper())
    }

    fun GetUserPollListResponse.Meta?.toMapper(): Meta {
        return Meta(this?.totalParticipantsCount.toDefaultInt(), 0)
    }

    private fun GetUserPollListResponse.Poll.Content?.toMapper(): UserPollItem.Poll {
        return UserPollItem.Poll(
            category = this?.category.toMapper(),
            content = this?.content.toMapper(),
            createdAt = this?.createdAt.orEmpty(),
            options = this?.options.orEmpty().map { it.toMapper() },
            period = this?.period.toMapper(),
            pollId = this?.pollId.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty()
        )
    }

    private fun GetUserPollListResponse.Poll.Content.Category?.toMapper(): UserPollItem.Poll.Category {
        return UserPollItem.Poll.Category(categoryId = this?.categoryId.orEmpty(), title = this?.title.orEmpty())
    }

    private fun GetUserPollListResponse.Poll.Content.Content?.toMapper(): UserPollItem.Poll.Content {
        return UserPollItem.Poll.Content(title = this?.title.orEmpty())
    }

    private fun GetUserPollListResponse.Poll.Content.Option?.toMapper(): UserPollItem.Poll.Option {
        return UserPollItem.Poll.Option(choice = this?.choice.toMapper(), name = this?.name.orEmpty(), optionId = this?.optionId.orEmpty())
    }

    private fun GetUserPollListResponse.Poll.Content.Option.Choice?.toMapper(): UserPollItem.Poll.Option.Choice {
        return UserPollItem.Poll.Option.Choice(
            count = this?.count.toDefaultInt(),
            ratio = this?.ratio.toDefaultInt(),
            selectedByMe = this?.selectedByMe ?: false
        )
    }

    private fun GetUserPollListResponse.Poll.Content.Period?.toMapper(): UserPollItem.Poll.Period {
        return UserPollItem.Poll.Period(endDateTime = this?.endDateTime.orEmpty(), startDateTime = this?.startDateTime.orEmpty())
    }

    private fun GetUserPollListResponse.Poll.Cursor?.toMapper(): Cursor {
        return Cursor(this?.nextCursor.orEmpty(), this?.hasMore ?: false)
    }
}