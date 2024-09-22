package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class GetUserPollListResponse(
    @SerializedName("meta")
    var meta: Meta? = null,
    @SerializedName("poll")
    var poll: Poll? = null
) {
    data class Meta(
        @SerializedName("totalParticipantsCount")
        var totalParticipantsCount: Int? = null // 0
    )

    data class Poll(
        @SerializedName("contents")
        var contents: List<Content>? = null,
        @SerializedName("cursor")
        var cursor: Cursor? = null
    ) {
        data class Content(
            @SerializedName("category")
            var category: Category? = null,
            @SerializedName("content")
            var content: Content? = null,
            @SerializedName("createdAt")
            var createdAt: String? = null, // 2023-09-13T19:58:44
            @SerializedName("isOwner")
            var isOwner: Boolean? = null,
            @SerializedName("options")
            var options: List<Option>? = null,
            @SerializedName("period")
            var period: Period? = null,
            @SerializedName("pollId")
            var pollId: String? = null, // string
            @SerializedName("updatedAt")
            var updatedAt: String? = null // 2023-09-13T19:58:44
        ) {
            data class Category(
                @SerializedName("categoryId")
                var categoryId: String? = null, // string
                @SerializedName("title")
                var title: String? = null // string
            )

            data class Content(
                @SerializedName("title")
                var title: String? = null // string
            )

            data class Option(
                @SerializedName("choice")
                var choice: Choice? = null,
                @SerializedName("name")
                var name: String? = null, // string
                @SerializedName("optionId")
                var optionId: String? = null // string
            ) {
                data class Choice(
                    @SerializedName("count")
                    var count: Int? = null, // 0
                    @SerializedName("ratio")
                    var ratio: Int? = null, // 0
                    @SerializedName("selectedByMe")
                    var selectedByMe: Boolean? = null // true
                )
            }

            data class Period(
                @SerializedName("endDateTime")
                var endDateTime: String? = null, // 2023-09-13T19:58:44
                @SerializedName("startDateTime")
                var startDateTime: String? = null // 2023-09-13T19:58:44
            )
        }

        data class Cursor(
            @SerializedName("hasMore")
            var hasMore: Boolean? = null, // true
            @SerializedName("nextCursor")
            var nextCursor: String? = null // string
        )
    }
}