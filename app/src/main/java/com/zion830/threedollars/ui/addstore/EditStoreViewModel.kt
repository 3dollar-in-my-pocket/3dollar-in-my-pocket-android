package com.zion830.threedollars.ui.addstore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.repository.model.response.StoreDetailResponse
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.await
import zion830.com.common.base.BaseViewModel

class EditStoreViewModel : BaseViewModel() {

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

    val storeLocation: LiveData<LatLng> = Transformations.map(_storeInfo) {
        if (it != null) {
            Log.d("ADDRESS vm", it.longitude.toString())
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

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
        showLoading()

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
            hideLoading()
            _editStoreResult.postValue(result.isSuccessful)
        }
    }
}