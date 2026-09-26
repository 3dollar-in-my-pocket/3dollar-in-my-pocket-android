package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeMapControl
import com.threedollar.common.serverdriven.model.HomeMapControlButton
import com.threedollar.common.serverdriven.model.HomeMapControlFilterOption
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel

/** 홈 지도 좌측 하단에 그릴 컨트롤 버튼 하나. */
sealed interface HomeMapControlItem {

    data class ServerDriven(
        val control: HomeMapControl,
        val button: HomeMapControlButton,
    ) : HomeMapControlItem

    /** 서버 컨트롤이 없을 때(섹션 없음·응답 실패) 기존 현재 위치 버튼으로 대신한다. */
    data object FallbackCurrentLocation : HomeMapControlItem
}

/**
 * `HOME_MAP_CONTROL` 섹션을 화면에 그릴 버튼과 주변 가게 조회 파라미터로 바꾼다.
 *
 * 필터 값은 `paramKey → Boolean` 으로 들고 있고, 한 번도 탭하지 않은 필터는 false 다.
 */
internal object HomeMapControlResolver {

    private const val MOVE_TO_CURRENT_LOCATION_ACTION = "HOME_MAP_CONTROL_SECTION_MOVE_TO_CURRENT_LOCATION"
    private const val ZOOM_LEVEL_PARAM_KEY = "MAP_ZOOM_LEVEL"

    fun controls(sections: List<HomeScreenSection>): List<HomeMapControl> =
        sections.filterIsInstance<HomeScreenSection.HomeMapControlSectionModel>()
            .firstOrNull()
            ?.controls
            .orEmpty()

    fun items(
        controls: List<HomeMapControl>,
        filterValues: Map<String, Boolean>,
    ): List<HomeMapControlItem> {
        val items = controls.mapNotNull { control ->
            when (control) {
                is HomeMapControl.Action -> HomeMapControlItem.ServerDriven(control, control.button)
                is HomeMapControl.Filter -> displayedOption(control, filterValues)
                    ?.let { HomeMapControlItem.ServerDriven(control, it.button) }
            }
        }
        return items.ifEmpty { listOf(HomeMapControlItem.FallbackCurrentLocation) }
    }

    /**
     * 서버는 `paramValue` 를 "탭하면 적용될 값"으로 내려준다(꺼짐 상태의 빈 북마크 옵션이 `paramValue = true`).
     * 그래서 현재 값과 다른 옵션이 지금 보여줄 버튼이다. iOS `HomeMapControlViewModel.displayedOption` 과 같다.
     */
    fun displayedOption(
        filter: HomeMapControl.Filter,
        filterValues: Map<String, Boolean>,
    ): HomeMapControlFilterOption? {
        val current = filterValues[filter.paramKey] ?: false
        return filter.options.firstOrNull { it.paramValue != current } ?: filter.options.firstOrNull()
    }

    fun toggle(
        filter: HomeMapControl.Filter,
        filterValues: Map<String, Boolean>,
    ): Map<String, Boolean> {
        val option = displayedOption(filter, filterValues) ?: return filterValues
        return filterValues + (filter.paramKey to option.paramValue)
    }

    fun queryParams(
        controls: List<HomeMapControl>,
        filterValues: Map<String, Boolean>,
    ): Map<String, String> = controls
        .filterIsInstance<HomeMapControl.Filter>()
        .associate { it.paramKey to (filterValues[it.paramKey] ?: false).toString() }

    fun isMoveToCurrentLocation(action: SDCustomActionModel?): Boolean =
        action?.actionType == MOVE_TO_CURRENT_LOCATION_ACTION

    /** 현재 위치 이동 후 적용할 `MAP_ZOOM_LEVEL`. 없거나 숫자가 아니면 null 이라 줌을 유지한다. */
    fun zoomLevel(action: SDCustomActionModel?): Double? =
        when (val value = action?.extraParams?.get(ZOOM_LEVEL_PARAM_KEY)) {
            is SDClickLogValue.DoubleValue -> value.value
            is SDClickLogValue.IntValue -> value.value.toDouble()
            is SDClickLogValue.LongValue -> value.value.toDouble()
            is SDClickLogValue.StringValue -> value.value.toDoubleOrNull()
            else -> null
        }
}
