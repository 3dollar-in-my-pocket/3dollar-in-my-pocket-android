package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.domain.home.data.store.ReasonModel

sealed interface StoreDetailV2UiState {
    data class Loading(val storeId: Long? = null) : StoreDetailV2UiState

    data class Content(
        val storeId: Long,
        val screen: StoreDetailScreenModel,
    ) : StoreDetailV2UiState

    data class Error(
        val storeId: Long,
        val message: String?,
        val error: String?,
    ) : StoreDetailV2UiState
}

sealed interface StoreDetailV2Event {
    data class ShowMessage(val message: String?) : StoreDetailV2Event
    data class CloseContainer(val message: String?) : StoreDetailV2Event
    data class Platform(val action: StoreDetailV2PlatformAction) : StoreDetailV2Event
    data class ShowReviewReportDialog(
        val customAction: SDCustomActionModel,
        val reasons: List<ReasonModel>,
    ) : StoreDetailV2Event
}

sealed interface StoreDetailV2PlatformAction {
    data class CopyAccount(val text: String) : StoreDetailV2PlatformAction
    data class OpenLink(val link: SDLinkModel) : StoreDetailV2PlatformAction
    data class Share(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class Navigation(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class ReviewWrite(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class EditStore(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class ReportStore(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class AddImage(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class EnlargeImage(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class ReportReview(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class CopyAddress(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
    data class EnlargeMap(val customAction: SDCustomActionModel) : StoreDetailV2PlatformAction
}

internal const val STORE_DETAIL_ACCOUNT_COPY_ACTION = "ACCOUNT_COPY"

internal fun canSubmitStoreDetailReviewReport(
    reasons: List<ReasonModel>,
    selectedIndex: Int,
    detail: String,
): Boolean {
    val reason = reasons.getOrNull(selectedIndex) ?: return false
    return !reason.hasReasonDetail || detail.isNotBlank()
}
