package com.threedollar.domain.community.model

data class PollCreateModel(
    val title: String,
    val content: String,
    val categoryId: String,
    val options: List<String>,
    val period: Int,
    val isAllowMultipleChoice: Boolean
)