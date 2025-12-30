package com.threedollar.data.community.mapper

import com.threedollar.common.utils.toDefaultDouble
import com.threedollar.common.utils.toDefaultInt
import com.threedollar.domain.community.data.PollItem
import com.threedollar.network.data.poll.response.GetPollResponse

object GetPollResponseMapper {
    fun GetPollResponse.Meta?.toMapper(): PollItem.Meta {
        return PollItem.Meta(
            totalParticipantsCount = this?.totalParticipantsCount.toDefaultInt(),
            totalCommentsCount = this?.totalCommentsCount.toDefaultInt()
        )
    }

    fun GetPollResponse.Poll?.toMapper(): PollItem.Poll {
        return PollItem.Poll(
            category = this?.category.toMapper(),
            content = this?.content.toMapper(),
            createdAt = this?.createdAt.orEmpty(),
            options = this?.options.orEmpty().map { it.toMapper() },
            period = this?.period.toMapper(),
            pollId = this?.pollId.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty(),
            isOwner = this?.isOwner ?: false
        )
    }

    private fun GetPollResponse.Poll.Category?.toMapper(): PollItem.Poll.Category {
        return PollItem.Poll.Category(categoryId = this?.categoryId.orEmpty(), title = this?.title.orEmpty(), content = this?.content.orEmpty())
    }

    private fun GetPollResponse.Poll.Content?.toMapper(): PollItem.Poll.Content {
        return PollItem.Poll.Content(title = this?.title.orEmpty())
    }

    private fun GetPollResponse.Poll.Option?.toMapper(): PollItem.Poll.Option {
        return PollItem.Poll.Option(choice = this?.choice.toMapper(), name = this?.name.orEmpty(), optionId = this?.optionId.orEmpty())
    }

    private fun GetPollResponse.Poll.Option.Choice?.toMapper(): PollItem.Poll.Option.Choice {
        return PollItem.Poll.Option.Choice(
            count = this?.count.toDefaultInt(),
            ratio = this?.ratio.toDefaultDouble(),
            selectedByMe = this?.selectedByMe ?: false
        )
    }

    private fun GetPollResponse.Poll.Period?.toMapper(): PollItem.Poll.Period {
        return PollItem.Poll.Period(endDateTime = this?.endDateTime.orEmpty(), startDateTime = this?.startDateTime.orEmpty())
    }

    fun GetPollResponse.PollWriter?.toMapper(): PollItem.PollWriter {
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

    private fun GetPollResponse.PollWriter.Medal?.toMapper(): PollItem.PollWriter.Medal {
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

    private fun GetPollResponse.PollWriter.Medal.Acquisition?.toMapper(): PollItem.PollWriter.Medal.Acquisition {
        return PollItem.PollWriter.Medal.Acquisition(this?.description.orEmpty())
    }
}