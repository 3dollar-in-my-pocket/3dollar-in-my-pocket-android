package com.threedollar.domain.community.data

data class CreatePolicy(
    val currentCount: Int, // 0
    val limitCount: Int,
    val pollRetentionDays: Int
)