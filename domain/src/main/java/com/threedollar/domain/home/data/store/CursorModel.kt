package com.threedollar.domain.home.data.store

data class CursorModel(
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val totalCount: Int? = null,
)