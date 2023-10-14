package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class GetPollCommentListResponse(
    @SerializedName("contents")
    var contents: List<Content>? = null,
    @SerializedName("cursor")
    var cursor: Cursor? = null
) {
    data class Content(
        @SerializedName("current")
        var current: Current? = null
    ) {
        data class Current(
            @SerializedName("comment")
            var comment: Comment? = null,
            @SerializedName("commentReport")
            var commentReport: CommentReport? = null,
            @SerializedName("commentWriter")
            var commentWriter: CommentWriter? = null,
            @SerializedName("poll")
            var poll: Poll? = null
        ) {
            data class Comment(
                @SerializedName("commentId")
                var commentId: String? = null, // string
                @SerializedName("content")
                var content: String? = null, // string
                @SerializedName("createdAt")
                var createdAt: String? = null, // 2023-10-14T15:13:29
                @SerializedName("isOwner")
                var isOwner: Boolean? = null, // true
                @SerializedName("status")
                var status: String? = null, // ACTIVE
                @SerializedName("updatedAt")
                var updatedAt: String? = null // 2023-10-14T15:13:29
            )

            data class CommentReport(
                @SerializedName("reportedByMe")
                var reportedByMe: Boolean? = null // true
            )

            data class CommentWriter(
                @SerializedName("medal")
                var medal: Medal? = null,
                @SerializedName("name")
                var name: String? = null, // string
                @SerializedName("socialType")
                var socialType: String? = null, // KAKAO
                @SerializedName("userId")
                var userId: Int? = null // 0
            ) {
                data class Medal(
                    @SerializedName("acquisition")
                    var acquisition: Acquisition? = null,
                    @SerializedName("createdAt")
                    var createdAt: String? = null, // 2023-10-14T15:13:29
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
                    var updatedAt: String? = null // 2023-10-14T15:13:29
                ) {
                    data class Acquisition(
                        @SerializedName("description")
                        var description: String? = null // string
                    )
                }
            }

            data class Poll(
                @SerializedName("isWriter")
                var isWriter: Boolean? = null, // true
                @SerializedName("selectedOptions")
                var selectedOptions: List<SelectedOption?>? = null
            ) {
                data class SelectedOption(
                    @SerializedName("name")
                    var name: String? = null, // string
                    @SerializedName("optionId")
                    var optionId: String? = null // string
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