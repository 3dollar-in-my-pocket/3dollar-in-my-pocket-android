package com.zion830.threedollars.ui.storeDetail.user.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.threedollar.domain.home.data.store.DeleteType
import com.threedollar.domain.home.data.store.FavoriteModel
import com.threedollar.domain.home.data.store.ImageContentModel
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewSortType
import com.threedollar.domain.home.data.store.UserStoreDetailModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.domain.store.model.StoreDisplayItemModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.ImpressionEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.network.sdui.model.section.SDSectionType
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItem
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemEffect
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemState
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailViewSessionCounter
import com.zion830.threedollars.utils.StringUtils
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import com.threedollar.common.R as CommonR

// TODO : Edit 로직 분리 필요
@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val storeRepository: StoreRepository
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.STORE_DETAIL

    private val _userStoreDetailModel: MutableStateFlow<UserStoreDetailModel?> = MutableStateFlow(null)
    val userStoreDetailModel: StateFlow<UserStoreDetailModel?> get() = _userStoreDetailModel

    private val _selectedLocation: MutableLiveData<LatLng?> = MutableLiveData()

    private val _uploadImageStatus: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val uploadImageStatus: SharedFlow<Boolean> get() = _uploadImageStatus

    private val _addReviewResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val addReviewResult: LiveData<Boolean>
        get() = _addReviewResult

    private val _photoDeleted: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val photoDeleted: SharedFlow<Boolean> get() = _photoDeleted

    private val _isDeleteStore: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDeleteStore: StateFlow<Boolean> get() = _isDeleteStore

    private val _favoriteModel: MutableStateFlow<FavoriteModel> = MutableStateFlow(FavoriteModel())
    val favoriteModel: StateFlow<FavoriteModel> get() = _favoriteModel

    private val _imagePagingData = MutableStateFlow<PagingData<ImageContentModel>?>(null)
    val imagePagingData get() = _imagePagingData

    private val _reviewPagingData = MutableStateFlow<PagingData<ReviewContentModel>?>(null)
    val reviewPagingData get() = _reviewPagingData

    private val _reportReasons = MutableStateFlow<List<ReasonModel>?>(null)
    val reportReasons: StateFlow<List<ReasonModel>?> get() = _reportReasons

    private val _reviewSuccessEvent = MutableSharedFlow<Boolean>()
    val reviewSuccessEvent: SharedFlow<Boolean> get() = _reviewSuccessEvent

    private val _relatedStoreSection = MutableStateFlow<SDRelatedStoresSectionModel?>(null)
    val relatedStoreSection = _relatedStoreSection.asStateFlow()

    private val _displayItemState = MutableStateFlow(StoreDetailDisplayItemState())
    val displayItemState = _displayItemState.asStateFlow()

    private val _displayItemEffect = MutableSharedFlow<StoreDetailDisplayItemEffect>()
    val displayItemEffect = _displayItemEffect.asSharedFlow()

    private val pendingDisplayItems = ArrayDeque<StoreDisplayItemModel>()
    private var displayItemShowJob: Job? = null
    private var displayItemAutoDismissJob: Job? = null
    private var displayItemStoreId: Int? = null

    init {
        getReportReasons()
    }

    fun getUserStoreDetail(
        storeId: Int,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
        storeImagesCount: Int? = 30,
        reviewsCount: Int? = null,
        visitHistoriesCount: Int? = null,
        filterVisitStartDate: String,
    ) {
        if (deviceLatitude != null && deviceLongitude != null) {
            getStoreDisplayItems(
                storeId = storeId,
                deviceLatitude = deviceLatitude,
                deviceLongitude = deviceLongitude,
            )
        }

        viewModelScope.launch {
            storeRepository.getScreenStore(
                storeId = storeId,
                lat = deviceLatitude ?: 0.0,
                lng = deviceLongitude ?: 0.0
            ).onSuccess { ret ->
                _relatedStoreSection.update {
                    ret.sections?.find { it.type == SDSectionType.RELATED_STORES } as? SDRelatedStoresSectionModel
                }
            }.onFailure {
                _reportReasons.update { null }
            }
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            if (deviceLatitude != null && deviceLongitude != null) {
                homeRepository.getUserStoreDetail(
                    storeId = storeId,
                    deviceLatitude = deviceLatitude,
                    deviceLongitude = deviceLongitude,
                    storeImagesCount = storeImagesCount,
                    reviewsCount = reviewsCount,
                    visitHistoriesCount = visitHistoriesCount,
                    filterVisitStartDate = filterVisitStartDate
                ).collect {
                    if (it.ok) {
                        it.data?.let { data ->
                            _userStoreDetailModel.value = data
                            _favoriteModel.value = data.favorite
                        }
                    } else {
                        if (it.error == "not_exists_store") {
                            _isDeleteStore.value = true
                        }
                        _serverError.emit(it.message)
                    }
                }
            } else {
                // TODO: 위도 경도 확인하게하는 토스트 메시지
            }
        }
    }

    fun deleteStore(deleteType: DeleteType) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteStore(userStoreDetailModel.value?.store?.storeId ?: -1, deleteType.key).collect {
                if (!it.ok) {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun onDisplayItemDisplayed(item: StoreDetailDisplayItem) {
        recordDisplayItemImpression(item.storeId, item.itemType)
        sendDisplayItemImpressionLog(item)
        scheduleAutoDismiss(item.trigger?.displayDurationSeconds)
    }

    fun onVisitInducementClick(isOpened: Boolean) {
        val item = displayItemState.value.item as? StoreDetailDisplayItem.VisitInducement ?: return
        if (item.isSubmitting) return

        cancelAutoDismiss()
        updateCurrentDisplayItem(item.copy(isSubmitting = true))
        sendClickVisitInducement(isOpened)

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postStoreVisit(
                storeId = item.storeId,
                visitType = if (isOpened) VISIT_TYPE_EXISTS else VISIT_TYPE_NOT_EXISTS,
            ).collect { response ->
                if (response.ok) {
                    dismissCurrentDisplayItem(showNext = true)
                    _displayItemEffect.emit(StoreDetailDisplayItemEffect.RefreshStoreDetail)
                    _displayItemEffect.emit(StoreDetailDisplayItemEffect.ShowToast(StringUtils.getString(CommonR.string.display_item_modal_thanks_toast)))
                } else {
                    updateCurrentDisplayItem(item.copy(isSubmitting = false))
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun onDisappearanceReasonClick(reason: ReasonModel) {
        val item = displayItemState.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return
        if (item.isSubmitting) return

        updateCurrentDisplayItem(item.copy(selectedReason = reason))
        sendSelectReasonLog(item.storeId, reason.type)
        scheduleAutoDismiss(item.trigger?.displayDurationSeconds)
    }

    fun onDisappearanceReportClick() {
        val item = displayItemState.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return
        if (item.isSubmitting) return
        val reason = item.selectedReason ?: return

        cancelAutoDismiss()
        updateCurrentDisplayItem(item.copy(isSubmitting = true))
        sendClickDisappearanceReport(item.storeId, reason.type)

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteStore(item.storeId, reason.type).collect { response ->
                if (response.ok) {
                    dismissCurrentDisplayItem(showNext = true)
                    _displayItemEffect.emit(StoreDetailDisplayItemEffect.ShowToast(StringUtils.getString(CommonR.string.display_item_modal_thanks_toast)))
                } else {
                    updateCurrentDisplayItem(item.copy(isSubmitting = false))
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun putStoreReview(reviewId: Long, content: String, rating: Int) {
        if (content.isBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putStoreReview(reviewId, content, rating).collect {
                if (it.ok) {
                    _msgTextId.postValue(CommonR.string.success_edit_review)
                    _addReviewResult.postValue(true)
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun saveImages(images: List<MultipartBody.Part>, storeId: Int) {
        if (images.isEmpty()) {
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            _uploadImageStatus.emit(true)
            val result = homeRepository.saveImages(
                images = images,
                storeId = storeId
            )
            if (result == null) {
                _serverError.emit("인터넷이 불안정해요.")
            } else {
                getImage(storeId)
            }
            _uploadImageStatus.emit(false)
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun deletePhoto(selectedImage: ImageContentModel?) {
        viewModelScope.launch(coroutineExceptionHandler) {
            selectedImage?.let {
                homeRepository.deleteImage(selectedImage.imageId).collect { _photoDeleted.emit(it.ok) }
            }
        }
    }

    fun putFavorite(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putFavorite(storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(StringUtils.getString(CommonR.string.toast_favorite_add))
                    _favoriteModel.update {
                        it.copy(isFavorite = true, totalSubscribersCount = it.totalSubscribersCount + 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun deleteFavorite(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(StringUtils.getString(CommonR.string.toast_favorite_delete))
                    _favoriteModel.update {
                        it.copy(isFavorite = false, totalSubscribersCount = it.totalSubscribersCount - 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun getImage(storeId: Int) {
        viewModelScope.launch {
            homeRepository.getStoreImages(storeId).cachedIn(viewModelScope).collect {
                _imagePagingData.value = it
            }
        }
    }

    fun postStoreReview(content: String, rating: Int, storeId: Int?) {
        viewModelScope.launch {
            storeId?.let {
                homeRepository.postStoreReview(contents = content, rating = rating, storeId = storeId).collect {
                    if (it.ok) {
                        _reviewSuccessEvent.emit(true)
                    } else {
                        _serverError.emit(it.message)
                    }
                }
            }
        }
    }

    fun getReview(storeId: Int, sortType: ReviewSortType) {
        viewModelScope.launch {
            homeRepository.getStoreReview(storeId, sortType).cachedIn(viewModelScope).collect {
                _reviewPagingData.value = it
            }
        }
    }

    fun reportReview(storeId: Int, reviewId: Long, reportReviewModelRequest: ReportReviewModelRequest) {
        viewModelScope.launch {
            homeRepository.reportStoreReview(storeId, reviewId, reportReviewModelRequest).collect {
                if (it.ok) {
                    getReview(storeId, ReviewSortType.LATEST)
                    showToast("신고 완료!")
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun getReportReasons() {
        viewModelScope.launch {
            homeRepository.getReportReasons(ReportReasonsGroupType.REVIEW).collect {
                if (it.ok) {
                    _reportReasons.value = it.data?.reasonModels
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun getStoreDisplayItems(
        storeId: Int,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ) {
        viewModelScope.launch {
            val viewCount = StoreDetailViewSessionCounter.increment(storeId)
            storeRepository.getStoreDisplayItems(
                storeId = storeId,
                lat = deviceLatitude,
                lng = deviceLongitude,
                itemTypes = DISPLAY_ITEM_TYPES,
            ).onSuccess { response ->
                displayItemStoreId = storeId
                pendingDisplayItems.clear()
                pendingDisplayItems.addAll(
                    response.contents.filter { item ->
                        item.isVisible &&
                            item.itemType != StoreDisplayItemType.UNKNOWN &&
                            (item.trigger?.conditions?.sessionViewCountRange?.contains(viewCount) ?: true)
                    },
                )
                if (displayItemState.value.item == null && displayItemShowJob?.isActive != true) {
                    showNextDisplayItem()
                }
            }
        }
    }

    private fun showNextDisplayItem() {
        val item = pendingDisplayItems.removeFirstOrNull() ?: return
        displayItemShowJob?.cancel()
        displayItemShowJob = viewModelScope.launch {
            delay(item.trigger?.displayAfterSeconds.toDelayMillis())
            val modal = item.toDisplayItem(displayItemStoreId ?: return@launch)
            _displayItemState.value = StoreDetailDisplayItemState(item = modal, isVisible = true)
            if (modal is StoreDetailDisplayItem.DisappearanceInquiry) {
                getStoreReportReasons(modal.storeId)
            }
        }
    }

    private fun getStoreReportReasons(storeId: Int) {
        viewModelScope.launch {
            homeRepository.getReportReasons(ReportReasonsGroupType.STORE).collect { response ->
                if (response.ok) {
                    val item = displayItemState.value.item as? StoreDetailDisplayItem.DisappearanceInquiry ?: return@collect
                    if (item.storeId == storeId) {
                        updateCurrentDisplayItem(
                            item.copy(
                                reasons = response.data?.reasonModels ?: listOf(),
                                isReasonLoading = false,
                            ),
                        )
                    }
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun dismissCurrentDisplayItem(showNext: Boolean) {
        cancelAutoDismiss()
        displayItemShowJob?.cancel()
        val currentItem = displayItemState.value.item ?: return
        _displayItemState.value = StoreDetailDisplayItemState(item = currentItem, isVisible = false)
        viewModelScope.launch {
            delay(DISPLAY_ITEM_SLIDE_OUT_MILLIS)
            _displayItemState.value = StoreDetailDisplayItemState()
            if (showNext) {
                showNextDisplayItem()
            }
        }
    }

    private fun updateCurrentDisplayItem(item: StoreDetailDisplayItem) {
        _displayItemState.update { state ->
            if (state.item?.itemType == item.itemType) {
                state.copy(item = item)
            } else {
                state
            }
        }
    }

    private fun recordDisplayItemImpression(storeId: Int, itemType: StoreDisplayItemType) {
        viewModelScope.launch {
            storeRepository.postStoreDisplayItemImpression(
                storeId = storeId,
                itemTypes = listOf(itemType),
            )
        }
    }

    private fun scheduleAutoDismiss(displayDurationSeconds: Double?) {
        cancelAutoDismiss()
        displayDurationSeconds ?: return
        displayItemAutoDismissJob = viewModelScope.launch {
            delay(displayDurationSeconds.toDelayMillis())
            dismissCurrentDisplayItem(showNext = true)
        }
    }

    private fun cancelAutoDismiss() {
        displayItemAutoDismissJob?.cancel()
        displayItemAutoDismissJob = null
    }

    // GA Events - Review Bottom Sheet
    fun sendClickWriteReviewSubmit(rating: Int) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_BOTTOM_SHEET,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW,
                additionalParams = mapOf(ParameterName.VALUE to rating.toString())
            )
        )
    }

    // GA Events - Report Store
    fun sendClickReportStore(reportType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REPORT_STORE,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT,
                additionalParams = mapOf(ParameterName.VALUE to reportType)
            )
        )
    }

    // GA Events - Store Detail
    fun sendClickFavorite(isOn: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.FAVORITE,
                additionalParams = mapOf(ParameterName.VALUE to if (isOn) "on" else "off")
            )
        )
    }

    fun sendClickShare() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SHARE
            )
        )
    }

    fun sendClickWriteReview() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW
            )
        )
    }

    fun sendClickNavigation() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.NAVIGATION
            )
        )
    }

    fun sendClickZoomMap() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.ZOOM_MAP
            )
        )
    }

    fun sendClickCopyAddress() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.COPY_ADDRESS
            )
        )
    }

    fun sendClickVisit() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT
            )
        )
    }

    fun sendClickReportButton() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT
            )
        )
    }

    private fun sendDisplayItemImpressionLog(item: StoreDetailDisplayItem) {
        LogManager.sendEvent(
            ImpressionEvent(
                screen = screenName,
                objectType = LogObjectType.BANNER,
                objectId = item.itemType.toLogObjectId(),
                additionalParams = mapOf(ParameterName.STORE_ID to item.storeId.toString()),
            ),
        )
    }

    private fun sendClickVisitInducement(isOpened: Boolean) {
        val item = displayItemState.value.item as? StoreDetailDisplayItem.VisitInducement ?: return
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

    // GA Events - Review List
    fun sendClickSortReviewList(sortType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SORT,
                additionalParams = mapOf(ParameterName.VALUE to sortType)
            )
        )
    }

    fun sendClickWriteReviewFromList() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW
            )
        )
    }

    fun sendClickReportReviewFromList() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT
            )
        )
    }

    fun sendClickDeleteReviewFromList() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.DELETE_REVIEW
            )
        )
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(CommonR.string.connection_failed)
        hideLoading()
    }

    private fun StoreDisplayItemModel.toDisplayItem(storeId: Int): StoreDetailDisplayItem =
        when (itemType) {
            StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL -> StoreDetailDisplayItem.DisappearanceInquiry(
                storeId = storeId,
                trigger = trigger,
            )

            StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL -> StoreDetailDisplayItem.VisitInducement(
                storeId = storeId,
                trigger = trigger,
            )

            StoreDisplayItemType.UNKNOWN -> error("Unknown display item cannot be shown.")
        }

    private fun StoreDisplayItemType.toLogObjectId(): LogObjectId =
        when (this) {
            StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL -> LogObjectId.DISAPPEARANCE_INQUIRY_MODAL
            StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL -> LogObjectId.VISIT_INDUCEMENT_MODAL
            StoreDisplayItemType.UNKNOWN -> LogObjectId.STORE
        }

    private fun Double?.toDelayMillis(): Long =
        ((this ?: 0.0).coerceAtLeast(0.0) * 1_000).toLong()

    companion object {
        private const val VISIT_TYPE_EXISTS = "EXISTS"
        private const val VISIT_TYPE_NOT_EXISTS = "NOT_EXISTS"
        private const val DISPLAY_ITEM_SLIDE_OUT_MILLIS = 300L

        private val DISPLAY_ITEM_TYPES = listOf(
            StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL,
            StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL,
        )
    }
}
