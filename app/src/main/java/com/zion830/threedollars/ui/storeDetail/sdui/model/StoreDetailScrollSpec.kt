package com.zion830.threedollars.ui.storeDetail.sdui.model

/**
 * 스크롤 위치에 따라 달라지는 상세 화면 표시 규칙. Compose 에 의존하지 않도록 값만 받아 계산한다.
 */
object StoreDetailScrollSpec {

    /** 가게명이 네비 아래로 지나가는 구간(dp). 이 구간 동안 네비 제목이 서서히 나타난다. */
    const val TITLE_FADE_RANGE_DP = 48f

    /**
     * 네비 제목 투명도.
     *
     * @param firstVisibleIndex 목록의 첫 번째 보이는 항목 위치
     * @param firstVisibleOffsetDp 그 항목이 위로 가려진 만큼(dp)
     * @param previewIndex PREVIEW 섹션 위치. 없으면 목록 맨 위를 기준으로 한다.
     */
    fun titleAlpha(firstVisibleIndex: Int, firstVisibleOffsetDp: Float, previewIndex: Int?): Float {
        val anchor = previewIndex ?: 0
        return when {
            firstVisibleIndex > anchor -> 1f
            firstVisibleIndex < anchor -> 0f
            else -> (firstVisibleOffsetDp / TITLE_FADE_RANGE_DP).coerceIn(0f, 1f)
        }
    }

    /**
     * 선택할 탭 위치.
     *
     * @param tabTargets 탭마다 가리키는 섹션 위치(못 찾으면 null)
     * @param anchorIndex 고정된 탭 바 바로 아래에 걸린 섹션 위치
     * @param isAtBottom 목록 끝에 닿았는지. 닿았으면 마지막 탭을 선택한다.
     */
    fun selectedTab(tabTargets: List<Int?>, anchorIndex: Int, isAtBottom: Boolean): Int {
        if (tabTargets.isEmpty()) return 0
        if (isAtBottom) return tabTargets.indexOfLast { it != null }.coerceAtLeast(0)
        val selected = tabTargets.indices.lastOrNull { index ->
            val target = tabTargets[index]
            target != null && target <= anchorIndex
        }
        return selected ?: 0
    }

    /**
     * 하단 고정 칩 바 노출 여부. PREVIEW 섹션의 액션 버튼 줄이 목록 위쪽으로 사라지면 보인다.
     *
     * @param actionBarBottom 액션 버튼 줄 아래 끝 위치(화면 좌표). 화면에 그려지지 않았으면 null
     * @param viewportTop 목록이 실제로 보이기 시작하는 위치(네비·고정 탭 아래, 화면 좌표)
     */
    fun isBottomBarVisible(
        hasActionBars: Boolean,
        previewIndex: Int?,
        firstVisibleIndex: Int,
        actionBarBottom: Float?,
        viewportTop: Float,
    ): Boolean {
        if (!hasActionBars || previewIndex == null) return false
        if (firstVisibleIndex > previewIndex) return true
        if (firstVisibleIndex < previewIndex) return false
        return actionBarBottom != null && actionBarBottom <= viewportTop
    }
}
