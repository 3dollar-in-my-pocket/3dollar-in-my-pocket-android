package com.zion830.threedollars.ui.webview

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object InAppBrowserHeaders {
    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val SECURE_SCHEME = "https"

    private const val TRUSTED_DOMAIN = "threedollars.co.kr"
    private val TRUSTED_HOSTS = setOf("boong-village.3dollarinmypocket.workers.dev")
    private val TRUSTED_DEV_HOSTS = setOf("boong-village-dev.3dollarinmypocket.workers.dev")

    fun build(url: String, token: String?, userAgent: String, isDebug: Boolean): InAppBrowserRequest {
        if (!isTrustedHost(url, isDebug)) return InAppBrowserRequest(userAgent = null, headers = emptyMap())
        val headers = if (token.isNullOrBlank()) emptyMap() else mapOf(HEADER_AUTHORIZATION to token)
        return InAppBrowserRequest(userAgent = userAgent, headers = headers)
    }

    fun isTrustedHost(url: String, isDebug: Boolean): Boolean {
        val httpUrl = url.trim().toHttpUrlOrNull() ?: return false
        if (httpUrl.scheme != SECURE_SCHEME) return false
        val host = httpUrl.host.trimEnd('.').lowercase()
        if (host == TRUSTED_DOMAIN || host.endsWith(".$TRUSTED_DOMAIN")) return true
        return host in if (isDebug) TRUSTED_DEV_HOSTS else TRUSTED_HOSTS
    }
}

data class InAppBrowserRequest(
    val userAgent: String?,
    val headers: Map<String, String>,
)
