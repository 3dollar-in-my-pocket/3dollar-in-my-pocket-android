package com.login.data.mapper

import com.login.domain.model.FeedbackTypeModel
import com.threedollar.network.data.feedback.FeedbackTypeResponse

object FeedbackTypeMapper {

    fun toDomainModel(response: FeedbackTypeResponse): FeedbackTypeModel {
        return FeedbackTypeModel(
            feedbackType = response.feedbackType,
            description = response.description,
            emoji = response.emoji
        )
    }

    fun toDomainModelList(responses: List<FeedbackTypeResponse>): List<FeedbackTypeModel> {
        return responses.map { toDomainModel(it) }
    }
}