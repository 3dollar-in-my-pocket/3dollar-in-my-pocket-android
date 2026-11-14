package com.zion830.threedollars.ui.home.data

import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M

data class HomeCurationQuerySnapshot(
    val location: HomeCurationRequestLocation,
    val revision: Long,
)

object HomeCurationQueryPolicy {
    /**
     * 큐레이션·피드 조회 반경. 홈 리스트와 같은 지도 뷰포트 반경을 쓰되,
     * 아직 뷰포트를 못 잰 초기값([DEFAULT_DISTANCE_M] 그대로)이면 null 로 보내 서버 기본 반경(3km)을 쓰게 한다.
     * 실제로 잰 값은 100km 를 넘어도(전국 단위로 축소) 그대로 보낸다.
     */
    fun maxDistanceM(currentDistanceM: Double): Double? =
        currentDistanceM.takeIf { it > 0.0 && it != DEFAULT_DISTANCE_M }

    fun commit(
        previous: HomeCurationQuerySnapshot?,
        location: HomeCurationRequestLocation,
        refresh: Boolean,
    ): HomeCurationQuerySnapshot {
        if (previous != null && !refresh) return previous
        return HomeCurationQuerySnapshot(location, revision = (previous?.revision ?: 0L) + 1L)
    }

    fun canReuse(
        state: HomeCurationUiState,
        snapshot: HomeCurationQuerySnapshot,
        requestedRevision: Long?,
        tabId: String,
    ): Boolean = requestedRevision == snapshot.revision &&
        state.requestLocation == snapshot.location &&
        state.sectionTabId == tabId &&
        (state.isLoading || (state.section != null && state.errorMessage == null))
}
