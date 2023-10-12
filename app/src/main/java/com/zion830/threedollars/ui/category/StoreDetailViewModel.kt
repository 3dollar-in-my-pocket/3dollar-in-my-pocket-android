package com.zion830.threedollars.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.FavoriteModel
import com.home.domain.data.store.ImageContentModel
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.repository.HomeRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import com.zion830.threedollars.datasource.model.v2.request.NewReviewRequest
import com.zion830.threedollars.datasource.model.v2.response.store.Image
import com.zion830.threedollars.ui.report_store.DeleteType
import com.zion830.threedollars.utils.StringUtils
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

// TODO : Edit 로직 분리 필요
@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val repository: StoreDataSource,
) :
    BaseViewModel() {

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

    private val _isExistStoreInfo: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()
    val isExistStoreInfo: LiveData<Pair<Int, Boolean>> get() = _isExistStoreInfo

    private val _favoriteModel: MutableStateFlow<FavoriteModel> = MutableStateFlow(FavoriteModel())
    val favoriteModel: StateFlow<FavoriteModel> get() = _favoriteModel

    private val _imageContentModelList: MutableStateFlow<List<ImageContentModel>> = MutableStateFlow(listOf())
    val imageContentModelList: StateFlow<List<ImageContentModel>> get() = _imageContentModelList

    fun getUserStoreDetail(
        storeId: Int,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
        storeImagesCount: Int? = null,
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
                            _imageContentModelList.value = data.images.contents
                        }
                    } else {
                        _serverError.emit(it.message)
                    }
                }
            } else {
                // TODO: 위도 경도 확인하게하는 토스트 메시지
            }
        }
    }

    fun addReview(content: String, rating: Float) {
        if (content.isBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val request = NewReviewRequest(content, rating, _userStoreDetailModel.value?.store?.storeId ?: -1)
            val response = repository.addReview(request)
            if (response.isSuccessful) {
                _msgTextId.postValue(R.string.success_add_review)
                _addReviewResult.postValue(true)
            } else {
                _addReviewResult.postValue(false)
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

    fun editReview(reviewId: Int, newReview: NewReview) {
        if (newReview.contents.isBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val request = EditReviewRequest(newReview.contents, newReview.rating)
            repository.editReview(reviewId, request)
            _msgTextId.postValue(R.string.success_edit_review)
            _addReviewResult.postValue(true)
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteReview(reviewId)
            _msgTextId.postValue(R.string.success_delete_review)
            _addReviewResult.postValue(true)
        }
    }

    fun saveImages(images: List<MultipartBody.Part>) {
        if (images.isEmpty()) {
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            _uploadImageStatus.emit(true)
            homeRepository.saveImages(images, _userStoreDetailModel.value?.store?.storeId ?: -1).collect {
                _uploadImageStatus.emit(false)
            }
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun deletePhoto(selectedImage: ImageContentModel) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteImage(selectedImage.imageId).collect { _photoDeleted.emit(it.ok) }
        }
    }

    fun putFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putFavorite(storeType, storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(StringUtils.getString(R.string.toast_favorite_add))
                    _favoriteModel.update {
                        it.copy(isFavorite = true, totalSubscribersCount = it.totalSubscribersCount + 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun deleteFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(storeType, storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(StringUtils.getString(R.string.toast_favorite_delete))
                    _favoriteModel.update {
                        it.copy(isFavorite = false, totalSubscribersCount = it.totalSubscribersCount - 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
        hideLoading()
    }
}