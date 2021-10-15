package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.NearStoreResponse
import com.zion830.threedollars.repository.model.v2.response.store.StoreByDistance
import com.zion830.threedollars.repository.model.v2.response.store.StoreByRating
import com.zion830.threedollars.ui.category.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class StoreByMenuViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val nearStoreInfo: MutableLiveData<NearStoreResponse> = MutableLiveData()

    private val _sortType: MutableLiveData<SortType> = MutableLiveData(SortType.DISTANCE)
    val sortType: LiveData<SortType>
        get() = _sortType

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    val storeByDistance = MutableLiveData<StoreByDistance>()
    val storeByRating = MutableLiveData<StoreByRating>()
    val hasData = MutableLiveData<Boolean>()

    val firstSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList4?.isNotEmpty() == true || storeByDistance.value?.storeList50?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList4?.isNotEmpty() == true || storeByDistance.value?.storeList50?.isNotEmpty() == true
        }
    }

    val secondSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList3?.isNotEmpty() == true || storeByDistance.value?.storeList100?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList3?.isNotEmpty() == true || storeByDistance.value?.storeList100?.isNotEmpty() == true
        }
    }

    val thirdSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList2?.isNotEmpty() == true || storeByDistance.value?.storeList500?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList2?.isNotEmpty() == true || storeByDistance.value?.storeList500?.isNotEmpty() == true
        }
    }

    val fourthSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList1?.isNotEmpty() == true || storeByDistance.value?.storeList1000?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList1?.isNotEmpty() == true || storeByDistance.value?.storeList1000?.isNotEmpty() == true
        }
    }

    val fifthSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList0?.isNotEmpty() == true || storeByDistance.value?.storeListOver1000?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList0?.isNotEmpty() == true || storeByDistance.value?.storeListOver1000?.isNotEmpty() == true
        }
    }

    fun changeCategory(menuType: MenuType) {
        _category.value = menuType
    }

    fun changeCategory(menuType: MenuType, location: LatLng) {
        if (_category.value != menuType) {
            _category.value = menuType
            requestStoreInfo(location)
        }
    }

    fun changeSortType(sortType: SortType, location: LatLng) {
        if (_sortType.value != sortType) {
            _sortType.value = sortType
            requestStoreInfo(location)

            if (sortType == SortType.DISTANCE) {
                storeByRating.value = StoreByRating()
            } else {
                storeByDistance.value = StoreByDistance()
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (sortType.value) {
                SortType.DISTANCE -> {
                    val data = repository.getCategoryByDistance(_category.value?.key ?: "", location.latitude, location.longitude)
                    storeByDistance.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
                SortType.RATING -> {
                    val data = repository.getCategoryByReview(_category.value?.key ?: "", location.latitude, location.longitude)
                    storeByRating.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
            }
        }
    }
}