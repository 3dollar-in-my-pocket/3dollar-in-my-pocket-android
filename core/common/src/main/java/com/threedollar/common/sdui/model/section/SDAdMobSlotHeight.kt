package com.threedollar.common.sdui.model.section

/**
 * 서버가 내려주는 애드몹 카드 `height`(dp)를 광고 슬롯 높이로 바꾼다.
 * 배너가 잘리지 않도록 [MIN_DP] 보다 작은 값은 [MIN_DP] 로 올린다.
 */
object SDAdMobSlotHeight {
    const val MIN_DP = 50

    fun resolve(height: Int?): Int = (height ?: MIN_DP).coerceAtLeast(MIN_DP)
}
