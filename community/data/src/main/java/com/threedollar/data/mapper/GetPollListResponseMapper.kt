package com.threedollar.data.mapper

import com.threedollar.common.utils.toDefaultDouble
import com.threedollar.common.utils.toDefaultInt
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PollItem
import com.threedollar.network.data.poll.response.GetPollListResponse

object GetPollListResponseMapper {
    fun GetPollListResponse.Content?.toMapper(): PollItem {
        return PollItem(this?.meta.toMapper(), this?.poll.toMapper(), this?.pollWriter.toMapper())
    }

    private fun GetPollListResponse.Content.Meta?.toMapper(): PollItem.Meta {
        return PollItem.Meta(
            totalParticipantsCount = this?.totalParticipantsCount.toDefaultInt(),
            totalCommentsCount = this?.totalCommentsCount.toDefaultInt()
        )
    }

    private fun GetPollListResponse.Content.Poll?.toMapper(): PollItem.Poll {
        return PollItem.Poll(
            category = this?.category.toMapper(),
            content = this?.content.toMapper(),
            createdAt = this?.createdAt.orEmpty(),
            isOwner = this?.isOwner ?: false,
            options = this?.options.orEmpty().map { it.toMapper() },
            period = this?.period.toMapper(),
            pollId = this?.pollId.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty()
        )
    }

    private fun GetPollListResponse.Content.Poll.Category?.toMapper(): PollItem.Poll.Category {
        return PollItem.Poll.Category(categoryId = this?.categoryId.orEmpty(), content = this?.content.orEmpty(), title = this?.title.orEmpty())
    }

    private fun GetPollListResponse.Content.Poll.Content?.toMapper(): PollItem.Poll.Content {
        return PollItem.Poll.Content(title = this?.title.orEmpty())
    }

    private fun GetPollListResponse.Content.Poll.Option?.toMapper(): PollItem.Poll.Option {
        return PollItem.Poll.Option(choice = this?.choice.toMapper(), name = this?.name.orEmpty(), optionId = this?.optionId.orEmpty())
    }

    private fun GetPollListResponse.Content.Poll.Option.Choice?.toMapper(): PollItem.Poll.Option.Choice {
        return PollItem.Poll.Option.Choice(
            count = this?.count.toDefaultInt(),
            ratio = this?.ratio.toDefaultDouble(),
            selectedByMe = this?.selectedByMe ?: false
        )
    }

    private fun GetPollListResponse.Content.Poll.Period?.toMapper(): PollItem.Poll.Period {
        return PollItem.Poll.Period(endDateTime = this?.endDateTime.orEmpty(), startDateTime = this?.startDateTime.orEmpty())
    }

    private fun GetPollListResponse.Content.PollWriter?.toMapper(): PollItem.PollWriter {
        return PollItem.PollWriter(
            createdAt = this?.createdAt.orEmpty(),
            medal = this?.medal.toMapper(),
            medalsCount = this?.medalsCount.toDefaultInt(),
            name = this?.name.orEmpty(),
            socialType = this?.socialType.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty(),
            userId = this?.userId.toDefaultInt()
        )
    }

    private fun GetPollListResponse.Content.PollWriter.Medal?.toMapper(): PollItem.PollWriter.Medal {
        return PollItem.PollWriter.Medal(
            acquisition = this?.acquisition.toMapper(),
            createdAt = this?.createdAt.orEmpty(),
            disableIconUrl = this?.disableIconUrl.orEmpty(),
            iconUrl = this?.iconUrl.orEmpty(),
            introduction = this?.introduction.orEmpty(),
            medalId = this?.medalId.toDefaultInt(),
            name = this?.name.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty()
        )
    }

    private fun GetPollListResponse.Content.PollWriter.Medal.Acquisition?.toMapper(): PollItem.PollWriter.Medal.Acquisition {
        return PollItem.PollWriter.Medal.Acquisition(this?.description.orEmpty())
    }

    fun GetPollListResponse.Cursor?.toMapper(): Cursor {
        return Cursor(this?.nextCursor.orEmpty(), this?.hasMore ?: false)
    }
}