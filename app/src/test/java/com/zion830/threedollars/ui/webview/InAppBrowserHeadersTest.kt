package com.zion830.threedollars.ui.webview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InAppBrowserHeadersTest {

    private val userAgent = "4.36.0 (com.zion830.threedollars.dev); 36"

    // TH-1581 TC1
    @Test
    fun `TH1581_TC1_허용도메인이면_토큰과_앱UA를_함께_싣는다`() {
        // Given
        val url = "https://boong-village-dev.3dollarinmypocket.workers.dev/"

        // When
        val request = InAppBrowserHeaders.build(url, token = "token", userAgent = userAgent, isDebug = true)

        // Then
        assertEquals(InAppBrowserRequest(userAgent = userAgent, headers = mapOf("Authorization" to "token")), request)
    }

    // TH-1581 TC1
    @Test
    fun `TH1581_TC1_빌드환경에_맞는_게임도메인만_허용한다`() {
        // Given
        val debugUrl = "https://boong-village-dev.3dollarinmypocket.workers.dev/"
        val releaseUrl = "https://BOONG-VILLAGE.3dollarinmypocket.workers.dev./?ref=app"

        // When
        val debugTrusted = InAppBrowserHeaders.isTrustedHost(debugUrl, isDebug = true)
        val releaseTrusted = InAppBrowserHeaders.isTrustedHost(releaseUrl, isDebug = false)

        // Then
        assertTrue(debugTrusted)
        assertTrue(releaseTrusted)
    }

    // TH-1581 TC1
    @Test
    fun `TH1581_TC1_threedollars_도메인과_서브도메인은_빌드와_무관하게_허용한다`() {
        // Given
        val urls = listOf(
            "https://threedollars.co.kr/",
            "https://dev.threedollars.co.kr/event",
            "https://DEV.ThreeDollars.co.kr./event",
        )

        // When
        val debugTrusted = urls.map { InAppBrowserHeaders.isTrustedHost(it, isDebug = true) }
        val releaseTrusted = urls.map { InAppBrowserHeaders.isTrustedHost(it, isDebug = false) }
        val request = InAppBrowserHeaders.build(urls[1], token = "token", userAgent = userAgent, isDebug = true)

        // Then
        assertTrue(debugTrusted.all { it })
        assertTrue(releaseTrusted.all { it })
        assertEquals(InAppBrowserRequest(userAgent = userAgent, headers = mapOf("Authorization" to "token")), request)
    }

    // TH-1581 TC2
    @Test
    fun `TH1581_TC2_허용하지않은_도메인에는_Authorization과_앱UA를_싣지_않는다`() {
        // Given
        val urls = listOf(
            "https://httpbin.org/headers",
            "https://evilthreedollars.co.kr/",
            "https://threedollars.co.kr.evil.com/",
            "http://dev.threedollars.co.kr/",
            "https://boong-village-dev.3dollarinmypocket.workers.dev.evil.com/",
            "https://boong-village-dev.3dollarinmypocket.workers.dev@evil.com/",
            "http://boong-village-dev.3dollarinmypocket.workers.dev/",
            "https://boong-village.3dollarinmypocket.workers.dev/",
            "not a url",
        )

        // When
        val requests = urls.map { InAppBrowserHeaders.build(it, token = "token", userAgent = userAgent, isDebug = true) }

        // Then
        requests.forEach { assertEquals(InAppBrowserRequest(userAgent = null, headers = emptyMap()), it) }
        assertFalse(InAppBrowserHeaders.isTrustedHost("https://boong-village-dev.3dollarinmypocket.workers.dev/", isDebug = false))
    }

    // TH-1581 TC3
    @Test
    fun `TH1581_TC3_비로그인이면_허용도메인이어도_Authorization_없이_앱UA만_싣는다`() {
        // Given
        val url = "https://boong-village-dev.3dollarinmypocket.workers.dev/"

        // When
        val withoutToken = InAppBrowserHeaders.build(url, token = null, userAgent = userAgent, isDebug = true)
        val blankToken = InAppBrowserHeaders.build(url, token = " ", userAgent = userAgent, isDebug = true)

        // Then
        assertEquals(InAppBrowserRequest(userAgent = userAgent, headers = emptyMap()), withoutToken)
        assertEquals(InAppBrowserRequest(userAgent = userAgent, headers = emptyMap()), blankToken)
    }
}
