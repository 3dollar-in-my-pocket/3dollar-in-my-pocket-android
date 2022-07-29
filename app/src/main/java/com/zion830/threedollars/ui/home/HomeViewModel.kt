package com.zion830.threedollars.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.PopupRepository
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.repository.model.v2.response.store.BossCategoriesResponse
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.utils.getCurrentLocationName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    private val repository = StoreRepository()
    private val popupRepository = PopupRepository()

    private val _bossCategoryModelList =
        MutableSharedFlow<List<BossCategoriesResponse.BossCategoriesModel>>()
    val bossCategoryModelList: SharedFlow<List<BossCategoriesResponse.BossCategoriesModel>> get() = _bossCategoryModelList

    private val _roadFoodCategoryModelList =
        MutableSharedFlow<List<CategoryInfo>>()
    val roadFoodCategoryModelList: SharedFlow<List<CategoryInfo>> get() = _roadFoodCategoryModelList

    val nearStoreInfo: MutableLiveData<List<AdAndStoreItem>?> = MutableLiveData()

    val searchResultLocation: MutableLiveData<LatLng> = MutableLiveData()

    val addressText: MutableLiveData<String> = MutableLiveData()

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

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
            val adData = popupRepository.getPopups("MAIN_PAGE_CARD").body()?.data

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
                resultList.add(HomeStoreEmptyResponse())
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
            val adData = popupRepository.getPopups("MAIN_PAGE_CARD").body()?.data

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
                resultList.add(HomeStoreEmptyResponse())
            }
            if (adData != null) {
                adData.firstOrNull()?.let { resultList.add(1, it) }
            }
            nearStoreInfo.postValue(resultList.toList())
        }
    }


    fun getBossCategory() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getBossCategory().body()?.data?.let {
                _bossCategoryModelList.emit(listOf(BossCategoriesResponse.BossCategoriesModel()) + it)
            }
        }
    }

    fun getRoadFoodCategory() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getCategories().body()?.data?.let {
                _roadFoodCategoryModelList.emit(
                    listOf(CategoryInfo(category = "All", name = "전체")) + it
                )
            }
        }
    }
}