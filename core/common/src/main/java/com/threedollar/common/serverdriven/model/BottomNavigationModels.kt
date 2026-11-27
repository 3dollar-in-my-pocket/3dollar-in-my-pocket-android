package com.threedollar.common.serverdriven.model

/**
 * 서버가 내려준 하단 탭 하나. 응답 원형에 가깝게 두고, 어떤 화면으로 매핑할지·제외할지는 앱이 정한다.
 *
 * @property type `APP_DEFINED`(앱이 [id]로 기존 화면 매핑) / `DYNAMIC`([link]로 랜딩). 구 응답은 null 이다.
 */
data class BottomNavigationTabModel(
    val id: String,
    val label: String,
    val iconUrl: String?,
    val selectedTintColor: String?,
    val type: String?,
    val link: SDLinkModel?,
    val clickLog: SDClickLogModel?,
)
