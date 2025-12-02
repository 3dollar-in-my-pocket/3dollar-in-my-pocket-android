package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.user.Medal

data class ContentListCommentResponse(
    @SerializedName("contents")
    val contents: List<CommentItemResponse> = listOf()
)

data class CommentItemResponse(
    @SerializedName("commentId")
    val commentId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("writer")
    val writer: WriterResponse,

    @SerializedName("isOwner")
    val isOwner: Boolean,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class WriterResponse(
    @SerializedName("writerId")
    val writerId: String,

    @SerializedName("writerType")
    val writerType: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("additionalInfo")
    val additionalInfo: AdditionalInfoResponse?
)

data class AdditionalInfoResponse(
    @SerializedName("type")
    val type: String,

    @SerializedName("medal")
    val medal: Medal
)
