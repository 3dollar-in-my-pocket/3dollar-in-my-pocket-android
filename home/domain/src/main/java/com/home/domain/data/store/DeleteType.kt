package com.home.domain.data.store

enum class DeleteType(val key: String) {
    NOSTORE("NOSTORE"),
    WRONGNOPOSITION("WRONGNOPOSITION"),
    OVERLAPSTORE("OVERLAPSTORE"),
    NONE("NONE")
}