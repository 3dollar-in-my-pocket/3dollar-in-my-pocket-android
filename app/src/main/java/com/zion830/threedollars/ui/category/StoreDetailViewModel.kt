package com.zion830.threedollars.ui.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.repository.HomeRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.MyMenu
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import com.zion830.threedollars.datasource.model.v2.request.NewReviewRequest
import com.zion830.threedollars.datasource.model.v2.response.store.Image
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.ui.report_store.DeleteType
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _userStoreDetailModel: MutableStateFlow<UserStoreDetailModel> = MutableStateFlow(UserStoreDetailModel())
    val userStoreDetailModel: StateFlow<UserStoreDetailModel> get() = _userStoreDetailModel

    private val _selectedLocation: MutableLiveData<LatLng?> = MutableLiveData()
    val selectedLocation: LiveData<LatLng?>
        get() = _selectedLocation

    private val _deleteStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private val _uploadImageStatus: MutableLiveData<Boolean> = MutableLiveData(false)
    val uploadImageStatus: LiveData<Boolean>
        get() = _uploadImageStatus

    private val _closeActivity: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val closeActivity: LiveData<Boolean>
        get() = _closeActivity

    private val _deleteType: MutableLiveData<DeleteType> = MutableLiveData(DeleteType.NONE)
    val deleteType: LiveData<DeleteType>
        get() = _deleteType

    private val _addReviewResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val addReviewResult: LiveData<Boolean>
        get() = _addReviewResult

    private val _photoDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val photoDeleted: LiveData<Boolean>
        get() = _photoDeleted

    private val _isExistStoreInfo: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()
    val isExistStoreInfo: LiveData<Pair<Int, Boolean>> get() = _isExistStoreInfo

    // TODO: SingleLiveData로 수정 필요
    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

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
                        _userStoreDetailModel.value = it.data
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
            val request = NewReviewRequest(content, rating, _userStoreDetailModel.value.store?.storeId ?: -1)
            val response = repository.addReview(request)
            if (response.isSuccessful) {
                _msgTextId.postValue(R.string.success_add_review)
                _addReviewResult.postValue(true)
            } else {
                _addReviewResult.postValue(false)
            }
        }
    }

    fun deleteStore() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = repository.deleteStore(_userStoreDetailModel.value.store?.storeId ?: -1, deleteType.value!!.key)

            _deleteStoreResult.postValue(result.isSuccessful)
            if (result.body()?.data?.isDeleted == true) {
                _closeActivity.postValue(result.body()?.data?.isDeleted)
            }
            _msgTextId.postValue(
                when (result.code()) {
                    in 200..299 -> R.string.success_delete_store
                    in 400..499 -> R.string.already_request_delete
                    else -> R.string.failed_delete_store
                }
            )
        }
    }

    fun changeDeleteType(type: DeleteType) {
        _deleteType.value = type
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
        _uploadImageStatus.value = true

        if (images.isEmpty()) {
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val responses = async(coroutineExceptionHandler) {
                repository.saveImage(_userStoreDetailModel.value.store?.storeId ?: -1, images)
            }

            responses.await()
            _uploadImageStatus.postValue(false)
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun deletePhoto(selectedImage: Image) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val data = repository.deleteImage(selectedImage.imageId)
            _photoDeleted.postValue(data.isSuccessful)
        }
    }

    fun putFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
//            val response = repository.putFavorite(storeType, storeId)
//            if (response.isSuccessful) {
//                showCustomBlackToast(getString(R.string.toast_favorite_add))
//            } else {
//                response.errorBody()?.string()?.getErrorMessage()?.let { showCustomBlackToast(it) }
//            }
//            _isFavorite.value = response.isSuccessful
        }
    }

    fun deleteFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
//            val response = repository.deleteFavorite(storeType, storeId)
//            if (response.isSuccessful) {
//                showCustomBlackToast(getString(R.string.toast_favorite_delete))
//            } else {
//                response.errorBody()?.string()?.getErrorMessage()?.let { showCustomBlackToast(it) }
//            }
//            _isFavorite.value = !response.isSuccessful
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
        hideLoading()
    }
}