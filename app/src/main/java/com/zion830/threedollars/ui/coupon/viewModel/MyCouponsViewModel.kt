package com.zion830.threedollars.ui.coupon.viewModel

import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.coroutines.CoroutineTagElement
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.result.ApiException
import com.zion830.threedollars.ui.coupon.model.CouponTab
import com.zion830.threedollars.ui.coupon.model.MY_COUPON_PAGE_SIZE
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiEffect
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiIntent
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class MyCouponsViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
) : UdfViewModel<MyCouponsUiIntent, MyCouponsUiState, MyCouponsUiEffect>() {

    private var initialized = false
    private val failedCursors = mutableMapOf<CouponTab, String?>()

    /** 두 탭을 동시에 조회하다 함께 실패해도 얼럿은 한 번만 띄운다. 조회가 성공하면 다시 띄울 수 있다. */
    private var isLoadErrorShown = false

    private val stateStore = MutableStateFlow(MyCouponsUiState())
    override val state: StateFlow<MyCouponsUiState> = stateStore.asStateFlow()

    private val _effect = Channel<MyCouponsUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val effect: Flow<MyCouponsUiEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: MyCouponsUiIntent) {
        when (intent) {
            MyCouponsUiIntent.OnInit -> onInit()
            MyCouponsUiIntent.OnBackClick -> _effect.trySend(MyCouponsUiEffect.Close)
            is MyCouponsUiIntent.OnLoadNextPage -> loadPage(intent.tab)
            is MyCouponsUiIntent.OnStoreClick -> intent.coupon.storeId.takeIf { it.isNotBlank() }
                ?.let { _effect.trySend(MyCouponsUiEffect.OpenStore(it)) }

            is MyCouponsUiIntent.OnCouponClick -> if (intent.coupon.status == IssuedCouponStatus.ISSUED) {
                stateStore.update { it.copy(sheetCoupon = intent.coupon) }
            }

            MyCouponsUiIntent.OnSheetDismiss -> stateStore.update {
                if (it.isUsing) it else it.copy(sheetCoupon = null, isConfirmVisible = false)
            }

            MyCouponsUiIntent.OnUseClick -> stateStore.update { it.copy(isConfirmVisible = it.sheetCoupon != null) }
            MyCouponsUiIntent.OnConfirmCancel -> stateStore.update { it.copy(isConfirmVisible = false) }
            MyCouponsUiIntent.OnConfirmUse -> useCoupon()
        }
    }

    override fun onException(exception: Throwable, tag: Any?) {
        super.onException(exception, tag)
        val requestTag = (tag as? CoroutineTagElement)?.tag ?: tag
        if (requestTag is CouponTab) {
            stateStore.update { state -> state.updateTab(requestTag) { it.copy(isLoading = false) } }
            if (isLoadErrorShown) return
            isLoadErrorShown = true
        }
        _effect.trySend(MyCouponsUiEffect.ShowErrorAlert((exception as? ApiException)?.message))
    }

    private fun onInit() {
        if (initialized) return
        initialized = true
        CouponTab.entries.forEach(::loadPage)
    }

    private fun loadPage(tab: CouponTab) {
        val current = stateStore.value.tab(tab)
        if (current.isLoading) return
        if (current.hasLoaded && !current.canLoadMore) return
        val cursor = current.nextCursor
        if (cursor != null && failedCursors[tab] == cursor) return
        stateStore.update { state -> state.updateTab(tab) { it.copy(isLoading = true) } }

        launch(tag = tab) {
            val page = storeRepository.getMyIssuedCoupons(tab.statuses, cursor, MY_COUPON_PAGE_SIZE)
                .onFailure { failedCursors[tab] = cursor }
                .getOrThrow()
            isLoadErrorShown = false
            stateStore.update { state -> state.updateTab(tab) { it.appendPage(page) } }
        }
    }

    private fun useCoupon() {
        val coupon = stateStore.value.sheetCoupon ?: return
        if (stateStore.value.isUsing) return
        stateStore.update { it.copy(isConfirmVisible = false, isUsing = true) }

        launch(tag = USE_TAG) {
            storeRepository.useIssuedCoupon(coupon.issuedKey)
                .onSuccess {
                    failedCursors.clear()
                    stateStore.update { it.resetTabs().copy(sheetCoupon = null, isUsing = false) }
                    _effect.trySend(MyCouponsUiEffect.ShowToast(CommonR.string.store_detail_coupon_used))
                    CouponTab.entries.forEach(::loadPage)
                }
                .onFailure { error ->
                    stateStore.update { it.copy(isUsing = false) }
                    _effect.trySend(MyCouponsUiEffect.ShowErrorAlert((error as? ApiException)?.message))
                }
        }
    }

    private companion object {
        const val USE_TAG = "use_coupon"
    }
}
