package com.zion830.threedollars.ui.storeDetail.sdui.viewModel

import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.text.SDHtmlText
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.domain.store.repository.StoreRepository
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver.Resolution
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailErrorMessage
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiLogger
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiEffect
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiState
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
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

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var loadVersion = 0
    private var pendingFragment: String? = null
    private var isDisplayed = false
    private var sentPageViewVersion = -1
    private var viewLog: SDLogModel? = null
    private val sentImpressionKeys = mutableSetOf<String>()

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
        if (intent.storeId == stateStore.value.storeId && stateStore.value.hasContent) {
            intent.fragment?.let(::scrollToFragment)
            return
        }
        pendingFragment = intent.fragment
        if (stateStore.value.storeId.isNotBlank()) isDisplayed = false
        sentImpressionKeys.clear()
        stateStore.value = StoreDetailSduiUiState(storeId = intent.storeId)
        fetch(keepContent = false)
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
        if (!isDisplayed || !stateStore.value.hasContent || sentPageViewVersion == loadVersion) return
        sentPageViewVersion = loadVersion
        logger.pageView(viewLog)
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
