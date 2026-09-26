package com.zion830.threedollars.ui.storeDetail.sdui.viewModel

import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.text.SDHtmlText
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.domain.store.repository.StoreRepository
import com.zion830.threedollars.ui.storeDetail.displayitem.StoreDisplayItemController
import com.zion830.threedollars.ui.storeDetail.displayitem.StoreDisplayItemRequestPolicy
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver.Resolution
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailErrorMessage
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiLogger
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiEffect
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiState
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemEffect
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import com.threedollar.common.R as CommonR

/**
 * 가게 상세 v2 (SDUI). 홈 지도 시트와 전체 화면 상세가 같은 ViewModel 을 쓴다.
 */
@HiltViewModel
class StoreDetailSduiViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val homeRepository: HomeRepository,
    private val logger: StoreDetailSduiLogger,
) : UdfViewModel<StoreDetailSduiUiIntent, StoreDetailSduiUiState, StoreDetailSduiUiEffect>() {

    private val stateStore = MutableStateFlow(StoreDetailSduiUiState())
    override val state: StateFlow<StoreDetailSduiUiState> = stateStore.asStateFlow()

    private val _effect = Channel<StoreDetailSduiUiEffect>(capacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val effect: Flow<StoreDetailSduiUiEffect> = _effect.receiveAsFlow()

    private val previewStore = MutableStateFlow<SDStorePreviewSectionModel?>(null)

    /**
     * 홈 미리보기 시트(tip)가 그리는 PREVIEW. 상세와 같은 렌더러로 그려 tip → full 전환 때 위쪽이 그대로 이어진다.
     * 제보자 줄은 가이드상 full 에만 있어 `/preview` 응답 그대로 쓴다.
     */
    val preview: StateFlow<SDStorePreviewSectionModel?> = previewStore.asStateFlow()

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var loadVersion = 0
    private var pendingFragment: String? = null
    private var isDisplayed = false
    private var sentPageViewVersion = -1
    private var viewLog: SDLogModel? = null
    private val sentImpressionKeys = mutableSetOf<String>()
    private var displayItemRequestedStoreId: String? = null
    private var showsDisplayItems = false

    private val displayItemController = StoreDisplayItemController(
        scope = viewModelScope,
        storeRepository = storeRepository,
        homeRepository = homeRepository,
        screenName = ScreenName.STORE_DETAIL,
    )

    /** 활동 유도 모달(방문 인증 유도·없어진 가게 문의). */
    val displayItemState: StateFlow<StoreDetailDisplayItemState> = displayItemController.state

    init {
        viewModelScope.launch {
            displayItemController.effect.collect { effect ->
                when (effect) {
                    StoreDetailDisplayItemEffect.RefreshStoreDetail -> fetch(keepContent = true)
                    is StoreDetailDisplayItemEffect.ShowToast ->
                        sendEffect(StoreDetailSduiUiEffect.ShowToast(message = effect.message))
                }
            }
        }
        viewModelScope.launch {
            displayItemController.serverError.collect { sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(it)) }
        }
    }

    override fun dispatch(intent: StoreDetailSduiUiIntent) {
        when (intent) {
            is StoreDetailSduiUiIntent.Load -> load(intent)
            StoreDetailSduiUiIntent.Refresh -> fetch(keepContent = true)
            is StoreDetailSduiUiIntent.OnAction -> onAction(intent)
            StoreDetailSduiUiIntent.OnFavoriteClick -> toggleFavorite()
            StoreDetailSduiUiIntent.OnDisplayed -> onDisplayed()
            is StoreDetailSduiUiIntent.OnImpression -> onImpression(intent.key, intent.log)
            is StoreDetailSduiUiIntent.ScrollToFragment -> scrollToFragment(intent.fragment)
            is StoreDetailSduiUiIntent.OnReviewDeleteConfirmed -> deleteReview(intent.reviewId)
            is StoreDetailSduiUiIntent.OnCouponUseConfirmed -> useCoupon(intent.issuedKey)
            is StoreDetailSduiUiIntent.OnStoreReportSubmit -> reportStore(intent.deleteReasonType)
            is StoreDetailSduiUiIntent.OnReviewReportSubmit -> reportReview(intent)
            is StoreDetailSduiUiIntent.OnReviewSubmit -> postReview(intent)
            is StoreDetailSduiUiIntent.OnImagesSelected -> uploadImages(intent.images)
            StoreDetailSduiUiIntent.OnStoreChanged -> fetch(keepContent = true)
            is StoreDetailSduiUiIntent.OnDisplayItemDisplayed -> displayItemController.onDisplayed(intent.item)
            is StoreDetailSduiUiIntent.OnVisitInducementClick -> displayItemController.onVisitClick(intent.isOpened)
            is StoreDetailSduiUiIntent.OnDisappearanceReasonClick -> displayItemController.onReasonClick(intent.reason)
            StoreDetailSduiUiIntent.OnDisappearanceReportClick -> displayItemController.onReportClick()
        }
    }

    override fun onException(exception: Throwable, tag: Any?) {
        super.onException(exception, tag)
        if (tag is LoadTag && tag.version != loadVersion) return
        stateStore.update { it.copy(isLoading = false, isUploading = false) }
        sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(StoreDetailErrorMessage.from(exception)))
    }

    private fun load(intent: StoreDetailSduiUiIntent.Load) {
        latitude = intent.latitude
        longitude = intent.longitude
        showsDisplayItems = intent.showsDisplayItems
        if (!intent.startsNewSession && intent.storeId == stateStore.value.storeId && stateStore.value.hasContent) {
            intent.fragment?.let(::scrollToFragment)
            if (intent.withPreview && previewStore.value == null) fetchPreview(intent.storeId)
            return
        }
        pendingFragment = intent.fragment
        if (stateStore.value.storeId.isNotBlank()) isDisplayed = false
        if (intent.startsNewSession) displayItemRequestedStoreId = null
        sentImpressionKeys.clear()
        stateStore.value = StoreDetailSduiUiState(storeId = intent.storeId)
        previewStore.value = null
        fetch(keepContent = false)
        if (intent.withPreview) fetchPreview(intent.storeId)
    }

    /**
     * 미리보기는 실패해도 알리지 않는다. 홈이 목록 카드로 만든 임시 미리보기를 그대로 둔다.
     * 상세보다 먼저 오면 저장 여부·가게명을 미리 채워 tip 의 저장 버튼이 맞게 보이도록 한다.
     */
    private fun fetchPreview(storeId: String) {
        launch {
            val preview = storeRepository.getStorePreviewScreen(storeId = storeId, lat = latitude, lng = longitude)
                .getOrNull()
                ?.sections.orEmpty()
                .filterIsInstance<SDStorePreviewSectionModel>()
                .firstOrNull()
                ?: return@launch
            if (stateStore.value.storeId != storeId) return@launch
            previewStore.value = preview
            stateStore.update {
                if (it.hasContent) {
                    it
                } else {
                    it.copy(
                        isFavorite = preview.additionalInfos?.isSubscriber ?: it.isFavorite,
                        storeType = preview.additionalInfos?.storeType ?: it.storeType,
                        storeName = SDHtmlText.plainText(preview.header?.title?.text).ifBlank { it.storeName },
                    )
                }
            }
        }
    }

    private fun fetch(keepContent: Boolean) {
        val storeId = stateStore.value.storeId.takeIf { it.isNotBlank() } ?: return
        val version = ++loadVersion
        if (!keepContent) stateStore.update { it.copy(isLoading = true) }

        launch(tag = LoadTag(version)) {
            val result = storeRepository.getStoreScreenV2(storeId = storeId, lat = latitude, lng = longitude)
            if (version != loadVersion) return@launch
            result
                .onSuccess { screen ->
                    val sections = screen.sections.orEmpty().filter { it.type != SDSectionType.UNKNOWN }
                    val preview = sections.filterIsInstance<SDStorePreviewSectionModel>().firstOrNull()
                    viewLog = screen.viewLog
                    stateStore.update {
                        it.copy(
                            isLoading = false,
                            sections = sections,
                            isFavorite = preview?.additionalInfos?.isSubscriber ?: it.isFavorite,
                            storeType = preview?.additionalInfos?.storeType ?: it.storeType,
                            storeName = SDHtmlText.plainText(preview?.header?.title?.text).ifBlank { it.storeName },
                        )
                    }
                    if (!keepContent) sendPageViewIfNeeded()
                    pendingFragment?.let {
                        pendingFragment = null
                        scrollToFragment(it)
                    }
                }
                .onFailure { throwable ->
                    stateStore.update { it.copy(isLoading = false) }
                    if (throwable is StoreNotExistsException) {
                        sendEffect(StoreDetailSduiUiEffect.Close(message = throwable.message, asAlert = true))
                    } else {
                        sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(StoreDetailErrorMessage.from(throwable)))
                    }
                }
        }
    }

    private fun onDisplayed() {
        isDisplayed = true
        sendPageViewIfNeeded()
    }

    private fun sendPageViewIfNeeded() {
        requestDisplayItemsIfNeeded()
        if (!isDisplayed || !stateStore.value.hasContent || sentPageViewVersion == loadVersion) return
        sentPageViewVersion = loadVersion
        logger.pageView(viewLog)
    }

    private fun requestDisplayItemsIfNeeded() {
        val storeId = stateStore.value.storeId
        val latitude = latitude
        val longitude = longitude
        val shouldRequest = showsDisplayItems && isDisplayed && StoreDisplayItemRequestPolicy.shouldRequest(
            storeId = storeId,
            requestedStoreId = displayItemRequestedStoreId,
            hasContent = stateStore.value.hasContent,
            hasLocation = latitude != null && longitude != null,
        )
        if (!shouldRequest || latitude == null || longitude == null) return
        val numericStoreId = storeId.toIntOrNull() ?: return
        displayItemRequestedStoreId = storeId
        displayItemController.load(numericStoreId, latitude, longitude)
    }

    private fun onImpression(key: String, log: SDLogModel?) {
        if (log == null || !sentImpressionKeys.add(key)) return
        logger.impression(log)
    }

    private fun scrollToFragment(fragment: String) {
        val sections = stateStore.value.sections
        if (sections.isEmpty()) {
            pendingFragment = fragment
            return
        }
        StoreSectionFragment.resolveIndex(sections, fragment)?.let {
            sendEffect(StoreDetailSduiUiEffect.ScrollToSection(it))
        }
    }

    private fun onAction(intent: StoreDetailSduiUiIntent.OnAction) {
        logger.click(intent.event.clickLog)
        val current = stateStore.value
        val context = StoreDetailActionResolver.Context(
            storeId = current.storeId,
            isBossStore = current.storeType == BOSS_STORE,
            storeName = current.storeName,
            sections = current.sections,
        )
        when (val resolution = StoreDetailActionResolver.resolve(intent.event, context)) {
            is Resolution.Navigate -> sendEffect(StoreDetailSduiUiEffect.Navigate(resolution.destination))
            is Resolution.ScrollTo -> sendEffect(StoreDetailSduiUiEffect.ScrollToSection(resolution.index))
            is Resolution.LikeReview -> launch {
                storeRepository.putStoreReviewSticker(resolution.storeId, resolution.reviewId, resolution.stickerId)
                    .refreshOrShowError()
            }
            is Resolution.LikePost -> launch {
                storeRepository.putStorePostSticker(resolution.storeId, resolution.postId, resolution.stickerId)
                    .refreshOrShowError()
            }
            is Resolution.IssueCoupon -> launch {
                storeRepository.issueStoreCoupon(resolution.storeId, resolution.couponId)
                    .refreshOrShowError(successMessage = CommonR.string.store_detail_coupon_issued)
            }
            is Resolution.ReportReview -> fetchReviewReportReasons(resolution)
            Resolution.Ignore -> Unit
        }
    }

    private fun toggleFavorite() {
        val current = stateStore.value
        if (current.storeId.isBlank()) return
        val willFavorite = !current.isFavorite
        launch {
            val flow = if (willFavorite) homeRepository.putFavorite(current.storeId) else homeRepository.deleteFavorite(current.storeId)
            val response = flow.first()
            if (response.ok) {
                stateStore.update { it.copy(isFavorite = willFavorite) }
                sendEffect(
                    StoreDetailSduiUiEffect.ShowToast(
                        messageRes = if (willFavorite) CommonR.string.store_detail_favorite_added else CommonR.string.store_detail_favorite_removed
                    )
                )
            } else {
                sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response.message))
            }
        }
    }

    private fun deleteReview(reviewId: String) = launch {
        storeRepository.deleteStoreReview(reviewId).refreshOrShowError()
    }

    private fun useCoupon(issuedKey: String) = launch {
        storeRepository.useIssuedCoupon(issuedKey).refreshOrShowError(successMessage = CommonR.string.store_detail_coupon_used)
    }

    private fun reportStore(deleteReasonType: String) {
        val storeId = stateStore.value.storeId.toIntOrNull() ?: return
        launch {
            val response = homeRepository.deleteStore(storeId, deleteReasonType).first()
            if (response.ok) {
                sendEffect(StoreDetailSduiUiEffect.Close(messageRes = CommonR.string.report_completed))
            } else {
                sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response.message))
            }
        }
    }

    private fun reportReview(intent: StoreDetailSduiUiIntent.OnReviewReportSubmit) {
        val storeId = stateStore.value.storeId.toIntOrNull() ?: return
        launch {
            val response = homeRepository.reportStoreReview(storeId, intent.reviewId, intent.request).first()
            if (response.ok) {
                sendEffect(StoreDetailSduiUiEffect.ShowToast(messageRes = CommonR.string.report_completed))
                fetch(keepContent = true)
            } else {
                sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response.message))
            }
        }
    }

    private fun postReview(intent: StoreDetailSduiUiIntent.OnReviewSubmit) {
        val storeId = stateStore.value.storeId.toIntOrNull() ?: return
        launch {
            val response = homeRepository.postStoreReview(intent.contents, intent.rating, storeId).first()
            if (response.ok) {
                fetch(keepContent = true)
            } else {
                sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response.message))
            }
        }
    }

    private fun uploadImages(images: List<MultipartBody.Part>) {
        val storeId = stateStore.value.storeId.toIntOrNull() ?: return
        if (images.isEmpty()) return
        stateStore.update { it.copy(isUploading = true) }
        launch {
            val response = homeRepository.saveImages(images, storeId)
            stateStore.update { it.copy(isUploading = false) }
            if (response?.ok == true) {
                fetch(keepContent = true)
            } else {
                sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response?.message))
            }
        }
    }

    private fun fetchReviewReportReasons(resolution: Resolution.ReportReview) = launch {
        val response = homeRepository.getReportReasons(ReportReasonsGroupType.REVIEW).first()
        if (response.ok) {
            val destination = StoreDetailDestination.ReportReview(
                storeId = resolution.storeId,
                reviewId = resolution.reviewId,
                reasons = response.data?.reasonModels.orEmpty(),
            )
            sendEffect(StoreDetailSduiUiEffect.Navigate(destination))
        } else {
            sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(response.message))
        }
    }

    private fun Result<Unit>.refreshOrShowError(successMessage: Int? = null) {
        onSuccess {
            successMessage?.let { sendEffect(StoreDetailSduiUiEffect.ShowToast(messageRes = it)) }
            fetch(keepContent = true)
        }
        onFailure { sendEffect(StoreDetailSduiUiEffect.ShowErrorAlert(StoreDetailErrorMessage.from(it))) }
    }

    private fun sendEffect(effect: StoreDetailSduiUiEffect) {
        _effect.trySend(effect)
    }

    private data class LoadTag(val version: Int)

    companion object {
        const val BOSS_STORE = "BOSS_STORE"
    }
}
