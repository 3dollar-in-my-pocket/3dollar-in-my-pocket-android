package com.zion830.threedollars.ui.storeDetail.displayitem

import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.ImpressionEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.store.model.StoreDisplayItemModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.repository.StoreRepository
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItem
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemEffect
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemState
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailViewSessionCounter
import com.zion830.threedollars.utils.StringUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.threedollar.common.R as CommonR

/**
 * 가게 상세의 활동 유도 모달(방문 인증 유도·없어진 가게 문의) 상태와 동작.
 * 레거시 상세와 v2 상세가 같이 쓰도록 ViewModel 에서 분리했다. 코루틴은 호출한 ViewModel 의 [scope]에서 돈다.
 */
class StoreDisplayItemController(
    private val scope: CoroutineScope,
    private val storeRepository: StoreRepository,
    private val homeRepository: HomeRepository,
    private val screenName: ScreenName,
) {
    private val _state = MutableStateFlow(StoreDetailDisplayItemState())
    val state: StateFlow<StoreDetailDisplayItemState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<StoreDetailDisplayItemEffect>(extraBufferCapacity = 8)
    val effect: SharedFlow<StoreDetailDisplayItemEffect> = _effect.asSharedFlow()

    /** 제출 실패처럼 사용자에게 알려야 하는 서버 메시지. */
    private val _serverError = MutableSharedFlow<String?>(extraBufferCapacity = 8)
    val serverError: SharedFlow<String?> = _serverError.asSharedFlow()

    private val pendingItems = ArrayDeque<StoreDisplayItemModel>()
    private var showJob: Job? = null
    private var autoDismissJob: Job? = null
    private var storeId: Int? = null

    /**
     * 가게 상세 조회 1회에 해당하는 모달 후보를 받아 순서대로 보여준다. 세션 조회수는 호출할 때마다 1 오른다.
     */
    fun load(storeId: Int, deviceLatitude: Double, deviceLongitude: Double) {
        scope.launch {
            val viewCount = StoreDetailViewSessionCounter.increment(storeId)
            storeRepository.getStoreDisplayItems(
                storeId = storeId,
                lat = deviceLatitude,
                lng = deviceLongitude,
                itemTypes = DISPLAY_ITEM_TYPES,
            ).onSuccess { response ->
                this@StoreDisplayItemController.storeId = storeId
                pendingItems.clear()
                pendingItems.addAll(StoreDisplayItemFilter.eligible(response.contents, viewCount))
                if (_state.value.item == null && showJob?.isActive != true) {
                    showNext()
                }
            }
        }
    }

    fun onDisplayed(item: StoreDetailDisplayItem) {
        recordImpression(item.storeId, item.itemType)
        sendImpressionLog(item)
        scheduleAutoDismiss(item.trigger?.displayDurationSeconds)
    }

    fun onVisitClick(isOpened: Boolean) {
        val item = _state.value.item as? StoreDetailDisplayItem.VisitInducement ?: return
        if (item.isSubmitting) return

        cancelAutoDismiss()
        updateCurrent(item.copy(isSubmitting = true))
        sendClickVisitInducement(item, isOpened)

        scope.launch {
            val response = homeRepository.postStoreVisit(
                storeId = item.storeId,
                visitType = if (isOpened) VISIT_TYPE_EXISTS else VISIT_TYPE_NOT_EXISTS,
            ).first()
            if (response.ok) {
                dismissCurrent(showNext = true)
                _effect.emit(StoreDetailDisplayItemEffect.RefreshStoreDetail)
                _effect.emit(StoreDetailDisplayItemEffect.ShowToast(StringUtils.getString(CommonR.string.display_item_modal_thanks_toast)))
            } else {
                updateCurrent(item.copy(isSubmitting = false))
                _serverError.emit(response.message)
            }
        }
    }

    fun onReasonClick(reason: ReasonModel) {
        val item = _state.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return
        if (item.isSubmitting) return

        updateCurrent(item.copy(selectedReason = reason))
        sendSelectReasonLog(item.storeId, reason.type)
        scheduleAutoDismiss(item.trigger?.displayDurationSeconds)
    }

    fun onReportClick() {
        val item = _state.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return
        if (item.isSubmitting) return
        val reason = item.selectedReason ?: return

        cancelAutoDismiss()
        updateCurrent(item.copy(isSubmitting = true))
        sendClickDisappearanceReport(item.storeId, reason.type)

        scope.launch {
            val response = homeRepository.deleteStore(item.storeId, reason.type).first()
            if (response.ok) {
                dismissCurrent(showNext = true)
                _effect.emit(StoreDetailDisplayItemEffect.ShowToast(StringUtils.getString(CommonR.string.display_item_modal_thanks_toast)))
            } else {
                updateCurrent(item.copy(isSubmitting = false))
                _serverError.emit(response.message)
            }
        }
    }

    private fun showNext() {
        val item = pendingItems.removeFirstOrNull() ?: return
        showJob?.cancel()
        showJob = scope.launch {
            delay(item.trigger?.displayAfterSeconds.toDelayMillis())
            val modal = item.toDisplayItem(storeId ?: return@launch) ?: return@launch
            _state.value = StoreDetailDisplayItemState(item = modal, isVisible = true)
            if (modal is StoreDetailDisplayItem.DisappearanceInquiry) {
                loadStoreReportReasons(modal.storeId)
            }
        }
    }

    private fun loadStoreReportReasons(storeId: Int) {
        scope.launch {
            val response = homeRepository.getReportReasons(ReportReasonsGroupType.STORE).first()
            if (response.ok) {
                val item = _state.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return@launch
                if (item.storeId == storeId) {
                    updateCurrent(item.copy(reasons = response.data?.reasonModels.orEmpty(), isReasonLoading = false))
                }
            } else {
                _serverError.emit(response.message)
            }
        }
    }

    private fun dismissCurrent(showNext: Boolean) {
        cancelAutoDismiss()
        showJob?.cancel()
        val currentItem = _state.value.item ?: return
        _state.value = StoreDetailDisplayItemState(item = currentItem, isVisible = false)
        scope.launch {
            delay(SLIDE_OUT_MILLIS)
            _state.value = StoreDetailDisplayItemState()
            if (showNext) showNext()
        }
    }

    private fun updateCurrent(item: StoreDetailDisplayItem) {
        _state.update { state -> if (state.item?.itemType == item.itemType) state.copy(item = item) else state }
    }

    private fun recordImpression(storeId: Int, itemType: StoreDisplayItemType) {
        scope.launch {
            storeRepository.postStoreDisplayItemImpression(storeId = storeId, itemTypes = listOf(itemType))
        }
    }

    private fun scheduleAutoDismiss(displayDurationSeconds: Double?) {
        cancelAutoDismiss()
        displayDurationSeconds ?: return
        autoDismissJob = scope.launch {
            delay(displayDurationSeconds.toDelayMillis())
            dismissCurrent(showNext = true)
        }
    }

    private fun cancelAutoDismiss() {
        autoDismissJob?.cancel()
        autoDismissJob = null
    }

    private fun sendImpressionLog(item: StoreDetailDisplayItem) {
        LogManager.sendEvent(
            ImpressionEvent(
                screen = screenName,
                objectType = LogObjectType.BANNER,
                objectId = item.itemType.toLogObjectId(),
                additionalParams = mapOf(ParameterName.STORE_ID to item.storeId.toString()),
            ),
        )
    }

    private fun sendClickVisitInducement(item: StoreDetailDisplayItem.VisitInducement, isOpened: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT_INDUCEMENT_MODAL,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to item.storeId.toString(),
                    ParameterName.VALUE to if (isOpened) LogObjectId.VISIT_SUCCESS.value else LogObjectId.VISIT_FAIL.value,
                ),
            ),
        )
    }

    private fun sendSelectReasonLog(storeId: Int, reasonType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SELECT_REASON,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId.toString(),
                    ParameterName.REASON_TYPE to reasonType,
                ),
            ),
        )
    }

    private fun sendClickDisappearanceReport(storeId: Int, reasonType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId.toString(),
                    ParameterName.REASON_TYPE to reasonType,
                ),
            ),
        )
    }

    private fun StoreDisplayItemModel.toDisplayItem(storeId: Int): StoreDetailDisplayItem? = when (itemType) {
        StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL -> StoreDetailDisplayItem.DisappearanceInquiry(storeId = storeId, trigger = trigger)
        StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL -> StoreDetailDisplayItem.VisitInducement(storeId = storeId, trigger = trigger)
        StoreDisplayItemType.UNKNOWN -> null
    }

    private fun StoreDisplayItemType.toLogObjectId(): LogObjectId = when (this) {
        StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL -> LogObjectId.DISAPPEARANCE_INQUIRY_MODAL
        StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL -> LogObjectId.VISIT_INDUCEMENT_MODAL
        StoreDisplayItemType.UNKNOWN -> LogObjectId.STORE
    }

    private fun Double?.toDelayMillis(): Long = ((this ?: 0.0).coerceAtLeast(0.0) * 1_000).toLong()

    private companion object {
        const val VISIT_TYPE_EXISTS = "EXISTS"
        const val VISIT_TYPE_NOT_EXISTS = "NOT_EXISTS"
        const val SLIDE_OUT_MILLIS = 300L
        val DISPLAY_ITEM_TYPES = listOf(
            StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL,
            StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL,
        )
    }
}
