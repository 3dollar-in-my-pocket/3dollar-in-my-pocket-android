package com.threedollar.network.data.poll.request


import com.google.gson.annotations.SerializedName

data class PollChoiceCreateApiRequest(
    @SerializedName("options")
    val options: List<Option>
) {
    data class Option(
        @SerializedName("optionId")
        val optionId: String
    )
}