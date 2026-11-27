package com.zion830.threedollars.ui.main

import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
import com.threedollar.common.serverdriven.model.SDLinkModel

/**
 * 서버 하단 탭 응답을 메인 탭 목록으로 바꾼다(TH-1650).
 *
 * - `APP_DEFINED`: 앱이 id 로 기존 화면에 매핑, 앱에 없는 id 는 제외
 * - `DYNAMIC`: link WEB → 웹뷰 탭, APP_SCHEME → 딥링크 탭
 * - type 없음(구 응답): 아는 id 면 기존 화면, 아니면 WEB 링크만 웹뷰 탭
 * - 모르는 type 제외, 같은 화면·같은 id 는 첫 번째만, 최대 [MAX_TAB_COUNT]개
 * - 응답이 없거나 홈 탭이 없으면 [MainTab.defaults]
 */
object MainTabResolver {

    /** BottomNavigationView 는 6개부터 예외를 던진다. */
    const val MAX_TAB_COUNT = 5

    private const val TYPE_APP_DEFINED = "APP_DEFINED"
    private const val TYPE_DYNAMIC = "DYNAMIC"
    private const val LINK_TYPE_WEB = "WEB"
    private const val LINK_TYPE_APP_SCHEME = "APP_SCHEME"

    fun resolve(tabs: List<BottomNavigationTabModel>?, webBaseUrl: String): List<MainTab> {
        if (tabs.isNullOrEmpty()) return MainTab.defaults

        val usedIds = mutableSetOf<String>()
        val usedAppDestinations = mutableSetOf<MainTabDestination>()
        val resolved = tabs.mapNotNull { tab ->
            val destination = destinationOf(tab, webBaseUrl) ?: return@mapNotNull null
            if (destination.isAppScreen && !usedAppDestinations.add(destination)) return@mapNotNull null
            if (!usedIds.add(tab.id)) return@mapNotNull null
            MainTab(
                id = tab.id,
                label = tab.label,
                iconUrl = tab.iconUrl,
                selectedTintColor = tab.selectedTintColor,
                destination = destination,
                clickLog = tab.clickLog,
            )
        }.take(MAX_TAB_COUNT)

        return if (resolved.any { it.destination == MainTabDestination.Home }) resolved else MainTab.defaults
    }

    /** 첫 탭을 기본 선택하되, 첫 탭이 제보·딥링크 탭이면 홈을 고른다. */
    fun initialTab(tabs: List<MainTab>): MainTab {
        val first = tabs.firstOrNull() ?: return MainTab.defaults.first()
        return when (first.destination) {
            MainTabDestination.StoreCreate, is MainTabDestination.Deeplink ->
                tabs.firstOrNull { it.destination == MainTabDestination.Home } ?: first

            else -> first
        }
    }

    private fun destinationOf(tab: BottomNavigationTabModel, webBaseUrl: String): MainTabDestination? =
        when (tab.type) {
            TYPE_APP_DEFINED -> MainTab.appDestinationOf(tab.id)
            TYPE_DYNAMIC -> dynamicDestinationOf(tab.link, webBaseUrl)
            null -> MainTab.appDestinationOf(tab.id)
                ?: tab.link?.takeIf { it.type == LINK_TYPE_WEB }?.let { dynamicDestinationOf(it, webBaseUrl) }

            else -> null
        }

    private fun dynamicDestinationOf(link: SDLinkModel?, webBaseUrl: String): MainTabDestination? {
        link ?: return null
        return when (link.type) {
            LINK_TYPE_WEB -> WebTabUrl.resolve(link.link, webBaseUrl)?.let(MainTabDestination::Web)
            LINK_TYPE_APP_SCHEME -> link.link.trim().takeIf { it.isNotEmpty() }?.let(MainTabDestination::Deeplink)
            else -> null
        }
    }

    private val MainTabDestination.isAppScreen: Boolean
        get() = this !is MainTabDestination.Web && this !is MainTabDestination.Deeplink
}
