package com.zion830.threedollars.ui.home.data

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
