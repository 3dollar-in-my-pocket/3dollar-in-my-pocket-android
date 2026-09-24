package com.threedollar.common.sdui.model.element

/**
 * 서버가 내려주는 배경·테두리 스타일. 색은 `#RRGGBB` 문자열이다.
 */
data class SDSurfaceStyleModel(
    val backgroundColor: String? = null,
    val border: Border? = null
) {
    data class Border(
        val color: String?,
        val width: Float?
    )
}
