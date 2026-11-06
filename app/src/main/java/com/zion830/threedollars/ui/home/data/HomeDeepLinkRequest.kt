package com.zion830.threedollars.ui.home.data

internal sealed interface HomeDeepLinkRequest {
    data class Screen(val preset: String, val tabId: String?) : HomeDeepLinkRequest
    data class Tabs(val tabId: String) : HomeDeepLinkRequest

    companion object {
        fun from(preset: String?, tabId: String?): HomeDeepLinkRequest? = when {
            preset != null -> Screen(preset = preset, tabId = tabId)
            tabId != null -> Tabs(tabId = tabId)
            else -> null
        }
    }
}
