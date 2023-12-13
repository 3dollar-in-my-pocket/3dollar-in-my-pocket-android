package com.threedollar.network.data.poll.request


import com.google.gson.annotations.SerializedName

data class PollCreateApiRequest(
    @SerializedName("categoryId")
    val categoryId: String, // string
    @SerializedName("options")
    val options: List<Option?>,
    @SerializedName("startDateTime")
    val startDateTime: String, // 2023-09-13T19:58:44
    @SerializedName("title")
    val title: String // string
) {
    data class Option(
        @SerializedName("name")
        val name: String // string
    )
}