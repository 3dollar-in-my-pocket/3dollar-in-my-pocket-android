package com.threedollar.common.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object GlobalEvent {
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> get() = _logoutEvent

    fun triggerLogout() {
        _logoutEvent.value = true
    }

    fun resetLogoutEvent() {
        _logoutEvent.value = false
    }
}
