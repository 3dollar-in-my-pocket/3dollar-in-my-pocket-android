package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.response.store.BossCategoriesResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.ui.category.SortType
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class TruckStoreByMenuViewModel @Inject constructor(private val storeDataSource: StoreDataSource) : BaseViewModel() {

    private val _sortType: SingleLiveEvent<SortType> = SingleLiveEvent<SortType>().apply {
        value = SortType.DISTANCE
    }
    val sortType: LiveData<SortType>
        get() = _sortType
    private val _category: SingleLiveEvent<BossCategoriesResponse.BossCategoriesModel> =
        SingleLiveEvent<BossCategoriesResponse.BossCategoriesModel>().apply {
            value = BossCategoriesResponse.BossCategoriesModel()
        }
    val category: LiveData<BossCategoriesResponse.BossCategoriesModel>
        get() = _category

    private val _categories: SingleLiveEvent<List<BossCategoriesResponse.BossCategoriesModel>> =
        SingleLiveEvent<List<BossCategoriesResponse.BossCategoriesModel>>().apply {
            value = SharedPrefUtils.getTruckCategories()
        }
    val categories: LiveData<List<BossCategoriesResponse.BossCategoriesModel>> = _categories

    private val _storeByDistance = SingleLiveEvent<List<BossNearStoreResponse.BossNearStoreModel>>()
    val storeByDistance: LiveData<List<BossNearStoreResponse.BossNearStoreModel>> get() = _storeByDistance

    private val _storeByReview = SingleLiveEvent<List<BossNearStoreResponse.BossNearStoreModel>>()
    val storeByReview: LiveData<List<BossNearStoreResponse.BossNearStoreModel>> get() = _storeByReview

    fun changeCategory(menuType: BossCategoriesResponse.BossCategoriesModel) {
        _category.value = menuType
    }

    fun changeCategory(menuType: BossCategoriesResponse.BossCategoriesModel, location: LatLng) {
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
                _storeByReview.value = listOf()
            } else {
                _storeByDistance.value = listOf()
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (sortType.value) {
                SortType.DISTANCE -> {
                    val data = storeDataSource.getDistanceBossNearStore(
                        categoryId = _category.value?.categoryId ?: "",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    _storeByDistance.postValue(data.body()?.data ?: listOf())
                }
                SortType.REVIEW -> {
                    val data = storeDataSource.getFeedbacksCountsBossNearStore(
                        categoryId = _category.value?.categoryId ?: "",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    _storeByReview.postValue(data.body()?.data ?: listOf())
                }
                else -> {

                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = storeDataSource.getBossCategory()

            if (response.isSuccessful) {
                _categories.postValue(response.body()?.data ?: emptyList())
                SharedPrefUtils.saveTruckCategories(response.body()?.data ?: emptyList())
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }
}