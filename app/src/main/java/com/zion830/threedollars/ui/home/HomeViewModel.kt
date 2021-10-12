package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.getCurrentLocationName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val nearStoreInfo: MutableLiveData<List<StoreInfo>?> = MutableLiveData()

    val searchResultLocation: MutableLiveData<LatLng> = MutableLiveData()

    val addressText: MutableLiveData<String> = MutableLiveData()

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

    fun updateCurrentLocation(latlng: LatLng) {
        searchResultLocation.value = latlng
        currentLocation.value = latlng
        addressText.value = getCurrentLocationName(latlng)
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getAllStore(location.latitude, location.longitude)
            if (data.isSuccessful) {
                nearStoreInfo.postValue(data.body()?.data)
            }
        }
    }
}