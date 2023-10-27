package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.home.presentation.data.HomeFilterEvent
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
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

    private val _selectCategory = MutableStateFlow(CategoryModel())
    val selectCategory = _selectCategory.asStateFlow()

    private val _advertisementModel: MutableStateFlow<AdvertisementModel?> = MutableStateFlow(null)
    val advertisementModel: StateFlow<AdvertisementModel?> get() = _advertisementModel

    private val _homeFilterEvent: MutableStateFlow<HomeFilterEvent> = MutableStateFlow(HomeFilterEvent())
    val homeFilterEvent: StateFlow<HomeFilterEvent> get() = _homeFilterEvent

    init {
        getAdvertisement()
    }

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
                mapLatitude = location.latitude,
                mapLongitude = location.longitude,
                deviceLatitude = location.latitude,
                deviceLongitude = location.longitude
            ).collect {
                val resultList: ArrayList<AdAndStoreItem> = arrayListOf()
                if (it.ok) {
                    if (it.data?.contentModels?.isEmpty() == true) {
                        resultList.add(StoreEmptyResponse())
                    } else {
                        it.data?.let { it1 -> resultList.addAll(it1.contentModels) }
                        advertisementModel.value?.let { advertisementModel ->
                            resultList.add(1, advertisementModel)
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

    fun updateHomeFilterEvent(homeSortType: HomeSortType? = null, homeStoreType: HomeStoreType? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _homeFilterEvent.update {
                if (homeSortType != null) {
                    it.copy(homeSortType = homeSortType)
                } else if (homeStoreType != null) {
                    it.copy(homeStoreType = homeStoreType)
                } else {
                    it
                }
            }
        }
    }

    private fun getAdvertisement() {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements("MAIN_PAGE_CARD").collect {
                if (it.ok) {
                    _advertisementModel.value = it.data?.firstOrNull()
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