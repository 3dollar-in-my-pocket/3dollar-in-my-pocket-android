package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.zion830.threedollars.ui.home.data.HomeFilterEvent
import com.zion830.threedollars.ui.home.data.HomeSortType
import com.zion830.threedollars.ui.home.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.utils.AdvertisementsPosition
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
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

    val addressText: MutableLiveData<String> = MutableLiveData()
    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    val mapLocation: MutableLiveData<LatLng> = MutableLiveData()
    val currentDistanceM: MutableLiveData<Double> = MutableLiveData()

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState())
    val uiState = _uiState.asStateFlow()

    private val _advertisementModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementModel: StateFlow<AdvertisementModelV2?> get() = _advertisementModel

    private val _advertisementListModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementListModel: StateFlow<AdvertisementModelV2?> get() = _advertisementListModel

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

    fun updateCurrentLocation(latLng: LatLng) {
        _uiState.update { it.copy(userLocation = latLng) }
    }

    fun updateDistanceM(distanceM: Double) {
        _uiState.update { it.copy(currentDistanceM = distanceM) }
    }

    fun updateMapPosition(mapPosition: LatLng) {
        _uiState.update { it.copy(mapPosition = mapPosition)  }
    }

    fun updateUserLocation(latLng: LatLng) {
        _uiState.update { it.copy(userLocation = latLng) }
    }

    fun fetchAroundStores() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val uiState = uiState.value

            homeRepository.getAroundStores(
                distanceM = uiState.currentDistanceM,
                categoryIds = uiState.selectedCategory?.categoryId?.let { arrayOf(it) },
                targetStores = uiState.homeStoreType.toArray(),
                sortType = uiState.homeSortType.name,
                filterCertifiedStores = uiState.filterCertifiedStores,
                filterConditionsTypeModel = uiState.filterConditionsType,
                mapLatitude = uiState.mapPosition.latitude,
                mapLongitude = uiState.mapPosition.longitude,
                deviceLatitude = uiState.userLocation.latitude,
                deviceLongitude = uiState.userLocation.longitude
            ).collect { response ->
                if (response.ok) {
                    val carouselItemList = if (response.data?.contentModels.isNullOrEmpty()) {
                        arrayListOf(StoreEmptyResponse())
                    } else {
                        ArrayList(response.data?.contentModels as List<AdAndStoreItem>)
                    }

                    updateCarouselItemList(carouselItemList)
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

    fun changeSelectCategory(categoryModel: CategoryModel?) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(selectedCategory = categoryModel) }
            fetchAroundStores()
        }
    }

    fun updateHomeFilterEvent(
        homeSortType: HomeSortType? = null,
        homeStoreType: HomeStoreType? = null,
        filterConditionsType: List<FilterConditionsTypeModel>? = null,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(
                homeStoreType = homeStoreType ?: it.homeStoreType,
                homeSortType = homeSortType ?: it.homeSortType,
                filterConditionsType = filterConditionsType ?: it.filterConditionsType
            ) }
            fetchAroundStores()
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

    private fun updateCarouselItemList(itemList: List<AdAndStoreItem>) {
        _uiState.update { it.copy(carouselItemList = itemList) }
    }
}