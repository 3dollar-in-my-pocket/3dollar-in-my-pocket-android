package com.threedollar.domain.my.model

data class UserPollsModel(
    val contents: List<UserPollModel> = emptyList(),
    val cursor: CursorModel? = null
)

data class UserPollModel(
    val pollId: String,
    val title: String,
    val content: String,
    val category: CategoryModel,
    val isOwner: Boolean,
    val isParticipated: Boolean,
    val totalParticipantsCount: Int,
    val status: String,
    val period: Int,
    val createdAt: String,
    val expiredAt: String,
    val options: List<PollOptionModel>
)

data class PollOptionModel(
    val optionId: String,
    val name: String,
    val photo: PhotoModel?,
    val count: Int,
    val ratio: Double
)

data class PhotoModel(
    val photoId: String,
    val url: String
)