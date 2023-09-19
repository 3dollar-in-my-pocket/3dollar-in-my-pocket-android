package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.zion830.threedollars.Constants.DISTANCE_ASC
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    private val _userInfo: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())

    val userInfo: StateFlow<UserModel> get() = _userInfo

    private val _aroundStoreModels: MutableStateFlow<List<AdAndStoreItem>> = MutableStateFlow(listOf())
    val aroundStoreModels: StateFlow<List<AdAndStoreItem>> get() = _aroundStoreModels

    val addressText: MutableLiveData<String> = MutableLiveData()

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

    private val _selectCategory = MutableStateFlow(CategoriesModel())
    val selectCategory = _selectCategory.asStateFlow()

    private val _advertisementModel: MutableStateFlow<AdvertisementModel?> = MutableStateFlow(null)
    val advertisementModel: StateFlow<AdvertisementModel?> get() = _advertisementModel

    init {
        getAdvertisement()
    }

    fun getUserInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getMyInfo().collect {
                _userInfo.value = it.data
            }
        }
    }

    fun updateCurrentLocation(latlng: LatLng) {
        currentLocation.value = latlng
        addressText.value = getCurrentLocationName(latlng) ?: "위치를 찾는 중..."
    }

    fun requestHomeItem(location: LatLng, targetStores: Array<String>? = null, selectCategoryId: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAroundStores(
                categoryIds = null,
                targetStores = targetStores,
                sortType = DISTANCE_ASC,
                filterCertifiedStores = false,
                mapLatitude = location.latitude,
                mapLongitude = location.longitude,
                deviceLatitude = location.latitude,
                deviceLongitude = location.longitude
            ).collect {
                val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
                if (it.data.contentModels.isEmpty()) {
                    resultList.add(StoreEmptyResponse())
                } else {
                    resultList.addAll(it.data.contentModels)
                    advertisementModel.value?.let { advertisementModel ->
                        resultList.add(1, advertisementModel)
                    }
                }
                _aroundStoreModels.value = resultList
            }
        }
    }


    fun postPushInformation(pushToken: String, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postPushInformation(pushToken).collect {
                putMarketingConsent((if (isMarketing) "APPROVE" else "DENY"))
            }
        }
    }

    fun changeSelectCategory(categoriesModel: CategoriesModel) {
        viewModelScope.launch {
            _selectCategory.emit(categoriesModel)
        }
    }

    private fun getAdvertisement() {
        viewModelScope.launch {
            homeRepository.getAdvertisements("MAIN_PAGE_CARD").collect {
                _advertisementModel.value = it.data.firstOrNull()
            }
        }
    }

    private fun putMarketingConsent(marketingConsent: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putMarketingConsent(marketingConsent).collect {
                getUserInfo()
            }
        }
    }
}