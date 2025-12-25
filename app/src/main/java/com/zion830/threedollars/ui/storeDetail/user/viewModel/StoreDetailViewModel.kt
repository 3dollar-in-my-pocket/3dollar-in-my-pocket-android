package com.zion830.threedollars.ui.storeDetail.user.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.home.domain.data.store.DeleteType
import com.home.domain.data.store.FavoriteModel
import com.home.domain.data.store.ImageContentModel
import com.home.domain.data.store.ReasonModel
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewSortType
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.ReportReasonsGroupType
import com.home.domain.request.ReportReviewModelRequest
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.utils.StringUtils
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import com.threedollar.common.R as CommonR

// TODO : Edit 로직 분리 필요
@HiltViewModel
class StoreDetailViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

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

    fun putStoreReview(reviewId: Int, content: String, rating: Int) {
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

    fun reportReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest) {
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

    // GA Events - Edit Store Info
    fun sendClickEditStoreInfo() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.EDIT_STORE_INFO,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.EDIT
            )
        )
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(CommonR.string.connection_failed)
        hideLoading()
    }
}