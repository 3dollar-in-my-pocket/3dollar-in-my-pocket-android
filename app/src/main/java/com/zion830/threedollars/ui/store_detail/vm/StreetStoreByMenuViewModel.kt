package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.category.SortType
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StreetStoreByMenuViewModel @Inject constructor(private val storeDataSource: StoreDataSource) : BaseViewModel() {


    private val _sortType: SingleLiveEvent<SortType> = SingleLiveEvent<SortType>().apply {
        value = SortType.DISTANCE
    }
    val sortType: LiveData<SortType>
        get() = _sortType

    private val _category: SingleLiveEvent<CategoryInfo> = SingleLiveEvent<CategoryInfo>().apply {
        value = CategoryInfo()
    }
    val category: LiveData<CategoryInfo>
        get() = _category

    private val _categories: SingleLiveEvent<List<CategoryInfo>> = SingleLiveEvent<List<CategoryInfo>>().apply {
        value = SharedPrefUtils.getCategories()
    }
    val categories: LiveData<List<CategoryInfo>> = _categories

    private val _storeByDistance = SingleLiveEvent<List<StoreInfo>>()
    val storeByDistance : LiveData<List<StoreInfo>> get() = _storeByDistance

    private val _storeByRating = SingleLiveEvent<List<StoreInfo>>()
    val storeByRating : LiveData<List<StoreInfo>> get() = _storeByRating

    private val _hasData  = SingleLiveEvent<Boolean>()
    val hasData :LiveData<Boolean> get() = _hasData

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
                _storeByRating.value = listOf()
            } else {
                _storeByDistance.value = listOf()
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (sortType.value) {
                SortType.DISTANCE -> {
                    val data = storeDataSource.getCategoryByDistance(_category.value?.category ?: "", location.latitude, location.longitude)
                    val storeList = data.body()?.data ?: listOf()
                    _storeByDistance.postValue(storeList)
                    _hasData.postValue(storeList.isNotEmpty())
                }
                SortType.RATING -> {
                    val data = storeDataSource.getCategoryByReview(_category.value?.category ?: "", location.latitude, location.longitude)
                    val storeList = data.body()?.data ?: listOf()
                    _storeByRating.postValue(storeList)
                    _hasData.postValue(storeList.isNotEmpty())
                }
                else -> {

                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = storeDataSource.getCategories()

            if (response.isSuccessful) {
                _categories.postValue(response.body()?.data ?: emptyList())
                SharedPrefUtils.saveCategories(response.body()?.data ?: emptyList())
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }
}