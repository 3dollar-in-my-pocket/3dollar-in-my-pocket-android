package com.threedollar.common.sdui.model.element

/**
 * 서버가 내려주는 클릭·노출·페이지뷰 로그. 클라이언트는 값을 해석하지 않고 그대로 전송한다.
 */
data class SDLogModel(
    val eventType: String? = null,
    val screenName: String? = null,
    val objectType: String? = null,
    val objectId: String? = null,
    val extraParameters: Map<String, Any?>? = null
)
