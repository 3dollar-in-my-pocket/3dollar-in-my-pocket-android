package com.threedollar.common.analytics

/**
 * Type-safe event names
 * Matches iOS EventName enum
 */
enum class EventName(val value: String) {
    Click("click"),
    HOME_REOPEN("home_reopen");

    override fun toString(): String = value
}
