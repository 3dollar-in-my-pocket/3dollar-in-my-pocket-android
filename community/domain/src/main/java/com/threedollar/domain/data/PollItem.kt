package com.threedollar.domain.data

data class PollItem(
    val meta: Meta,
    val poll: Poll,
    val pollWriter: PollWriter
) {
    data class Meta(
        val totalCommentsCount: Int, // 0
        val totalParticipantsCount: Int // 0
    )

    data class Poll(
        val category: Category,
        val content: Content,
        val createdAt: String, // 2023-10-14T15:13:29
        val isOwner: Boolean, // true
        val options: List<Option>,
        val period: Period,
        val pollId: String, // string
        val updatedAt: String // 2023-10-14T15:13:29
    ) {
        data class Category(
            val categoryId: String, // string
            val content: String, // string
            val title: String // string
        )

        data class Content(
            val title: String // string
        )

        data class Option(
            val choice: Choice,
            val name: String, // string
            val optionId: String // string
        ) {
            data class Choice(
                val count: Int, // 0
                val ratio: Double, // 0
                val selectedByMe: Boolean // true
            )
        }

        data class Period(
            val endDateTime: String, // 2023-10-14T15:13:29
            val startDateTime: String // 2023-10-14T15:13:29
        )
    }

    data class PollWriter(
        val createdAt: String, // 2023-10-14T15:13:29
        val medal: Medal,
        val medalsCount: Int, // 0
        val name: String, // string
        val socialType: String, // KAKAO
        val updatedAt: String, // 2023-10-14T15:13:29
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
}