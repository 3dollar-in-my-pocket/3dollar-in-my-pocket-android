package com.threedollar.presentation.utils

import com.threedollar.domain.data.PollItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun calculatePercentages(a: Int, b: Int): Pair<Int, Int> {
    val total = a + b
    if (total == 0) return Pair(0, 0)

    val percentageA = (a * 100) / total
    val percentageB = (b * 100) / total

    return Pair(percentageA, percentageB)
}

fun getDeadlineString(inputDate: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val deadlineCalendar = Calendar.getInstance().apply {
        time = dateFormat.parse(inputDate)
    }

    val currentCalendar = Calendar.getInstance()

    val daysUntilDeadline = kotlin.math.ceil(
        (deadlineCalendar.timeInMillis - currentCalendar.timeInMillis) / (1000.0 * 60 * 60 * 24)
    ).toInt()

    if (daysUntilDeadline == 0) {
        return "오늘 마감"
    } else if (daysUntilDeadline < 0) {
        return "마감"
    }

    return "D-$daysUntilDeadline"
}

fun hasVotingPeriodEnded(inputDate: String): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val deadlineCalendar = Calendar.getInstance().apply {
        time = dateFormat.parse(inputDate)
    }

    val currentCalendar = Calendar.getInstance()

    val daysUntilDeadline = kotlin.math.ceil(
        (deadlineCalendar.timeInMillis - currentCalendar.timeInMillis) / (1000.0 * 60 * 60 * 24)
    ).toInt()
    return daysUntilDeadline < 0
}

fun selectedPoll(pollItem: PollItem,optionId:String):PollItem{
    var firstChoice = pollItem.poll.options[0]
    var secondChoice = pollItem.poll.options[1]
    val isSelectedOption = pollItem.poll.options.find { it.choice.selectedByMe } == null
    val editFirstCount = if (firstChoice.optionId == optionId) 1 else if(isSelectedOption) 0 else -1
    val editSecondCount = if (secondChoice.optionId == optionId) 1 else if(isSelectedOption) 0 else -1
    firstChoice = firstChoice.copy(
        choice = firstChoice.choice.copy(
            selectedByMe = firstChoice.optionId == optionId,
            count = firstChoice.choice.count + editFirstCount
        )
    )
    secondChoice = secondChoice.copy(
        choice = secondChoice.choice.copy(
            selectedByMe = secondChoice.optionId == optionId,
            count = secondChoice.choice.count + editSecondCount
        )
    )
    return pollItem.copy(poll = pollItem.poll.copy(options = listOf(firstChoice, secondChoice)))
}