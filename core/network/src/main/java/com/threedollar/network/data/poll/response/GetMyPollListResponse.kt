package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class GetMyPollListResponse(
    @SerializedName("meta")
    var meta: Meta? = null,
    @SerializedName("polls")
    var polls: Polls? = null
) {
    data class Meta(
        @SerializedName("totalParticipantsCount")
        var totalParticipantsCount: Int? = null // 0
    )

    data class Polls(
        @SerializedName("contents")
        var contents: List<Content>? = null,
        @SerializedName("cursor")
        var cursor: Cursor? = null
    ) {
        data class Content(
            @SerializedName("poll")
            var poll: Poll = Poll()
        ) {
            data class Poll(
                @SerializedName("category")
                var category: Category? = null,
                @SerializedName("content")
                var content: ContentDetail? = null,
                @SerializedName("createdAt")
                var createdAt: String? = null, // 2023-09-13T19:58:44
                @SerializedName("updatedAt")
                var updatedAt: String? = null, // 2023-09-13T19:58:44
                @SerializedName("period")
                var period: Period? = null,
                @SerializedName("options")
                var options: List<Option>? = null,
                @SerializedName("isOwner")
                var isOwner: Boolean? = null,
                @SerializedName("pollId")
                var pollId: String? = null // string
            ) {
                data class Category(
                    @SerializedName("categoryId")
                    var categoryId: String? = null, // string
                    @SerializedName("title")
                    var title: String? = null, // string
                    @SerializedName("content")
                    var content: String? = null // string
                )

                data class ContentDetail(
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
                        var ratio: Double? = null, // 0
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
        }

        data class Cursor(
            @SerializedName("hasMore")
            var hasMore: Boolean? = null, // true
            @SerializedName("nextCursor")
            var nextCursor: String? = null // string
        )
    }
}