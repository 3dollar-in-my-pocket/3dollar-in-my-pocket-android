package com.my.presentation.page.data

import com.my.domain.model.UserPollModel
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

fun UserPollModel.toMyVoteHistory(): MyVoteHistory {
    return MyVoteHistory(
        title = this.title,
        date = this.createdAt.convertUpdateAt(),
        options = this.options.map {
            MyVoteHistory.Option(
                name = it.name,
                choiceCount = it.count,
                ratio = (it.ratio * 100).toInt(),
                isTopVote = false // TODO: Domain 모델에 isSelected 필드 추가 필요
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