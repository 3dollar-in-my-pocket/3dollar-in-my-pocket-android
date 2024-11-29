package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.ContentModel
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
    val currentDistanceM: MutableLiveData<Double> = MutableLiveData()

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
            homeRepository.getMyInfo().collect { response ->
                if (response.ok) {
                    _userInfo.value = response.data!!
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun updateCurrentLocation(latLng: LatLng) {
        currentLocation.value = latLng
        _currentLocationFlow.value = latLng
        addressText.value = getCurrentLocationName(latLng) ?: "위치를 찾는 중..."
    }

    fun requestHomeItem(location: LatLng, distanceM: Double, filterCertifiedStores: Boolean = false) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val targetStores = homeFilterEvent.value.homeStoreType.getTargetStoresArray()
            val categoryIds = selectCategory.value.getCategoryIdArray()

            homeRepository.getAroundStores(
                distanceM = distanceM,
                categoryIds = categoryIds,
                targetStores = targetStores,
                sortType = homeFilterEvent.value.homeSortType.name,
                filterCertifiedStores = filterCertifiedStores,
                filterConditionsTypeModel = homeFilterEvent.value.filterConditionsType,
                mapLatitude = location.latitude,
                mapLongitude = location.longitude,
                deviceLatitude = location.latitude,
                deviceLongitude = location.longitude
            ).collect { response ->
                if (response.ok) {
                    updateCurrentLocation(location)
                    val resultList = getResultList(response.data?.contentModels)
                    _aroundStoreModels.value = resultList
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun putPushInformation(pushToken: String, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putPushInformation(pushToken).collect { response ->
                if (response.ok) {
                    putMarketingConsent(if (isMarketing) "APPROVE" else "DENY")
                } else {
                    _serverError.emit(response.message)
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
                it.copy(
                    homeSortType = homeSortType ?: it.homeSortType,
                    homeStoreType = homeStoreType ?: it.homeStoreType,
                    filterConditionsType = filterConditionsType ?: it.filterConditionsType
                )
            }
        }
    }

    fun getAdvertisement(latLng: LatLng) {
        getAdvertisementList(latLng = latLng)
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.MAIN_PAGE_CARD,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect { response ->
                if (response.ok) {
                    _advertisementModel.value = response.data?.firstOrNull()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun getAdvertisementList(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.STORE_LIST,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect { response ->
                if (response.ok) {
                    _advertisementListModel.value = response.data?.firstOrNull()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun putMarketingConsent(marketingConsent: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putMarketingConsent(marketingConsent).collect { response ->
                if (response.ok) {
                    getUserInfo()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun getResultList(contentModels: List<ContentModel>?): ArrayList<AdAndStoreItem> {
        return if (contentModels.isNullOrEmpty()) {
            arrayListOf(StoreEmptyResponse())
        } else {
            ArrayList(contentModels)
        }
    }

    private fun HomeStoreType.getTargetStoresArray(): Array<String>? {
        return when (this) {
            HomeStoreType.ALL -> null
            else -> arrayOf(this.name)
        }
    }

    private fun CategoryModel.getCategoryIdArray(): Array<String>? {
        return if (this.categoryId.isEmpty()) null else arrayOf(this.categoryId)
    }
}