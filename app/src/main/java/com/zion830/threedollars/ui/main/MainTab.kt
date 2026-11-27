package com.zion830.threedollars.ui.main

import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.serverdriven.model.SDClickLogModel

/** 하단 탭을 눌렀을 때 갈 곳. 앱이 아는 화면 4개 + 서버 링크(웹뷰 탭·딥링크). */
sealed interface MainTabDestination {
    data object Home : MainTabDestination
    data object StoreCreate : MainTabDestination
    data object Community : MainTabDestination
    data object My : MainTabDestination
    data class Web(val url: String) : MainTabDestination
    data class Deeplink(val link: String) : MainTabDestination
}

/**
 * 메인 하단 탭 하나.
 *
 * @property label 비어 있으면 화면이 [destination] 기본 문구를 쓴다(기본 탭).
 * @property iconUrl null 이면 화면이 [destination] 기본 아이콘을 쓴다.
 * @property clickLog 서버가 내려준 탭 클릭 로그. 기본 탭은 null.
 */
data class MainTab(
    val id: String,
    val label: String,
    val iconUrl: String?,
    val selectedTintColor: String?,
    val destination: MainTabDestination,
    val clickLog: SDClickLogModel?,
) {
    /** 딥링크 탭은 탭을 선택하지 않고 링크 화면만 띄운다. */
    val isSelectable: Boolean get() = destination !is MainTabDestination.Deeplink

    /** 기존 GA 탭 클릭 로그(main_tab_bar)의 objectId. 앱이 아는 화면만 있고, 웹뷰·딥링크 탭은 서버 로그만 보낸다. */
    val legacyLogObjectId: LogObjectId?
        get() = when (destination) {
            MainTabDestination.Home -> LogObjectId.HOME
            MainTabDestination.StoreCreate -> LogObjectId.WRITE
            MainTabDestination.Community -> LogObjectId.COMMUNITY
            MainTabDestination.My -> LogObjectId.MY_PAGE
            is MainTabDestination.Web, is MainTabDestination.Deeplink -> null
        }

    companion object {
        const val ID_HOME = "home"
        const val ID_STORE_CREATE = "store_create"
        const val ID_COMMUNITY = "community"
        const val ID_MY = "my"

        /** 서버 응답이 없거나 쓸 수 없을 때의 기존 하드코딩 탭(홈/제보/커뮤니티/마이). */
        val defaults: List<MainTab> = listOf(
            defaultTab(ID_HOME, MainTabDestination.Home),
            defaultTab(ID_STORE_CREATE, MainTabDestination.StoreCreate),
            defaultTab(ID_COMMUNITY, MainTabDestination.Community),
            defaultTab(ID_MY, MainTabDestination.My),
        )

        /** APP_DEFINED 탭 id → 앱 화면. 앱에 없는 id 면 null. */
        fun appDestinationOf(tabId: String): MainTabDestination? = when (tabId.lowercase()) {
            ID_HOME -> MainTabDestination.Home
            ID_STORE_CREATE -> MainTabDestination.StoreCreate
            ID_COMMUNITY -> MainTabDestination.Community
            ID_MY -> MainTabDestination.My
            else -> null
        }

        private fun defaultTab(id: String, destination: MainTabDestination) = MainTab(
            id = id,
            label = "",
            iconUrl = null,
            selectedTintColor = null,
            destination = destination,
            clickLog = null,
        )
    }
}
