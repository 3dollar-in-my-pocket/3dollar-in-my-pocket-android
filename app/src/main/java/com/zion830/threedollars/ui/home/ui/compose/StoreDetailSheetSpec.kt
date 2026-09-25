package com.zion830.threedollars.ui.home.ui.compose

/**
 * 가게 미리보기 시트의 tip ↔ full(가게 상세) 전환 규칙.
 *
 * full 은 상단 네비 높이만큼 내려온 위치(fullOffset)다. 네비는 시트 밖 고정 위치에 두고 [expandProgress] 만큼 fade 로 나타난다.
 * tip 은 미리보기 내용 높이만큼 보이는 위치다.
 */
object StoreDetailSheetSpec {
    /** 끌다 놓았을 때 가까운 상태로 스냅하는 애니메이션 길이. 기획: Ease in 300ms */
    const val SNAP_DURATION_MS = 300

    /** 이 속도(px/s) 이상으로 튕기면 위치와 상관없이 튕긴 방향으로 스냅한다. */
    const val FLING_VELOCITY_THRESHOLD = 1200f

    /**
     * 손을 뗐을 때 full 로 갈지. 위로 빠르게 튕기면 full, 아래로 빠르게 튕기면 tip, 아니면 더 가까운 쪽이다.
     */
    fun shouldExpand(currentOffset: Float, tipOffset: Float, velocityY: Float, fullOffset: Float = 0f): Boolean = when {
        velocityY <= -FLING_VELOCITY_THRESHOLD -> true
        velocityY >= FLING_VELOCITY_THRESHOLD -> false
        else -> currentOffset < (tipOffset + fullOffset) / 2f
    }

    /**
     * tip(0) → full(1) 사이 어디쯤인지. 상단 네비의 투명도로 쓴다.
     */
    fun expandProgress(currentOffset: Float, tipOffset: Float, fullOffset: Float): Float {
        if (tipOffset <= fullOffset) return 1f
        return ((tipOffset - currentOffset) / (tipOffset - fullOffset)).coerceIn(0f, 1f)
    }
}
