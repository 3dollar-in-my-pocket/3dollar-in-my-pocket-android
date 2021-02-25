package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.response.StoreDetailResponse
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.isNotNullOrBlank

class StoreDetailViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    private val _storeInfo: MutableLiveData<StoreDetailResponse?> = MutableLiveData()
    val storeInfo: LiveData<StoreDetailResponse?>
        get() = _storeInfo

    val storeLocation: LiveData<LatLng?> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

    private val _addReviewResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val addReviewResult: LiveData<Boolean>
        get() = _addReviewResult

    val reviewContent = MutableLiveData<String>()

    val isAvailableReview = Transformations.map(reviewContent) {
        it.isNotNullOrBlank()
    }

    fun requestStoreInfo(storeId: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getStoreDetail(storeId, latitude, longitude).await()
            _storeInfo.postValue(data)
        }
    }

    fun clearReview() {
        reviewContent.value = ""
    }

    fun addReview(rating: Float) {
        if (reviewContent.value.isNullOrBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val newReview = NewReview(reviewContent.value!!, rating)
            repository.addReview(_storeInfo.value?.id ?: -1, SharedPrefUtils.getUserId(), newReview).await()
            _msgTextId.postValue(R.string.success_add_review)
            _addReviewResult.postValue(true)
        }
    }
}