package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v4.store.request.StoreRequest
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EditStoreViewModel @Inject constructor(private val repository: StoreDataSource) :
    BaseViewModel() {

    private val _storeInfo: MutableLiveData<StoreDetail?> = MutableLiveData()
    val storeInfo: LiveData<StoreDetail?>
        get() = _storeInfo

    val storeName = Transformations.map(_storeInfo) {
        it?.storeName
    }

    private val _editStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val editStoreResult: LiveData<Boolean>
        get() = _editStoreResult

    val storeLocation: LiveData<LatLng> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }

    fun requestStoreInfo(storeId: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _storeInfo.postValue(
                repository.getStoreDetail(
                    storeId,
                    latitude,
                    longitude,
                    LocalDateTime.now().minusMonths(1)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ).body()?.data
            )
        }
    }

    fun editStore(
        storeId: Int,
        newStore: StoreRequest
    ) {
        showLoading()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = repository.updateStore(storeId, newStore)
            hideLoading()
            _editStoreResult.postValue(result.isSuccessful)
        }
    }
}