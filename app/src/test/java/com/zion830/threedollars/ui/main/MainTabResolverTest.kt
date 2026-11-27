package com.zion830.threedollars.ui.main

import com.google.gson.Gson
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.data.screen.asTabModels
import com.threedollar.network.data.screen.BottomNavigationResponse
import com.zion830.threedollars.ui.webview.InAppBrowserHeaders
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MainTabResolverTest {

    private val debugBaseUrl = WebTabUrl.baseUrl(isDebug = true)

    /** 개발 서버에서 캡처한 하단 탭 응답을 실제 DTO·mapper 로 읽는다. */
    private fun serverTabs(name: String): List<BottomNavigationTabModel> = Gson().fromJson(
        requireNotNull(javaClass.classLoader?.getResource("bottomnavigation/$name")).readText(),
        BottomNavigationResponse::class.java,
    ).asTabModels()

    private fun resolve(name: String) = MainTabResolver.resolve(serverTabs(name), debugBaseUrl)

    // TH-1650 TC1
    @Test
    fun `TH1650_TC1_응답순서대로_라벨_아이콘_선택색이_탭에_들어간다`() {
        // Given
        val response = serverTabs("BottomNavigation.json")

        // When
        val tabs = MainTabResolver.resolve(response, debugBaseUrl)

        // Then
        assertEquals(listOf("home", "store_create", "event", "community", "my"), tabs.map { it.id })
        assertEquals(listOf("홈", "가게 제보", "이벤트", "커뮤니티", "마이페이지"), tabs.map { it.label })
        assertEquals(response.map { it.iconUrl }, tabs.map { it.iconUrl })
        assertEquals(listOf("#FF5C43"), tabs.map { it.selectedTintColor }.distinct())
    }

    // TH-1650 TC2
    @Test
    fun `TH1650_TC2_APP_DEFINED_id는_기존화면에_매핑되고_딥링크탭만_선택하지_않는다`() {
        // Given
        val response = "BottomNavigation.json"

        // When
        val tabs = resolve(response).associateBy { it.id }

        // Then
        assertEquals(MainTabDestination.Home, tabs.getValue("home").destination)
        assertEquals(MainTabDestination.StoreCreate, tabs.getValue("store_create").destination)
        assertEquals(MainTabDestination.Community, tabs.getValue("community").destination)
        assertEquals(MainTabDestination.My, tabs.getValue("my").destination)
        assertTrue(tabs.values.all { it.isSelectable })
    }

    // TH-1650 TC2
    @Test
    fun `TH1650_TC2_type없는_구응답도_아는_id는_기존화면_WEB은_웹뷰탭이다`() {
        // Given
        val response = "BottomNavigationWithoutType.json"

        // When
        val tabs = resolve(response)

        // Then
        assertEquals(
            listOf(
                MainTabDestination.Home,
                MainTabDestination.StoreCreate,
                MainTabDestination.Web("https://echo.dev.threedollars.co.kr/events/point"),
                MainTabDestination.Community,
                MainTabDestination.My,
            ),
            tabs.map { it.destination },
        )
    }

    // TH-1650 TC3
    @Test
    fun `TH1650_TC3_DYNAMIC_WEB탭은_웹뷰탭이고_상대경로면_빌드별_웹도메인을_붙인다`() {
        // Given
        val response = "BottomNavigationWithUnknownTabs.json"

        // When
        val debugEvent = MainTabResolver.resolve(serverTabs(response), WebTabUrl.baseUrl(isDebug = true)).first { it.id == "event" }
        val releaseEvent = MainTabResolver.resolve(serverTabs(response), WebTabUrl.baseUrl(isDebug = false)).first { it.id == "event" }

        // Then
        assertEquals(MainTabDestination.Web("https://echo.dev.threedollars.co.kr/events/point"), debugEvent.destination)
        assertEquals(MainTabDestination.Web("https://web.threedollars.co.kr/events/point"), releaseEvent.destination)
    }

    // TH-1650 TC3
    @Test
    fun `TH1650_TC3_웹탭URL은_https절대경로와_슬래시상대경로만_허용한다`() {
        // Given
        val baseUrl = "https://web.threedollars.co.kr/"

        // When
        val absolute = WebTabUrl.resolve(" https://echo.dev.threedollars.co.kr/events/point?ref=tab ", baseUrl)
        val relative = WebTabUrl.resolve("/events/point", baseUrl)
        val insecure = WebTabUrl.resolve("http://web.threedollars.co.kr/events/point", baseUrl)
        val noSlash = WebTabUrl.resolve("events/point", baseUrl)
        val blank = WebTabUrl.resolve(" ", baseUrl)

        // Then
        assertEquals("https://echo.dev.threedollars.co.kr/events/point?ref=tab", absolute)
        assertEquals("https://web.threedollars.co.kr/events/point", relative)
        assertNull(insecure)
        assertNull(noSlash)
        assertNull(blank)
    }

    // TH-1650 TC4
    @Test
    fun `TH1650_TC4_이벤트탭_웹도메인은_토큰과_앱UA를_싣고_비신뢰도메인엔_안싣는다`() {
        // Given
        val eventUrl = "https://echo.dev.threedollars.co.kr/events/point"
        val releaseEventUrl = "https://web.threedollars.co.kr/events/point"
        val untrustedUrl = "https://example.com/events/point"

        // When
        val event = InAppBrowserHeaders.build(eventUrl, token = "token", userAgent = "ua", isDebug = true)
        val releaseEvent = InAppBrowserHeaders.build(releaseEventUrl, token = "token", userAgent = "ua", isDebug = false)
        val untrusted = InAppBrowserHeaders.build(untrustedUrl, token = "token", userAgent = "ua", isDebug = true)

        // Then
        assertEquals(mapOf("Authorization" to "token"), event.headers)
        assertEquals("ua", event.userAgent)
        assertEquals(mapOf("Authorization" to "token"), releaseEvent.headers)
        assertEquals(emptyMap<String, String>(), untrusted.headers)
        assertNull(untrusted.userAgent)
    }

    // TH-1650 TC5
    @Test
    fun `TH1650_TC5_앱에없는_APP_DEFINED_id와_모르는_type은_제외된다`() {
        // Given
        val response = "BottomNavigationWithUnknownTabs.json"

        // When
        val tabs = resolve(response)

        // Then
        assertEquals(listOf("home", "store_create", "event", "community", "my"), tabs.map { it.id })
    }

    // TH-1650 TC5
    @Test
    fun `TH1650_TC5_구응답에서_모르는id의_APP_SCHEME탭과_link없는_DYNAMIC탭은_제외된다`() {
        // Given
        val home = tab("home", type = null)
        val unknownAppScheme = tab("coupon", type = null, link = SDLinkModel(type = "APP_SCHEME", link = "dollars://myCoupons"))
        val dynamicWithoutLink = tab("event", type = "DYNAMIC", link = null)
        val unknownLinkType = tab("store", type = "DYNAMIC", link = SDLinkModel(type = "STORE", link = "1"))

        // When
        val tabs = MainTabResolver.resolve(listOf(home, unknownAppScheme, dynamicWithoutLink, unknownLinkType), debugBaseUrl)

        // Then
        assertEquals(listOf("home"), tabs.map { it.id })
    }

    // TH-1650 TC10
    @Test
    fun `TH1650_TC10_DYNAMIC_APP_SCHEME탭은_선택하지않는_딥링크탭이다`() {
        // Given
        val deeplinkTab = tab("coupon", type = "DYNAMIC", link = SDLinkModel(type = "APP_SCHEME", link = "dollars://myCoupons"))

        // When
        val tabs = MainTabResolver.resolve(listOf(tab("home"), deeplinkTab), debugBaseUrl)

        // Then
        val coupon = tabs.first { it.id == "coupon" }
        assertEquals(MainTabDestination.Deeplink("dollars://myCoupons"), coupon.destination)
        assertFalse(coupon.isSelectable)
    }

    // TH-1650 TC6
    @Test
    fun `TH1650_TC6_기존화면탭은_GA탭로그와_서버로그를_웹뷰탭은_서버로그만_보낸다`() {
        // Given
        val tabs = resolve("BottomNavigation.json").associateBy { it.id }

        // When
        val legacyObjectIds = listOf("home", "store_create", "community", "my").map { tabs.getValue(it).legacyLogObjectId }
        val event = tabs.getValue("event")

        // Then
        assertEquals(listOf(LogObjectId.HOME, LogObjectId.WRITE, LogObjectId.COMMUNITY, LogObjectId.MY_PAGE), legacyObjectIds)
        assertNull(event.legacyLogObjectId)
        assertEquals("bottom_navigation", event.clickLog?.screenName)
        assertTrue(tabs.values.all { it.clickLog != null })
    }

    // TH-1650 TC6
    @Test
    fun `TH1650_TC6_기본탭은_서버로그없이_GA탭로그만_보낸다`() {
        // Given
        val tabs = MainTab.defaults

        // When
        val serverLogs = tabs.mapNotNull { it.clickLog }

        // Then
        assertTrue(serverLogs.isEmpty())
        assertEquals(listOf(LogObjectId.HOME, LogObjectId.WRITE, LogObjectId.COMMUNITY, LogObjectId.MY_PAGE), tabs.map { it.legacyLogObjectId })
    }

    // TH-1650 TC7
    @Test
    fun `TH1650_TC7_응답이없거나_비어있으면_기본4탭이다`() {
        // Given
        val failed: List<BottomNavigationTabModel>? = null

        // When
        val fromFailure = MainTabResolver.resolve(failed, debugBaseUrl)
        val fromEmpty = MainTabResolver.resolve(emptyList(), debugBaseUrl)

        // Then
        assertEquals(listOf("home", "store_create", "community", "my"), fromFailure.map { it.id })
        assertEquals(MainTab.defaults, fromEmpty)
    }

    // TH-1650 TC7
    @Test
    fun `TH1650_TC7_응답이_대기시간보다_늦으면_기다리지않고_돌아온다`() = runBlocking {
        // Given
        val neverFinishes = Job()
        val finished = CompletableDeferred(Unit)

        // When
        val startedAt = System.currentTimeMillis()
        val slowResult = neverFinishes.joinWithin(timeoutMillis = 50)
        val elapsed = System.currentTimeMillis() - startedAt
        val fastResult = finished.joinWithin(timeoutMillis = 50)

        // Then
        assertFalse(slowResult)
        assertTrue(elapsed < 1_000)
        assertTrue(fastResult)
        neverFinishes.cancel()
    }

    // TH-1650 TC8
    @Test
    fun `TH1650_TC8_홈탭이없으면_기본탭으로_대체된다`() {
        // Given
        val withoutHome = serverTabs("BottomNavigation.json").filterNot { it.id == "home" }

        // When
        val tabs = MainTabResolver.resolve(withoutHome, debugBaseUrl)

        // Then
        assertEquals(MainTab.defaults, tabs)
    }

    // TH-1650 TC8
    @Test
    fun `TH1650_TC8_같은화면과_같은id는_첫번째만_남고_최대5개다`() {
        // Given
        val response = listOf(
            tab("home"),
            tab("HOME"),
            tab("event", type = "DYNAMIC", link = SDLinkModel("WEB", "/events/point")),
            tab("event", type = "DYNAMIC", link = SDLinkModel("WEB", "/events/other")),
            tab("store_create"),
            tab("community"),
            tab("my"),
            tab("promo", type = "DYNAMIC", link = SDLinkModel("WEB", "/events/promo")),
        )

        // When
        val tabs = MainTabResolver.resolve(response, debugBaseUrl)

        // Then
        assertEquals(MainTabResolver.MAX_TAB_COUNT, tabs.size)
        assertEquals(listOf("home", "event", "store_create", "community", "my"), tabs.map { it.id })
        assertEquals(MainTabDestination.Web("https://echo.dev.threedollars.co.kr/events/point"), tabs[1].destination)
    }

    // TH-1650 TC8
    @Test
    fun `TH1650_TC8_첫탭을_기본선택하되_제보나_딥링크탭이면_홈을_고른다`() {
        // Given
        val eventFirst = MainTabResolver.resolve(
            listOf(tab("event", type = "DYNAMIC", link = SDLinkModel("WEB", "/events/point")), tab("home")),
            debugBaseUrl,
        )
        val storeCreateFirst = MainTabResolver.resolve(listOf(tab("store_create"), tab("home")), debugBaseUrl)
        val deeplinkFirst = MainTabResolver.resolve(
            listOf(tab("coupon", type = "DYNAMIC", link = SDLinkModel("APP_SCHEME", "dollars://myCoupons")), tab("home")),
            debugBaseUrl,
        )

        // When
        val fromEventFirst = MainTabResolver.initialTab(eventFirst)
        val fromStoreCreateFirst = MainTabResolver.initialTab(storeCreateFirst)
        val fromDeeplinkFirst = MainTabResolver.initialTab(deeplinkFirst)

        // Then
        assertEquals("event", fromEventFirst.id)
        assertEquals("home", fromStoreCreateFirst.id)
        assertEquals("home", fromDeeplinkFirst.id)
    }

    private fun tab(
        id: String,
        type: String? = "APP_DEFINED",
        link: SDLinkModel? = null,
    ) = BottomNavigationTabModel(
        id = id,
        label = id,
        iconUrl = null,
        selectedTintColor = null,
        type = type,
        link = link,
        clickLog = null,
    )
}
