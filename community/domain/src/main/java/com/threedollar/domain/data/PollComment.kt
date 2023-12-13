package com.threedollar.domain.data

data class PollComment(
    val current: Current
) {
    data class Current(
        val comment: Comment,
        val commentReport: CommentReport,
        val commentWriter: CommentWriter,
        val poll: Poll
    ) {
        data class Comment(
            val commentId: String, // string
            val content: String, // string
            val createdAt: String, // 2023-10-14T15:13:29
            val isOwner: Boolean, // true
            val status: String, // ACTIVE
            val updatedAt: String // 2023-10-14T15:13:29
        )

        data class CommentReport(
            val reportedByMe: Boolean // true
        )

        data class CommentWriter(
            val medal: Medal,
            val name: String, // string
            val socialType: String, // KAKAO
            val userId: Int // 0
        ) {
            data class Medal(
                val acquisition: Acquisition,
                val createdAt: String, // 2023-10-14T15:13:29
                val disableIconUrl: String, // string
                val iconUrl: String, // string
                val introduction: String, // string
                val medalId: Int, // 0
                val name: String, // string
                val updatedAt: String // 2023-10-14T15:13:29
            ) {
                data class Acquisition(
                    val description: String // string
                )
            }
        }

        data class Poll(
            val isWriter: Boolean, // true
            val selectedOptions: List<SelectedOption>
        ) {
            data class SelectedOption(
                val name: String, // string
                val optionId: String // string
            )
        }
    }
}