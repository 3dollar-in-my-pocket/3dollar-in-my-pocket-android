package com.zion830.threedollars.ui.splash.ui

/**
 * Splash 에서 로그인이 필요해졌을 때 로그인 뒤 이어서 열 링크(TH-1602).
 *
 * 백그라운드 푸시는 받은 `link` 를 그대로 쓰고, 카카오 공유 링크(가게 타입 + storeId)는
 * 로그인 상태에서 여는 화면과 같은 가게 상세 링크(`dollars://stores/{storeId}`)로 바꾼다.
 * 우선순위는 [SplashActivity.tryLogin] 과 같이 카카오 가게 타입이 먼저다.
 */
internal object SplashPendingLink {
    private const val DOLLARS_SCHEME = "dollars"
    private const val STORE_DETAIL_LINK_PREFIX = "dollars://stores/"

    fun resolve(
        pushLink: String?,
        storeType: String?,
        storeId: String?,
        kakaoStoreTypes: Set<String>,
    ): String? {
        if (storeType != null && storeType in kakaoStoreTypes) return storeDetailLink(storeId)
        return pushLink?.takeIf { it.contains(DOLLARS_SCHEME) }
    }

    fun storeDetailLink(storeId: String?): String? =
        storeId?.takeIf { id -> id.isNotEmpty() && id.all(Char::isDigit) }?.let { "$STORE_DETAIL_LINK_PREFIX$it" }
}
