package com.threedollar.common.serverdriven.model

enum class HomeScreenSectionType {
    HOME_FILTER,
    UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): HomeScreenSectionType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

enum class HomeFilterBarType {
    CATEGORY_BAR,
    RADIO_BAR,
    ACTION_BAR,
    UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): HomeFilterBarType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

enum class FilterOpenStatuses {
    OPEN,
    CLOSED,
    UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): FilterOpenStatuses =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

data class HomeFilterScreenModel(
    val sections: List<HomeScreenSection> = emptyList(),
    val configuration: HomeFilterConfiguration? = null,
    val viewLog: SDViewLogModel? = null,
)

/**
 * 홈 화면 동작을 서버에서 제어하는 설정값.
 *
 * @property initialMapZoomLevel 홈 지도의 최초 줌 레벨. 값이 없으면 지도 SDK 기본 줌을 유지한다.
 */
data class HomeFilterConfiguration(
    val initialMapZoomLevel: Double? = null,
)

sealed interface HomeScreenSection {
    val type: HomeScreenSectionType

    data class HomeFilterSectionModel(
        override val type: HomeScreenSectionType,
        val bars: List<HomeFilterBar> = emptyList(),
    ) : HomeScreenSection

    data class Unknown(
        override val type: HomeScreenSectionType,
    ) : HomeScreenSection
}

sealed interface HomeFilterBar {
    val type: HomeFilterBarType

    data class CategoryBar(
        override val type: HomeFilterBarType,
        val categoriesFilter: SDChipModel,
        val categoriesFilterClickLog: SDClickLogModel?,
        val currentCategoryFilter: HomeFilterCurrentCategory? = null,
    ) : HomeFilterBar

    data class RadioBar(
        override val type: HomeFilterBarType,
        val paramKey: String,
        val options: List<HomeFilterRadioOption> = emptyList(),
    ) : HomeFilterBar

    data class ActionBar(
        override val type: HomeFilterBarType,
        val button: SDButtonModel,
        val clickLog: SDClickLogModel?,
    ) : HomeFilterBar

    data class Unknown(
        override val type: HomeFilterBarType,
    ) : HomeFilterBar
}

data class HomeFilterCurrentCategory(
    val fontColor: String?,
    val style: SDSurfaceStyleModel?,
    val clickLog: SDClickLogModel?,
)

data class HomeFilterRadioOption(
    val chip: SDChipModel,
    val paramValue: String?,
    val clickLog: SDClickLogModel?,
)
