package com.threedollar.domain.home.data.store

import com.threedollar.domain.home.data.user.MedalModel

data class CommentModel(
    val commentId: String,
    val content: String,
    val status: CommentStatus,
    val writer: CommentWriter,
    val isOwner: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

enum class CommentStatus {
    ACTIVE, INACTIVE, DELETED;

    companion object {
        fun from(value: String): CommentStatus =
            values().find { it.name == value } ?: ACTIVE
    }
}

data class CommentWriter(
    val writerId: String,
    val writerType: WriterType,
    val name: String,
    val additionalInfo: AdditionalInfo
)

enum class WriterType {
    STORE, USER, OTHER;

    companion object {
        fun from(value: String): WriterType =
            values().find { it.name == value } ?: OTHER
    }
}

data class AdditionalInfo(
    val type: String = "",
    val medal: MedalModel = MedalModel(),
)