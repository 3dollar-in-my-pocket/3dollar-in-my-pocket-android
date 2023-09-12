package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
import com.zion830.threedollars.ui.category.SortType
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class TruckStoreByMenuViewModel @Inject constructor(private val storeDataSource: StoreDataSource) : BaseViewModel() {

    private val _sortType: MutableLiveData<SortType> = MutableLiveData(SortType.DISTANCE)
    val sortType: LiveData<SortType>
        get() = _sortType
    private val _category: MutableLiveData<CategoriesModel> = MutableLiveData(CategoriesModel())
    val category: LiveData<CategoriesModel> get() = _category

    private val _categories: MutableLiveData<List<CategoriesModel>> = MutableLiveData(SharedPrefUtils.getTruckCategories())
    val categories: LiveData<List<CategoriesModel>> = _categories

    val storeByDistance = MutableLiveData<List<BossNearStoreResponse.BossNearStoreModel>>()
    val storeByReview = MutableLiveData<List<BossNearStoreResponse.BossNearStoreModel>>()
    val hasData = MutableLiveData<Boolean>()

    fun changeCategory(menuType: CategoriesModel, location: LatLng) {
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
                storeByReview.value = listOf()
            } else {
                storeByDistance.value = listOf()
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
                    storeByDistance.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
                SortType.REVIEW -> {
                    val data = storeDataSource.getFeedbacksCountsBossNearStore(
                        categoryId = _category.value?.categoryId ?: "",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    storeByReview.postValue(data.body()?.data)
                    hasData.postValue(data.body()?.data?.isNotEmpty())
                }
                else -> {

                }
            }
        }
    }

}