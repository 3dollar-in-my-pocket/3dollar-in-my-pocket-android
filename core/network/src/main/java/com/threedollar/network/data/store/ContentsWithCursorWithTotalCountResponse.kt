package com.threedollar.network.data.store

data class ContentsWithCursorWithTotalCountResponse<T>(
    val contents: List<T> = listOf(),
    val cursor: Cursor = Cursor(),
)