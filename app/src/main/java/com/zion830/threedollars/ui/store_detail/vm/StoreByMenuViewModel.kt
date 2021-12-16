package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.repository.model.v2.response.store.NearStoreResponse
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
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

    private val _category: MutableLiveData<CategoryInfo> = MutableLiveData()
    val category: LiveData<CategoryInfo>
        get() = _category

    val storeByDistance = MutableLiveData<List<StoreInfo>>()
    val storeByRating = MutableLiveData<List<StoreInfo>>()
    val hasData = MutableLiveData<Boolean>()

    fun changeCategory(menuType: CategoryInfo) {
        _category.value = menuType
    }

    fun changeCategory(menuType: CategoryInfo, location: LatLng) {
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
                storeByRating.value = listOf()
            } else {
                storeByDistance.value = listOf()
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (sortType.value) {
                SortType.DISTANCE -> {
                    val data = repository.getCategoryByDistance(_category.value?.category ?: "", location.latitude, location.longitude)
                    storeByDistance.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
                SortType.RATING -> {
                    val data = repository.getCategoryByReview(_category.value?.category ?: "", location.latitude, location.longitude)
                    storeByRating.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
                else -> {

                }
            }
        }
    }
}