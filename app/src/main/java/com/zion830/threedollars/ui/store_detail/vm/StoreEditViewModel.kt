package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.repository.model.response.StoreDetailResponse
import com.zion830.threedollars.ui.store_detail.DeleteType
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.await
import zion830.com.common.base.BaseViewModel

class StoreEditViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    private val _storeInfo: MutableLiveData<StoreDetailResponse?> = MutableLiveData()
    val storeInfo: LiveData<StoreDetailResponse?>
        get() = _storeInfo

    val storeName = Transformations.map(_storeInfo) {
        it?.storeName
    }

    private val _editStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val editStoreResult: LiveData<Boolean>
        get() = _editStoreResult

    private val _deleteStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val deleteStoreResult: LiveData<Boolean>
        get() = _deleteStoreResult

    val storeLocation: LiveData<LatLng?> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

    private val _deleteType: MutableLiveData<DeleteType> = MutableLiveData(DeleteType.NOSTORE)
    val deleteType: LiveData<DeleteType>
        get() = _deleteType

    fun requestStoreInfo(storeId: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _storeInfo.postValue(repository.getStoreDetail(storeId, latitude, longitude).await())
        }
    }

    fun editStore(
        storeName: String,
        images: List<MultipartBody.Part>,
        storeId: Int,
        latitude: Double,
        longitude: Double,
        menus: List<Menu>
    ) {
        val params = hashMapOf<String, String>(
            Pair("userId", SharedPrefUtils.getUserId().toString()),
            Pair("latitude", latitude.toString()),
            Pair("longitude", longitude.toString()),
            Pair("storeId", storeId.toString()),
            Pair("storeName", storeName ?: "")
        )
        menus.forEachIndexed { index, menu ->
            params["menu[$index].name"] = menu.name
            params["menu[$index].price"] = menu.price
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = if (images.isEmpty()) {
                repository.updateStore(params).execute()
            } else {
                repository.updateStore(params, images).execute()
            }
            _isLoading.postValue(false)
            _editStoreResult.postValue(result.isSuccessful)
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
}