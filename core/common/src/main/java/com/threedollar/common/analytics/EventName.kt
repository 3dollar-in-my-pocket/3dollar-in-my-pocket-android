package com.threedollar.common.analytics

/**
 * Type-safe event names
 * Matches iOS EventName enum
 */
enum class EventName(val value: String) {
    Click("click"),
    IMPRESSION("impression"),
    HOME_REOPEN("home_reopen"),
    EDIT_STORE_COMPLETE("edit_store_complete");

    override fun toString(): String = value
}
