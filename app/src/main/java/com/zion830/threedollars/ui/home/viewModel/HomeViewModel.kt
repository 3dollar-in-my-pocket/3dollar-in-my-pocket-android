package com.zion830.threedollars.ui.home.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.FilterConditionsTypeModel
import com.home.presentation.data.HomeFilterEvent
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.utils.AdvertisementsPosition
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _currentLocationFlow: MutableStateFlow<LatLng> = MutableStateFlow(LatLng.INVALID)
    val currentLocationFlow = _currentLocationFlow.asStateFlow()

    private val _selectCategory = MutableStateFlow(CategoryModel())
    val selectCategory = _selectCategory.asStateFlow()

    private val _advertisementModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementModel: StateFlow<AdvertisementModelV2?> get() = _advertisementModel

    private val _advertisementListModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementListModel: StateFlow<AdvertisementModelV2?> get() = _advertisementListModel

    private val _homeFilterEvent: MutableStateFlow<HomeFilterEvent> = MutableStateFlow(HomeFilterEvent())
    val homeFilterEvent: StateFlow<HomeFilterEvent> get() = _homeFilterEvent

    fun getUserInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getMyInfo().collect {
                if (it.ok) {
                    _userInfo.value = it.data!!
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun updateCurrentLocation(latlng: LatLng) {
        currentLocation.value = latlng
        _currentLocationFlow.value = latlng
        addressText.value = getCurrentLocationName(latlng) ?: "위치를 찾는 중..."
    }

    fun requestHomeItem(location: LatLng, filterCertifiedStores: Boolean = false) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val targetStores = when (homeFilterEvent.value.homeStoreType) {
                HomeStoreType.ALL -> {
                    null
                }

                HomeStoreType.USER_STORE,
                HomeStoreType.BOSS_STORE,
                -> {
                    arrayOf(homeFilterEvent.value.homeStoreType.name)
                }
            }
            val categoryIds = if (selectCategory.value.categoryId.isEmpty()) null else arrayOf(selectCategory.value.categoryId)

            homeRepository.getAroundStores(
                categoryIds = categoryIds,
                targetStores = targetStores,
                sortType = homeFilterEvent.value.homeSortType.name,
                filterCertifiedStores = filterCertifiedStores,
                filterConditionsTypeModel = homeFilterEvent.value.filterConditionsType,
                mapLatitude = location.latitude,
                mapLongitude = location.longitude,
                deviceLatitude = location.latitude,
                deviceLongitude = location.longitude
            ).collect {
                val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
                if (it.ok) {
                    updateCurrentLocation(location)
                    if (it.data?.contentModels?.isEmpty() == true) {
                        resultList.add(StoreEmptyResponse())
                    } else {
                        it.data?.let { aroundStoreModel ->
                            resultList.addAll(aroundStoreModel.contentModels)
                        }
                    }
                    _aroundStoreModels.value = resultList
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }


    fun postPushInformation(pushToken: String, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postPushInformation(pushToken).collect {
                if (it.ok) {
                    putMarketingConsent((if (isMarketing) "APPROVE" else "DENY"))
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun changeSelectCategory(categoryModel: CategoryModel) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _selectCategory.emit(
                if (selectCategory.value.categoryId == categoryModel.categoryId) {
                    CategoryModel()
                } else {
                    categoryModel
                }
            )

        }
    }

    fun updateHomeFilterEvent(
        homeSortType: HomeSortType? = null,
        homeStoreType: HomeStoreType? = null,
        filterConditionsType: List<FilterConditionsTypeModel>? = null,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _homeFilterEvent.update {
                if (homeSortType != null) {
                    it.copy(homeSortType = homeSortType)
                } else if (homeStoreType != null) {
                    it.copy(homeStoreType = homeStoreType)
                } else if (filterConditionsType != null) {
                    it.copy(filterConditionsType = filterConditionsType)
                } else {
                    it
                }
            }
        }
    }

    fun getAdvertisement(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.MAIN_PAGE_CARD,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    _advertisementModel.value = it.data?.firstOrNull()
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun getAdvertisementList(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.STORE_LIST,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    _advertisementListModel.value = it.data?.firstOrNull()
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun putMarketingConsent(marketingConsent: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putMarketingConsent(marketingConsent).collect {
                if (it.ok) {
                    getUserInfo()
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }
}