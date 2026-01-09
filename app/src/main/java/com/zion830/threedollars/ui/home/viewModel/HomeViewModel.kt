package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.StoreModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.ui.home.data.HomeSortType
import com.zion830.threedollars.ui.home.data.HomeStoreType
import com.zion830.threedollars.ui.home.data.HomeUIState
import com.zion830.threedollars.ui.home.data.toArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.HOME

    private val _userInfo: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())
    val userInfo: StateFlow<UserModel> get() = _userInfo

    val addressText: MutableLiveData<String> = MutableLiveData()
    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    val mapLocation: MutableLiveData<LatLng> = MutableLiveData()
    val currentDistanceM: MutableLiveData<Double> = MutableLiveData()

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState())
    val uiState = _uiState.asStateFlow()

    private val _carouselUpdate = MutableSharedFlow<List<AdAndStoreItem>>(replay = 1)
    val carouselUpdate = _carouselUpdate.asSharedFlow()

    private var shouldResetScroll = false

    fun consumeShouldResetScroll(): Boolean {
        val value = shouldResetScroll
        shouldResetScroll = false
        return value
    }

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
        viewModelScope.launch {
            shouldResetScroll = true
            _carouselUpdate.emit(itemList)
        }
    }

    fun updateStoreItem(userStore: UserStoreModel) {
        val currentList = _carouselUpdate.replayCache.firstOrNull()?.toMutableList() ?: return
        val index = currentList.indexOfFirst { item ->
            (item as? ContentModel)?.storeModel?.storeId == userStore.storeId.toString()
        }

        if (index != -1) {
            val item = currentList[index] as ContentModel
            val updatedStoreModel = item.storeModel.copy(
                storeName = userStore.name,
                categories = userStore.categories,
                locationModel = userStore.location,
            )
            currentList[index] = item.copy(storeModel = updatedStoreModel)
            viewModelScope.launch {
                _carouselUpdate.emit(currentList)
            }
        }
    }

    // GA Events - Home
    fun sendClickStore(store: StoreModel) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to store.storeId,
                    ParameterName.STORE_TYPE to store.storeType
                )
            )
        )
    }

    fun sendClickAdvertisementCardLog(ad: AdvertisementModelV2) {
        LogManager.sendEvent(ClickEvent(
            screen = screenName,
            objectType = LogObjectType.CARD,
            objectId = LogObjectId.ADVERTISEMENT,
            additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to ad.advertisementId.toString())
        ))
    }

    fun sendClickCurrentLocationLog() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CURRENT_LOCATION
            )
        )
    }

    fun sendClickAddress() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.ADDRESS
            )
        )
    }

    fun sendClickCategoryFilter() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CATEGORY_FILTER
            )
        )
    }

    fun sendClickBossFilter(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.BOSS_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    fun sendClickSorting(sortType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SORTING,
                additionalParams = mapOf(ParameterName.VALUE to sortType)
            )
        )
    }

    fun sendClickRecentActivityFilter(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    fun sendClickVisitButtonLog() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT
            )
        )
    }

    fun sendClickMarkerLog(store: StoreModel) {
        LogManager.sendEvent(ClickEvent(
            screen = screenName,
            objectType = LogObjectType.MARKER,
            objectId = LogObjectId.STORE,
            additionalParams = mapOf(ParameterName.STORE_ID to store.storeId)
        ))
    }

    fun sendClickAdvertisementMarkerLog(advertisementId: Int) {
        LogManager.sendEvent(ClickEvent(
            screen = screenName,
            objectType = LogObjectType.MARKER,
            objectId = LogObjectId.ADVERTISEMENT,
            additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId.toString())
        ))
    }

    // GA Events - Category Filter
    fun sendClickCategoryInFilter(categoryId: String?) {
        val params = if (categoryId != null) {
            mapOf(ParameterName.CATEGORY_ID to categoryId)
        } else {
            emptyMap()
        }
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.CATEGORY_FILTER,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.CATEGORY,
            additionalParams = params
        ))
    }

    fun sendClickCategoryBannerAd(advertisementId: String) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.CATEGORY_FILTER,
            objectType = LogObjectType.BANNER,
            objectId = LogObjectId.ADVERTISEMENT,
            additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId)
        ))
    }

    fun sendClickCategoryMenuAd(advertisementId: String) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.CATEGORY_FILTER,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.ADVERTISEMENT,
            additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId)
        ))
    }

    // GA Events - Home List
    fun sendClickStoreInList(storeId: String, storeType: String) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.CARD,
            objectId = LogObjectId.STORE,
            additionalParams = mapOf(
                ParameterName.STORE_ID to storeId,
                ParameterName.STORE_TYPE to storeType
            )
        ))
    }

    fun sendClickCategoryFilterInList() {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.CATEGORY_FILTER
        ))
    }

    fun sendClickSortingInList(value: String) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.SORTING,
            additionalParams = mapOf(ParameterName.VALUE to value)
        ))
    }

    fun sendClickAdvertisementInList(advertisementId: String) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BANNER,
            objectId = LogObjectId.ADVERTISEMENT,
            additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId)
        ))
    }

    fun sendClickBossFilterInList(value: Boolean) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.BOSS_FILTER,
            additionalParams = mapOf(ParameterName.VALUE to value.toString())
        ))
    }

    fun sendClickOnlyVisitInList(value: Boolean) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.ONLY_VISIT,
            additionalParams = mapOf(ParameterName.VALUE to value.toString())
        ))
    }

    fun sendClickRecentActivityFilterInList(value: Boolean) {
        LogManager.sendEvent(ClickEvent(
            screen = ScreenName.HOME_LIST,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
            additionalParams = mapOf(ParameterName.VALUE to value.toString())
        ))
    }
}