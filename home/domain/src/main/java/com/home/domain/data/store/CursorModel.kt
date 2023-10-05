package com.home.domain.data.store

data class CursorModel(
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val totalCount: Int? = null,
)