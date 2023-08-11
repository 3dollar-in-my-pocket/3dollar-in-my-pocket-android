package com.zion830.threedollars.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.MarketingConsentRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationRequest
import com.zion830.threedollars.datasource.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoreDataSource,
    private val popupDataSource: PopupDataSource,
    private val userDataSource: UserDataSource
) : BaseViewModel() {

    private val _userInfo: MutableLiveData<MyInfoResponse> = MutableLiveData()

    val userInfo: LiveData<MyInfoResponse>
        get() = _userInfo

    val nearStoreInfo: MutableLiveData<List<AdAndStoreItem>?> = MutableLiveData()

    val searchResultLocation: MutableLiveData<LatLng> = MutableLiveData()

    val addressText: MutableLiveData<String> = MutableLiveData()

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.postValue(userDataSource.getMyInfo().body())
        }
    }

    fun updateCurrentLocation(latlng: LatLng) {
        searchResultLocation.value = latlng
        currentLocation.value = latlng
        addressText.value = getCurrentLocationName(latlng) ?: "위치를 찾는 중..."
    }

    fun getBossNearStore(location: LatLng, selectCategory: String = "All") {
        viewModelScope.launch(coroutineExceptionHandler) {
            val storeData = repository.getBossNearStore(
                latitude = location.latitude,
                longitude = location.longitude
            )
            val adData = popupDataSource.getPopups("MAIN_PAGE_CARD").body()?.data

            val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
            storeData.body()?.data?.let {
                if (selectCategory != "All") {
                    resultList.addAll(it.filter { bossNearStoreModel ->
                        bossNearStoreModel.categories.find { category ->
                            category.categoryId == selectCategory
                        } != null
                    })
                } else {
                    resultList.addAll(it)
                }
            }
            if (resultList.isEmpty()) {
                resultList.add(
                    StoreEmptyResponse(
                        emptyImage = R.drawable.img_truck,
                        emptyTitle = R.string.recruit_boss_title,
                        emptyBody = R.string.recruit_boss_body
                    )
                )
            }
            if (adData != null) {
                adData.firstOrNull()?.let { resultList.add(1, it) }
            }
            nearStoreInfo.postValue(resultList.toList())
        }
    }

    fun requestHomeItem(location: LatLng, selectCategory: String = "All") {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val storeData = repository.getAllStore(location.latitude, location.longitude)
            val adData = popupDataSource.getPopups("MAIN_PAGE_CARD").body()?.data
            val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
            storeData.body()?.data?.let {
                if (selectCategory != "All") {
                    resultList.addAll(it.filter { storeInfo ->
                        storeInfo.categories.find { category -> category == selectCategory }
                            ?.isNotEmpty() == true
                    })
                } else {
                    resultList.addAll(it)
                }
            }

            if (resultList.isEmpty()) {
                resultList.add(StoreEmptyResponse())
            }
            if (adData != null) {
                adData.firstOrNull()?.let { resultList.add(1, it) }
            }
            nearStoreInfo.postValue(resultList.toList())
        }
    }


    fun postPushInformation(informationRequest: PushInformationRequest, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (userDataSource.postPushInformation(informationRequest).isSuccessful) {
                putMarketingConsent(
                    (MarketingConsentRequest(if (isMarketing) "APPROVE" else "DENY"))
                )
            }
        }
    }

    fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (userDataSource.putMarketingConsent(marketingConsentRequest).isSuccessful) {
                getUserInfo()
            }
        }
    }
}