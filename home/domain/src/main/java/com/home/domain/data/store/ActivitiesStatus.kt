package com.home.domain.data.store

enum class ActivitiesStatus {
    RECENT_ACTIVITY, NO_ACTIVITY, OTHER;

    companion object {
        fun from(value: String?): ActivitiesStatus =
            values().find { it.name == value } ?: OTHER
    }
}