package com.threedollar.data.my.mapper

import com.threedollar.domain.my.model.CategoryModel
import com.threedollar.domain.my.model.CursorModel
import com.threedollar.domain.my.model.PollOptionModel
import com.threedollar.domain.my.model.UserPollModel
import com.threedollar.domain.my.model.UserPollsModel
import com.threedollar.network.data.poll.response.GetMyPollListResponse

object UserPollsMapper {

    fun toDomainModel(response: GetMyPollListResponse): UserPollsModel {
        return UserPollsModel(
            contents = response.polls?.contents?.map { content ->
                val poll = content.poll
                UserPollModel(
                    pollId = poll.pollId ?: "",
                    title = poll.content?.title ?: "",
                    content = "", // Content not available as separate field
                    category = CategoryModel(
                        categoryId = poll.category?.categoryId ?: "",
                        name = poll.category?.title ?: "",
                        imageUrl = "" // ImageUrl not available
                    ),
                    isOwner = poll.isOwner ?: false,
                    isParticipated = poll.options?.any { it.choice?.selectedByMe == true } ?: false,
                    totalParticipantsCount = response.meta?.totalParticipantsCount ?: 0,
                    status = "", // Status not available
                    period = 0, // Period as days not available, only start/end datetime
                    createdAt = poll.createdAt ?: "",
                    expiredAt = poll.period?.endDateTime ?: "",
                    options = poll.options?.map { option ->
                        PollOptionModel(
                            optionId = option.optionId ?: "",
                            name = option.name ?: "",
                            photo = null, // Photo not available in this response
                            count = option.choice?.count ?: 0,
                            ratio = option.choice?.ratio ?: 0.0
                        )
                    } ?: emptyList()
                )
            } ?: emptyList(),
            cursor = response.polls?.cursor?.let { cursor ->
                CursorModel(
                    hasMore = cursor.hasMore ?: false,
                    nextCursor = cursor.nextCursor
                )
            }
        )
    }
}