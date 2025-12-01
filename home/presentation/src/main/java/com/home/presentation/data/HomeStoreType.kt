package com.home.presentation.data

enum class HomeStoreType {
    ALL,
    USER_STORE,
    BOSS_STORE
}

fun HomeStoreType.toArray(): Array<String>? {
    return when (this) {
        HomeStoreType.ALL -> null
        else -> arrayOf(this.name)
    }
}
