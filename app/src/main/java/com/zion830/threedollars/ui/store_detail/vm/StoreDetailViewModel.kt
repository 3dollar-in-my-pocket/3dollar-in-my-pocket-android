package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.response.Category
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.repository.model.response.StoreDetailResponse
import com.zion830.threedollars.ui.report_store.DeleteType
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

    val categoryInfo: LiveData<List<Category>> = Transformations.map(_storeInfo) { store ->
        val categoryHasMenu = store?.menu?.groupBy { it.category }
        val allMenu: HashMap<String, List<Menu>> = hashMapOf()
        store?.categories?.forEach {
            allMenu[it] = emptyList()
        }
        categoryHasMenu?.forEach { category ->
            allMenu[category.key] = category.value
        }
        allMenu.map { Category(it.key, it.value) }
    }

    val categoryCount: LiveData<Int> = Transformations.map(categoryInfo) {
        it.sumOf { category -> category.menu?.count() ?: 0 } + it.count { category -> category.menu?.isEmpty() == true }
    }

    val storeLocation: LiveData<LatLng?> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

    private val _deleteStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val deleteStoreResult: LiveData<Boolean>
        get() = _deleteStoreResult

    private val _deleteType: MutableLiveData<DeleteType> = MutableLiveData(DeleteType.NONE)
    val deleteType: LiveData<DeleteType>
        get() = _deleteType

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

    fun deleteStore(userId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = repository.deleteStore(deleteType.value!!, storeInfo.value?.id ?: 0, userId).execute()

            _deleteStoreResult.postValue(result.isSuccessful)
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
        if (reviewContent.value.isNullOrBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            repository.editReview(reviewId, newReview).await()
            _msgTextId.postValue(R.string.success_edit_review)
            _addReviewResult.postValue(true)
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            repository.deleteReview(reviewId).await()
            _msgTextId.postValue(R.string.success_delete_review)
            _addReviewResult.postValue(true)
        }
    }
}