package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class GetPollCommentListResponse(
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
            @SerializedName("current")
            var current: Current? = null
        ) {
            data class Current(
                @SerializedName("comment")
                var comment: Comment? = null,
                @SerializedName("commentReport")
                var commentReport: CommentReport? = null,
                @SerializedName("commentWriter")
                var commentWriter: CommentWriter? = null
            ) {
                data class Comment(
                    @SerializedName("commentId")
                    var commentId: String? = null, // string
                    @SerializedName("content")
                    var content: String? = null, // string
                    @SerializedName("createdAt")
                    var createdAt: String? = null, // 2023-09-13T19:58:44
                    @SerializedName("status")
                    var status: String? = null, // ACTIVE
                    @SerializedName("updatedAt")
                    var updatedAt: String? = null // 2023-09-13T19:58:44
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
        }

        data class Cursor(
            @SerializedName("hasMore")
            var hasMore: Boolean? = null, // true
            @SerializedName("nextCursor")
            var nextCursor: String? = null // string
        )
    }
}