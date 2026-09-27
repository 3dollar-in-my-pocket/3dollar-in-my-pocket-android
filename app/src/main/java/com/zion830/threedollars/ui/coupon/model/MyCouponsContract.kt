package com.zion830.threedollars.ui.coupon.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponPageModel
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.threedollar.common.R as CommonR

/** 내 쿠폰함 탭. 탭마다 조회하는 쿠폰 상태가 다르다. */
enum class CouponTab(
    val statuses: List<IssuedCouponStatus>,
    @StringRes val titleRes: Int,
) {
    AVAILABLE(listOf(IssuedCouponStatus.ISSUED), CommonR.string.my_coupons_tab_available),
    PAST(listOf(IssuedCouponStatus.USED, IssuedCouponStatus.EXPIRED), CommonR.string.my_coupons_tab_past),
}

@Immutable
data class CouponTabState(
    val coupons: List<IssuedCouponModel> = emptyList(),
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
) {
    val canLoadMore: Boolean
        get() = hasLoaded && nextCursor != null && !isLoading

    /** 첫 조회가 끝났고 쿠폰이 0장이면 빈 뷰를 보여준다. */
    val isEmpty: Boolean
        get() = hasLoaded && coupons.isEmpty()

    fun appendPage(page: IssuedCouponPageModel): CouponTabState = copy(
        coupons = coupons + page.coupons,
        nextCursor = page.nextCursor,
        isLoading = false,
        hasLoaded = true,
    )
}

@Immutable
data class MyCouponsUiState(
    val tabs: Map<CouponTab, CouponTabState> = CouponTab.entries.associateWith { CouponTabState() },
    val sheetCoupon: IssuedCouponModel? = null,
    val isConfirmVisible: Boolean = false,
    val isUsing: Boolean = false,
) {
    fun tab(tab: CouponTab): CouponTabState = tabs[tab] ?: CouponTabState()

    fun updateTab(tab: CouponTab, transform: (CouponTabState) -> CouponTabState): MyCouponsUiState =
        copy(tabs = tabs + (tab to transform(tab(tab))))

    /** 쿠폰을 쓰면 두 탭 모두 첫 페이지부터 다시 불러와야 하므로 비운다. */
    fun resetTabs(): MyCouponsUiState = copy(tabs = CouponTab.entries.associateWith { CouponTabState() })
}

@Immutable
sealed interface MyCouponsUiIntent {
    data object OnInit : MyCouponsUiIntent
    data object OnBackClick : MyCouponsUiIntent
    data class OnLoadNextPage(val tab: CouponTab) : MyCouponsUiIntent
    data class OnStoreClick(val coupon: IssuedCouponModel) : MyCouponsUiIntent
    data class OnCouponClick(val coupon: IssuedCouponModel) : MyCouponsUiIntent
    data object OnSheetDismiss : MyCouponsUiIntent
    data object OnUseClick : MyCouponsUiIntent
    data object OnConfirmCancel : MyCouponsUiIntent
    data object OnConfirmUse : MyCouponsUiIntent
}

@Immutable
sealed interface MyCouponsUiEffect {
    data object Close : MyCouponsUiEffect
    data class OpenStore(val storeId: String) : MyCouponsUiEffect
    data class ShowToast(@StringRes val messageRes: Int) : MyCouponsUiEffect
    data class ShowErrorAlert(val message: String?) : MyCouponsUiEffect
}

const val MY_COUPON_PAGE_SIZE = 20
