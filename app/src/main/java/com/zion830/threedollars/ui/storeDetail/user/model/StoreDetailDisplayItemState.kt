package com.zion830.threedollars.ui.storeDetail.user.model

import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayTriggerModel

data class StoreDetailDisplayItemState(
    val item: StoreDetailDisplayItem? = null,
    val isVisible: Boolean = false,
)

sealed interface StoreDetailDisplayItem {
    val storeId: Int
    val itemType: StoreDisplayItemType
    val trigger: StoreDisplayTriggerModel?
    val isSubmitting: Boolean

    data class VisitInducement(
        override val storeId: Int,
        override val trigger: StoreDisplayTriggerModel?,
        override val isSubmitting: Boolean = false,
    ) : StoreDetailDisplayItem {
        override val itemType: StoreDisplayItemType = StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL
    }

    data class DisappearanceInquiry(
        override val storeId: Int,
        override val trigger: StoreDisplayTriggerModel?,
        val reasons: List<ReasonModel> = listOf(),
        val selectedReason: ReasonModel? = null,
        val isReasonLoading: Boolean = true,
        override val isSubmitting: Boolean = false,
    ) : StoreDetailDisplayItem {
        override val itemType: StoreDisplayItemType = StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL
    }
}

sealed interface StoreDetailDisplayItemEffect {
    data object RefreshStoreDetail : StoreDetailDisplayItemEffect
    data class ShowToast(val message: String) : StoreDetailDisplayItemEffect
}
