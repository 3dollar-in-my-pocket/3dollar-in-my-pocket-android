package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.AllStoreResponse
import com.zion830.threedollars.repository.model.response.SearchByDistanceResponse
import com.zion830.threedollars.repository.model.response.SearchByReviewResponse
import com.zion830.threedollars.ui.store_detail.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import zion830.com.common.base.BaseViewModel

class StoreByMenuViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val nearStoreInfo: MutableLiveData<AllStoreResponse> = MutableLiveData()

    private val _sortType: MutableLiveData<SortType> = MutableLiveData(SortType.DISTANCE)
    val sortType: LiveData<SortType>
        get() = _sortType

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    val storeByDistance = MutableLiveData<SearchByDistanceResponse>()
    val storeByRating = MutableLiveData<SearchByReviewResponse>()
    val hasData = MutableLiveData<Boolean>()

    val firstSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.getStoresOver3()?.isNotEmpty() == true || storeByDistance.value?.storeList50?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.getStoresOver3()?.isNotEmpty() == true || storeByDistance.value?.storeList50?.isNotEmpty() == true
        }
    }

    val secondSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList2?.isNotEmpty() == true || storeByDistance.value?.storeList100?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList2?.isNotEmpty() == true || storeByDistance.value?.storeList100?.isNotEmpty() == true
        }
    }

    val thirdSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList1?.isNotEmpty() == true || storeByDistance.value?.storeList500?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList1?.isNotEmpty() == true || storeByDistance.value?.storeList500?.isNotEmpty() == true
        }
    }

    val fourthSectionVisibility = MediatorLiveData<Boolean>().apply {
        addSource(storeByDistance) {
            value = storeByRating.value?.storeList0?.isNotEmpty() == true || storeByDistance.value?.storeList1000?.isNotEmpty() == true
        }
        addSource(storeByRating) {
            value = storeByRating.value?.storeList0?.isNotEmpty() == true || storeByDistance.value?.storeList1000?.isNotEmpty() == true
        }
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
                storeByRating.value = SearchByReviewResponse()
            } else {
                storeByDistance.value = SearchByDistanceResponse()
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (sortType.value) {
                SortType.DISTANCE -> {
                    val data = repository.getCategoryByDistance(_category.value?.key ?: "", location.latitude, location.longitude).await()
                    storeByDistance.postValue(data)
                    hasData.postValue(data.isNotEmpty())
                }
                SortType.RATING -> {
                    val data = repository.getCategoryByReview(_category.value?.key ?: "", location.latitude, location.longitude).await()
                    storeByRating.postValue(data)
                    hasData.postValue(data.isNotEmpty())
                }
            }
        }
    }
}