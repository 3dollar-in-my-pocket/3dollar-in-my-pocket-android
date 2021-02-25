package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.response.AllStoreResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val nearStoreInfo: MutableLiveData<AllStoreResponse> = MutableLiveData()

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getAllStore(location.latitude, location.longitude).execute()
            if (data.isSuccessful) {
                nearStoreInfo.postValue(data.body())
            }
        }
    }
}