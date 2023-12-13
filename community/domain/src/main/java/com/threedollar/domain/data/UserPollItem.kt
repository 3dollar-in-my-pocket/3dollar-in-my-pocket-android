package com.threedollar.domain.data

data class UserPollItem(
    val polls: List<Poll>,
    val cursor: Cursor
) {
    data class Poll(
        val category: Category,
        val content: Content,
        val createdAt: String, // 2023-10-07T03:50:53
        val options: List<Option>,
        val period: Period,
        val pollId: String, // string
        val updatedAt: String // 2023-10-07T03:50:53
    ) {
        data class Category(
            val categoryId: String, // string
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
                val ratio: Int, // 0
                val selectedByMe: Boolean // true
            )
        }

        data class Period(
            val endDateTime: String, // 2023-10-07T03:50:53
            val startDateTime: String // 2023-10-07T03:50:53
        )
    }
}