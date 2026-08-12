package com.threedollar.domain.store.model

data class StoreDisplayItemsModel(
    val contents: List<StoreDisplayItemModel> = listOf(),
)

data class StoreDisplayItemModel(
    val itemType: StoreDisplayItemType = StoreDisplayItemType.UNKNOWN,
    val description: String = "",
    val isVisible: Boolean = false,
    val trigger: StoreDisplayTriggerModel? = null,
)

enum class StoreDisplayItemType(val value: String) {
    DISAPPEARANCE_INQUIRY_MODAL("DISAPPEARANCE_INQUIRY_MODAL"),
    VISIT_CERTIFICATION_INDUCEMENT_MODAL("VISIT_CERTIFICATION_INDUCEMENT_MODAL"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(value: String?): StoreDisplayItemType =
            entries.firstOrNull { it.value == value } ?: UNKNOWN
    }
}

data class StoreDisplayTriggerModel(
    val type: StoreDisplayTriggerType = StoreDisplayTriggerType.UNKNOWN,
    val displayAfterSeconds: Double = 0.0,
    val displayDurationSeconds: Double? = null,
    val conditions: StoreDisplayTriggerConditionsModel? = null,
)

enum class StoreDisplayTriggerType(val value: String) {
    PAGE_ENTER("PAGE_ENTER"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(value: String?): StoreDisplayTriggerType =
            entries.firstOrNull { it.value == value } ?: UNKNOWN
    }
}

data class StoreDisplayTriggerConditionsModel(
    val sessionViewCountRange: SessionViewCountRangeModel? = null,
)

data class SessionViewCountRangeModel(
    val min: Int? = null,
    val max: Int? = null,
) {
    fun contains(count: Int): Boolean {
        min?.let { if (count < it) return false }
        max?.let { if (count > it) return false }
        return true
    }
}
