package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class GetPollListResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("message")
    var message: String? = null, // string
    @SerializedName("resultCode")
    var resultCode: String? = null // string
) {
    data class Data(
        @SerializedName("contents")
        var contents: List<Content?>? = null,
        @SerializedName("cursor")
        var cursor: Cursor? = null
    ) {
        data class Content(
            @SerializedName("meta")
            var meta: Meta? = null,
            @SerializedName("poll")
            var poll: Poll? = null,
            @SerializedName("pollWriter")
            var pollWriter: PollWriter? = null
        ) {
            data class Meta(
                @SerializedName("totalCommentsCount")
                var totalCommentsCount: Int? = null, // 0
                @SerializedName("totalParticipantsCount")
                var totalParticipantsCount: Int? = null // 0
            )

            data class Poll(
                @SerializedName("category")
                var category: Category? = null,
                @SerializedName("content")
                var content: Content? = null,
                @SerializedName("createdAt")
                var createdAt: String? = null, // 2023-09-13T19:58:44
                @SerializedName("options")
                var options: List<Option?>? = null,
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

            data class PollWriter(
                @SerializedName("createdAt")
                var createdAt: String? = null, // 2023-09-13T19:58:44
                @SerializedName("marketingConsent")
                var marketingConsent: String? = null, // APPROVE
                @SerializedName("medal")
                var medal: Medal? = null,
                @SerializedName("medalsCount")
                var medalsCount: Int? = null, // 0
                @SerializedName("name")
                var name: String? = null, // string
                @SerializedName("socialType")
                var socialType: String? = null, // KAKAO
                @SerializedName("updatedAt")
                var updatedAt: String? = null, // 2023-09-13T19:58:44
                @SerializedName("userId")
                var userId: Int? = null // 0
            ) {
                data class Medal(
                    @SerializedName("acquisition")
                    var acquisition: Acquisition? = null,
                    @SerializedName("createdAt")
                    var createdAt: String? = null, // 2023-09-13T19:58:44
                    @SerializedName("disableIconUrl")
                    var disableIconUrl: String? = null, // string
                    @SerializedName("iconUrl")
                    var iconUrl: String? = null, // string
                    @SerializedName("introduction")
                    var introduction: String? = null, // string
                    @SerializedName("medalId")
                    var medalId: Int? = null, // 0
                    @SerializedName("name")
                    var name: String? = null, // string
                    @SerializedName("updatedAt")
                    var updatedAt: String? = null // 2023-09-13T19:58:44
                ) {
                    data class Acquisition(
                        @SerializedName("description")
                        var description: String? = null // string
                    )
                }
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