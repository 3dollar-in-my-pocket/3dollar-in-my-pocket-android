package com.zion830.threedollars.ui.storeDetail.v2

import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.domain.screen.repository.ScreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StoreDetailV2ViewModel @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val homeRepository: HomeRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<StoreDetailV2UiState>(StoreDetailV2UiState.Loading())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<StoreDetailV2Event>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val _hasUpdates = MutableStateFlow(false)
    val hasUpdates = _hasUpdates.asStateFlow()

    private val _favoriteOverride = MutableStateFlow<Boolean?>(null)
    val favoriteOverride = _favoriteOverride.asStateFlow()

    private var currentStoreId: Long? = null
    private var currentDeviceLatitude: Double? = null
    private var currentDeviceLongitude: Double? = null
    private var loadJob: Job? = null
    internal var viewLoggedStoreId: Long? = null
        private set
    private val sentImpressionKeys = mutableSetOf<String>()

    fun load(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ) {
        if (currentStoreId != storeId) {
            _favoriteOverride.value = null
            sentImpressionKeys.clear()
        }
        currentStoreId = storeId
        currentDeviceLatitude = deviceLatitude
        currentDeviceLongitude = deviceLongitude
        requestScreen(showLoading = true)
    }

    fun issueCoupon(couponId: String) {
        val storeId = currentStoreId ?: return
        runMutation(screenRepository.issueStoreCoupon(storeId = storeId, couponId = couponId))
    }

    fun toggleFavorite(isFavorite: Boolean) {
        val storeId = currentStoreId ?: return
        runMutation(
            if (isFavorite) {
                homeRepository.deleteFavorite(storeId.toString())
            } else {
                homeRepository.putFavorite(storeId.toString())
            },
            onSuccess = { _favoriteOverride.value = !isFavorite },
        )
    }

    fun onAction(actionBar: StoreActionBarModel) {
        (actionBar.clickLog ?: actionBar.button.clickLog)?.let { log ->
            runCatching { SDClickLogger.send(log) }
        }
        actionBar.button.link?.let { link ->
            _events.tryEmit(StoreDetailV2Event.Platform(StoreDetailV2PlatformAction.OpenLink(link)))
            return
        }
        val customAction = actionBar.button.customAction ?: return
        val params = customAction.extraParams
        when (customAction.actionType) {
            ACTION_PREVIEW_SHARE -> emitPlatform(StoreDetailV2PlatformAction.Share(customAction))
            ACTION_PREVIEW_NAVIGATION -> emitPlatform(StoreDetailV2PlatformAction.Navigation(customAction))
            ACTION_PREVIEW_REVIEW_WRITE,
            ACTION_REVIEW_WRITE -> emitPlatform(StoreDetailV2PlatformAction.ReviewWrite(customAction))
            ACTION_EDIT_UPDATE -> emitPlatform(StoreDetailV2PlatformAction.EditStore(customAction))
            ACTION_EDIT_REPORT -> emitPlatform(StoreDetailV2PlatformAction.ReportStore(customAction))
            ACTION_IMAGE_ADD -> emitPlatform(StoreDetailV2PlatformAction.AddImage(customAction))
            ACTION_IMAGE_ENLARGE -> emitPlatform(StoreDetailV2PlatformAction.EnlargeImage(customAction))
            ACTION_REVIEW_REPORT -> emitPlatform(StoreDetailV2PlatformAction.ReportReview(customAction))
            ACTION_MAP_COPY_ADDRESS -> emitPlatform(StoreDetailV2PlatformAction.CopyAddress(customAction))
            ACTION_MAP_ENLARGE -> emitPlatform(StoreDetailV2PlatformAction.EnlargeMap(customAction))
            ACTION_COUPON_ISSUE -> params.stringValue(PARAM_COUPON_ID)?.let(::issueCoupon)
            ACTION_COUPON_USE -> params.stringValue(PARAM_ISSUED_KEY)?.let { issuedKey ->
                runMutation(screenRepository.useIssuedCoupon(issuedKey))
            }
            ACTION_POST_ADD_LIKE,
            ACTION_POST_CANCEL_LIKE -> mutatePostSticker(params)
            ACTION_REVIEW_DELETE -> params.longValue(PARAM_REVIEW_ID)?.let { reviewId ->
                runMutation(screenRepository.deleteStoreReview(reviewId))
            }
            ACTION_REVIEW_ADD_LIKE,
            ACTION_REVIEW_CANCEL_LIKE -> mutateReviewSticker(params)
        }
    }

    fun sendImpression(stableId: String, log: SDImpressionLogModel) {
        if (sentImpressionKeys.add(stableId)) runCatching { SDClickLogger.send(log) }
    }

    fun sendViewLog(log: SDViewLogModel) {
        val storeId = currentStoreId ?: return
        if (viewLoggedStoreId == storeId) return
        viewLoggedStoreId = storeId
        runCatching { SDClickLogger.send(log) }
    }

    fun onChildResult(updated: Boolean) {
        if (!updated) return
        _hasUpdates.value = true
        requestScreen(showLoading = false)
    }

    fun reportMissingStore(deleteReasonType: String) {
        val storeId = currentStoreId?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt() ?: return
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                homeRepository.deleteStore(storeId, deleteReasonType).collect { response ->
                    if (response.ok) {
                        _hasUpdates.value = true
                        _events.emit(StoreDetailV2Event.CloseContainer(response.message))
                    } else {
                        _events.emit(StoreDetailV2Event.ShowMessage(response.message))
                    }
                }
            } catch (throwable: Throwable) {
                throwable.rethrowCancellation()
                _events.emit(StoreDetailV2Event.ShowMessage(null))
            }
        }
    }

    fun requestReviewReport(customAction: com.threedollar.common.serverdriven.model.SDCustomActionModel) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                homeRepository.getReportReasons(ReportReasonsGroupType.REVIEW).collect { response ->
                    val reasons = response.data?.reasonModels.orEmpty()
                    if (response.ok && reasons.isNotEmpty()) {
                        _events.emit(StoreDetailV2Event.ShowReviewReportDialog(customAction, reasons))
                    } else {
                        _events.emit(StoreDetailV2Event.ShowMessage(response.message))
                    }
                }
            } catch (throwable: Throwable) {
                throwable.rethrowCancellation()
                _events.emit(StoreDetailV2Event.ShowMessage(null))
            }
        }
    }

    fun submitReviewReport(
        customAction: com.threedollar.common.serverdriven.model.SDCustomActionModel,
        reason: String,
        reasonDetail: String?,
    ) {
        val storeId = currentStoreId?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt() ?: return
        val reviewId = customAction.extraParams.longValue(PARAM_REVIEW_ID) ?: return
        runMutation(
            homeRepository.reportStoreReview(
                storeId = storeId,
                reviewId = reviewId,
                reportReviewModelRequest = ReportReviewModelRequest(reason = reason, reasonDetail = reasonDetail),
            )
        )
    }

    private fun emitPlatform(action: StoreDetailV2PlatformAction) {
        _events.tryEmit(StoreDetailV2Event.Platform(action))
    }

    private fun mutatePostSticker(params: Map<String, SDClickLogValue>) {
        val storeId = currentStoreId ?: return
        val postId = params.longValue(PARAM_POST_ID) ?: return
        val stickers = params.stringValue(PARAM_STICKER_ID)?.let(::listOf).orEmpty()
        runMutation(screenRepository.putStorePostStickers(storeId, postId, stickers))
    }

    private fun mutateReviewSticker(params: Map<String, SDClickLogValue>) {
        val storeId = currentStoreId ?: return
        val reviewId = params.longValue(PARAM_REVIEW_ID) ?: return
        val stickers = params.stringValue(PARAM_STICKER_ID)?.let(::listOf).orEmpty()
        runMutation(homeRepository.putStickers(storeId.toString(), reviewId.toString(), stickers))
    }

    private fun <T> runMutation(
        responseFlow: Flow<BaseResponse<T>>,
        onSuccess: () -> Unit = {},
    ) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                responseFlow.collect { response ->
                    if (response.ok) {
                        onSuccess()
                        _hasUpdates.value = true
                        requestScreen(showLoading = false)
                    } else if (response.error == NOT_EXISTS_STORE) {
                        _events.emit(StoreDetailV2Event.CloseContainer(response.message))
                    } else {
                        _events.emit(StoreDetailV2Event.ShowMessage(response.message))
                    }
                }
            } catch (throwable: Throwable) {
                throwable.rethrowCancellation()
                _events.emit(StoreDetailV2Event.ShowMessage(null))
            }
        }
    }

    private fun requestScreen(showLoading: Boolean) {
        val storeId = currentStoreId ?: return
        loadJob?.cancel()
        if (showLoading) {
            _uiState.value = StoreDetailV2UiState.Loading(storeId)
        }
        loadJob = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                screenRepository.getStoreDetailScreen(
                    storeId = storeId,
                    deviceLatitude = currentDeviceLatitude,
                    deviceLongitude = currentDeviceLongitude,
                ).collect { response ->
                    if (currentStoreId != storeId) return@collect
                    val screen = response.data
                    if (response.ok && screen != null) {
                        _uiState.value = StoreDetailV2UiState.Content(storeId = storeId, screen = screen)
                    } else {
                        if (response.error == NOT_EXISTS_STORE) {
                            _events.emit(StoreDetailV2Event.CloseContainer(response.message))
                        } else {
                            _events.emit(StoreDetailV2Event.ShowMessage(response.message))
                        }
                        if (showLoading) {
                            _uiState.value = StoreDetailV2UiState.Error(
                                storeId = storeId,
                                message = response.message,
                                error = response.error,
                            )
                        }
                    }
                }
            } catch (throwable: Throwable) {
                throwable.rethrowCancellation()
                if (currentStoreId == storeId) {
                    _events.emit(StoreDetailV2Event.ShowMessage(null))
                    if (showLoading) {
                        _uiState.value = StoreDetailV2UiState.Error(
                            storeId = storeId,
                            message = null,
                            error = null,
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        loadJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val NOT_EXISTS_STORE = "not_exists_store"
        const val ACTION_PREVIEW_SHARE = "STORE_PREVIEW_SECTION_SHARE"
        const val ACTION_PREVIEW_NAVIGATION = "STORE_PREVIEW_SECTION_NAVIGATION"
        const val ACTION_PREVIEW_REVIEW_WRITE = "STORE_PREVIEW_SECTION_REVIEW_WRITE"
        const val ACTION_EDIT_UPDATE = "STORE_EDIT_SECTION_UPDATE"
        const val ACTION_EDIT_REPORT = "STORE_EDIT_SECTION_REPORT"
        const val ACTION_COUPON_ISSUE = "STORE_COUPON_SECTION_COUPON_ISSUE"
        const val ACTION_COUPON_USE = "STORE_COUPON_SECTION_COUPON_USE"
        const val ACTION_POST_ADD_LIKE = "STORE_POST_SECTION_ADD_LIKE"
        const val ACTION_POST_CANCEL_LIKE = "STORE_POST_SECTION_CANCEL_LIKE"
        const val ACTION_IMAGE_ADD = "STORE_IMAGE_SECTION_ADD_IMAGE"
        const val ACTION_IMAGE_ENLARGE = "STORE_IMAGE_SECTION_IMAGE_ENLARGE"
        const val ACTION_REVIEW_WRITE = "STORE_REVIEW_SECTION_REVIEW_WRITE"
        const val ACTION_REVIEW_REPORT = "STORE_REVIEW_SECTION_REPORT"
        const val ACTION_REVIEW_DELETE = "STORE_REVIEW_SECTION_DELETE"
        const val ACTION_REVIEW_ADD_LIKE = "STORE_REVIEW_SECTION_ADD_LIKE"
        const val ACTION_REVIEW_CANCEL_LIKE = "STORE_REVIEW_SECTION_CANCEL_LIKE"
        const val ACTION_MAP_COPY_ADDRESS = "STORE_MAP_SECTION_COPY_ADDRESS"
        const val ACTION_MAP_ENLARGE = "STORE_MAP_SECTION_MAP_ENLARGE"
        const val PARAM_COUPON_ID = "COUPON_ID"
        const val PARAM_ISSUED_KEY = "ISSUED_KEY"
        const val PARAM_POST_ID = "POST_ID"
        const val PARAM_REVIEW_ID = "REVIEW_ID"
        const val PARAM_STICKER_ID = "STICKER_ID"
    }
}

private fun Map<String, SDClickLogValue>.stringValue(key: String): String? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value
    is SDClickLogValue.IntValue -> value.value.toString()
    is SDClickLogValue.LongValue -> value.value.toString()
    is SDClickLogValue.DoubleValue -> value.value.toString()
    is SDClickLogValue.BoolValue -> value.value.toString()
    SDClickLogValue.Null, null -> null
}

private fun Map<String, SDClickLogValue>.longValue(key: String): Long? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value.toLongOrNull()
    is SDClickLogValue.IntValue -> value.value.toLong()
    is SDClickLogValue.LongValue -> value.value
    is SDClickLogValue.DoubleValue -> value.value.toLong()
    is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
}

private fun Throwable.rethrowCancellation() {
    if (this is CancellationException) throw this
}
