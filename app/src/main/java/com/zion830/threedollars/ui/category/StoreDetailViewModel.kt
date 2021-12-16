package com.zion830.threedollars.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.Category
import com.zion830.threedollars.repository.model.v2.request.EditReviewRequest
import com.zion830.threedollars.repository.model.v2.request.MyMenu
import com.zion830.threedollars.repository.model.v2.request.NewReview
import com.zion830.threedollars.repository.model.v2.request.NewReviewRequest
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.repository.model.v2.response.store.Image
import com.zion830.threedollars.repository.model.v2.response.store.Menu
import com.zion830.threedollars.repository.model.v2.response.store.StoreDetail
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.ui.report_store.DeleteType
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import zion830.com.common.base.BaseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO : Edit 로직 분리 필요
class StoreDetailViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    private val _storeInfo: MutableLiveData<StoreDetail?> = MutableLiveData()
    val storeInfo: LiveData<StoreDetail?>
        get() = _storeInfo

    val categoryInfo: LiveData<List<Category>> = Transformations.map(_storeInfo) { store ->
        val categoryHasMenu = store?.menus?.groupBy { it.category }
        val allMenu: HashMap<String, List<Menu>> = hashMapOf()
        store?.categories?.forEach {
            allMenu[it] = emptyList()
        }
        categoryHasMenu?.forEach { category ->
            allMenu[category.key] = category.value
        }
        allMenu.map {
            val key = SharedPrefUtils.getCategories()
                .find { categoryInfo -> categoryInfo.category == it.key } ?: CategoryInfo()
            Category(key, it.value)
        }
    }

    private val _selectedCategory: MutableLiveData<List<SelectedCategory>> = MutableLiveData(
        SharedPrefUtils.getCategories().map { SelectedCategory(false, it) })
    val selectedCategory: LiveData<List<SelectedCategory>>
        get() = _selectedCategory

    val categoryCount: LiveData<Int> = Transformations.map(categoryInfo) {
        it.sumOf { category ->
            category.menu?.count() ?: 0
        } + it.count { category -> category.menu?.isEmpty() == true }
    }

    val storeLocation: LiveData<LatLng?> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

    private val _selectedLocation: MutableLiveData<LatLng?> = MutableLiveData()
    val selectedLocation: LiveData<LatLng?>
        get() = _selectedLocation

    private val _deleteStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val deleteStoreResult: LiveData<Boolean>
        get() = _deleteStoreResult

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

    fun requestStoreInfo(storeId: Int, latitude: Double?, longitude: Double?) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val startDate = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val data = repository.getStoreDetail(
                storeId,
                latitude ?: NaverMapUtils.DEFAULT_LOCATION.latitude,
                longitude ?: NaverMapUtils.DEFAULT_LOCATION.longitude,
                startDate
            )
            _storeInfo.postValue(data.body()?.data)
            _isExistStoreInfo.postValue(storeId to (data.code() == 200))
            hideLoading()
        }
    }

    fun addReview(content: String, rating: Float) {
        if (content.isBlank()) {
            _addReviewResult.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = NewReviewRequest(content, rating, _storeInfo.value?.storeId ?: -1)
            val response = repository.addReview(request)
            if (response.isSuccessful) {
                _msgTextId.postValue(R.string.success_add_review)
                _addReviewResult.postValue(true)
            } else {
                _addReviewResult.postValue(false)
            }
        }
    }

    fun deleteStore(storeId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val requestStoreId = if (storeInfo.value == null) storeId else storeInfo.value!!.storeId
            val result = repository.deleteStore(requestStoreId, deleteType.value!!.key)

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

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = EditReviewRequest(newReview.contents, newReview.rating)
            repository.editReview(reviewId, request)
            _msgTextId.postValue(R.string.success_edit_review)
            _addReviewResult.postValue(true)
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
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

        if (storeInfo.value?.storeId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val responses = async(Dispatchers.IO + coroutineExceptionHandler) {
                repository.saveImage(storeInfo.value?.storeId ?: -1, images)
            }

            responses.await()
            _uploadImageStatus.postValue(false)
        }
    }

    fun initSelectedCategory() {
        _selectedCategory.value = SharedPrefUtils.getCategories().map { menu ->
            val selectedCategory =
                categoryInfo.value?.find { categoryInfo -> categoryInfo.category.category == menu.category }
            SelectedCategory(
                selectedCategory != null,
                menu,
                selectedCategory?.menu?.map { MyMenu(it.category, it.name, it.price) })
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun updateCategory(list: List<SelectedCategory>) {
        _selectedCategory.value = list.toList()
    }

    fun removeCategory(item: SelectedCategory) {
        val newList = _selectedCategory.value?.map {
            SelectedCategory(
                if (item.menuType == it.menuType) false else it.isSelected,
                it.menuType,
                it.menuDetail
            )
        } ?: emptyList()
        _selectedCategory.value = newList
    }

    fun removeAllCategory() {
        val newList = _selectedCategory.value?.map {
            SelectedCategory(false, it.menuType)
        } ?: emptyList()
        _selectedCategory.value = newList
    }

    fun deletePhoto(selectedImage: Image) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.deleteImage(selectedImage.imageId)
            _photoDeleted.postValue(data.isSuccessful)
        }
    }
}