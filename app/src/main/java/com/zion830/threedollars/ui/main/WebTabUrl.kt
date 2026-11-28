package com.zion830.threedollars.ui.main

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * 웹뷰 탭 URL. 서버가 상대 경로(`/events/point`)를 내려주면 빌드 타입별 웹 도메인을 붙인다.
 * 앱에 웹 도메인 설정값이 없어 iOS(TH-1649 `WebTabURL`)와 같은 값을 쓴다.
 */
object WebTabUrl {
    private const val DEBUG_BASE_URL = "https://echo.dev.threedollars.co.kr"
    private const val RELEASE_BASE_URL = "https://web.threedollars.co.kr"
    private const val SECURE_SCHEME = "https"

    fun baseUrl(isDebug: Boolean): String = if (isDebug) DEBUG_BASE_URL else RELEASE_BASE_URL

    /** https 절대 URL 은 그대로, `/` 로 시작하는 상대 경로는 [baseUrl]을 붙인다. 그 외(http, 빈 값 등)는 null. */
    fun resolve(link: String, baseUrl: String): String? {
        val trimmed = link.trim()
        if (trimmed.isEmpty()) return null

        trimmed.toHttpUrlOrNull()?.let { url ->
            return if (url.scheme == SECURE_SCHEME) trimmed else null
        }
        if (!trimmed.startsWith("/")) return null
        return baseUrl.trimEnd('/') + trimmed
    }
}
