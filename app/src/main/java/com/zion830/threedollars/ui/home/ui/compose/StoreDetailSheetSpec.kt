package com.zion830.threedollars.ui.home.ui.compose

import kotlin.math.PI

/**
 * 가게 미리보기 시트의 tip ↔ full(가게 상세) 전환 규칙. iOS 가 쓰는 FloatingPanel 기본 동작을 따른다.
 *
 * full 은 상단 네비 높이만큼 내려온 위치(fullOffset)다. 네비는 시트 밖 고정 위치에 두고 [expandProgress] 만큼 fade 로 나타난다.
 * tip 은 미리보기 내용 높이만큼 보이는 위치다.
 */
object StoreDetailSheetSpec {
    /**
     * 손을 뗀 속도로 더 갈 거리를 예측하는 비율(초). FloatingPanel `momentumProjectionRate`(0.998) 로
     * `v * r / (1 - r) / 1000` 을 계산한 값이다.
     */
    const val MOMENTUM_PROJECTION_SECONDS = 0.499f

    /** 스냅 스프링 응답 시간(초). FloatingPanel `springResponseTime` 기본값. */
    private const val SPRING_RESPONSE_SECONDS = 0.4f

    /** 응답 시간 [SPRING_RESPONSE_SECONDS] 인 임계 감쇠 스프링의 강성. */
    val SPRING_STIFFNESS: Float = (2 * PI / SPRING_RESPONSE_SECONDS).let { (it * it).toFloat() }

    /**
     * 손을 뗐을 때 full 로 갈지. 지금 위치에 속도만큼 더 간 예측 위치가 tip 과 full 의 중간을 넘으면 그쪽으로 간다.
     */
    fun shouldExpand(currentOffset: Float, tipOffset: Float, velocityY: Float, fullOffset: Float = 0f): Boolean {
        val projected = (currentOffset + velocityY * MOMENTUM_PROJECTION_SECONDS).coerceIn(fullOffset, tipOffset)
        return projected < (tipOffset + fullOffset) / 2f
    }

    /**
     * tip(0) → full(1) 사이 어디쯤인지. 상단 네비의 투명도로 쓴다.
     */
    fun expandProgress(currentOffset: Float, tipOffset: Float, fullOffset: Float): Float {
        if (tipOffset <= fullOffset) return 1f
        return ((tipOffset - currentOffset) / (tipOffset - fullOffset)).coerceIn(0f, 1f)
    }
}
