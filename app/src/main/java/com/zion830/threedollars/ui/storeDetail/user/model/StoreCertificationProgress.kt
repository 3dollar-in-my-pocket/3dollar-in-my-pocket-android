package com.zion830.threedollars.ui.storeDetail.user.model

/**
 * 방문 인증 화면 하단 진행 막대 비율 (iOS `VisitView` 와 동일).
 * 가게와 [BASE_DISTANCE_M] 보다 멀면 절반에 고정하고, 가까워질수록 끝까지 찬다.
 */
object StoreCertificationProgress {
    const val BASE_DISTANCE_M = 300f
    private const val FAR_PROGRESS = 0.5f

    /** 0 ~ 100 */
    fun percent(distanceM: Float): Int {
        val ratio = if (distanceM > BASE_DISTANCE_M) FAR_PROGRESS else (BASE_DISTANCE_M - distanceM.coerceAtLeast(0f)) / BASE_DISTANCE_M
        return (ratio * 100).toInt()
    }
}
