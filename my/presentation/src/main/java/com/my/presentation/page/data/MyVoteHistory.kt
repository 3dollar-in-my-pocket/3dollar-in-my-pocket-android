package com.my.presentation.page.data

import com.threedollar.network.data.poll.response.GetMyPollListResponse
import java.text.SimpleDateFormat
import java.util.Locale


data class MyVoteHistory(
    val title: String,
    val date: String,
    val options: List<Option>
) {
    data class Option(
        val name: String,
        val choiceCount: Int,
        val ratio: Int,
        val isTopVote: Boolean
    )
}

fun GetMyPollListResponse.Polls.Content.Poll.toMyVoteHistory(): MyVoteHistory {
    return MyVoteHistory(
        title = this.content?.title.orEmpty(),
        date = this.createdAt.orEmpty().convertUpdateAt(),
        options = this.options.orEmpty().map {
            MyVoteHistory.Option(
                name = it.name.orEmpty(),
                choiceCount = it.choice?.count ?: 0,
                ratio = it.choice?.ratio?.toInt() ?: 0,
                isTopVote = it.choice?.selectedByMe ?: false
            )
        }
    )
}

fun String.convertUpdateAt(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        val formattedDateStr = outputFormat.format(date)
        formattedDateStr
    } catch (e: Exception) {
        ""
    }
}