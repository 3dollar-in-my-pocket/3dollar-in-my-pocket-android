package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.PopupRepository
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.utils.getCurrentLocationName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    private val repository = StoreRepository()
    private val popupRepository = PopupRepository()

    val nearStoreInfo: MutableLiveData<List<AdAndStoreItem>?> = MutableLiveData()

    val searchResultLocation: MutableLiveData<LatLng> = MutableLiveData()

    val addressText: MutableLiveData<String> = MutableLiveData()

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

    fun updateCurrentLocation(latlng: LatLng) {
        searchResultLocation.value = latlng
        currentLocation.value = latlng
        addressText.value = getCurrentLocationName(latlng) ?: "위치를 찾는 중..."
    }

    fun requestHomeItem(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val storeData = repository.getAllStore(location.latitude, location.longitude)
            val adData = popupRepository.getPopups("MAIN_PAGE_CARD").body()?.data

            val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
            storeData.body()?.data?.let { resultList.addAll(it) }

            if (resultList.isEmpty()) {
                resultList.add(HomeStoreEmptyResponse())
            }
            if (adData != null) {
                adData.firstOrNull()?.let { resultList.add(1, it) }
            }
            nearStoreInfo.postValue(resultList.toList())
        }
    }
}