package com.zion830.threedollars.ui.home.data

import com.naver.maps.geometry.LatLng

/**
 * 앱 밖(푸시·앱 스킴·외부 링크)에서 들어온 `store?storeId=` 링크로 홈에 랜딩해 달라는 요청.
 * [expanded] 가 true 면 가게 시트를 상세(full)로, false 면 미리보기로 연다.
 */
internal data class HomeStoreLandingRequest(
    val storeId: Long,
    val expanded: Boolean,
) {
    companion object {
        private const val VIEW_COLLAPSED = "collapsed"

        /**
         * 앱 안에서 연 링크([isExternal] false)는 기존처럼 가게 상세 화면으로 보내야 하므로 null 을 돌려준다.
         * `view` 가 없거나 알 수 없는 값이면 하위 호환을 위해 펼친다.
         */
        fun from(storeId: String?, view: String?, isExternal: Boolean): HomeStoreLandingRequest? {
            if (!isExternal) return null
            val id = storeId?.toLongOrNull()?.takeIf { it > 0L } ?: return null
            return HomeStoreLandingRequest(storeId = id, expanded = view != VIEW_COLLAPSED)
        }
    }
}

/** 가게 미리보기 응답의 마커 좌표까지 확인한 랜딩 대상. 홈 지도는 [location] 으로 이동한다. */
internal data class HomeStoreLanding(
    val storeId: Long,
    val location: LatLng,
    val expanded: Boolean,
)
