package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollCategoryApiResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("message")
    var message: String? = null, // string
    @SerializedName("resultCode")
    var resultCode: String? = null // string
) {
    data class Data(
        @SerializedName("categories")
        var categories: List<Category?>? = null
    ) {
        data class Category(
            @SerializedName("categoryId")
            var categoryId: String? = null, // string
            @SerializedName("title")
            var title: String? = null // string
        )
    }
}