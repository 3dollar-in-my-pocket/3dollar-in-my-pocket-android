package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.StoreDataSourceImpl
import com.zion830.threedollars.datasource.model.MenuType
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import zion830.com.common.ext.isNotNullOrBlank
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(private val repository: StoreDataSource) :
    BaseViewModel() {


    val storeName: SingleLiveEvent<String> = SingleLiveEvent<String>()

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

    private val _selectedLocation: SingleLiveEvent<LatLng?> = SingleLiveEvent()
    val selectedLocation: LiveData<LatLng?>
        get() = _selectedLocation

    private val _newStoreId: SingleLiveEvent<Int> = SingleLiveEvent()
    val newStoreId: LiveData<Int>
        get() = _newStoreId

    private val _selectedCategory: SingleLiveEvent<List<SelectedCategory>> = SingleLiveEvent()
    val selectedCategory: LiveData<List<SelectedCategory>>
        get() = _selectedCategory

    private val _isNearStoreExist: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isNearStoreExist: LiveData<Boolean> get() = _isNearStoreExist



    val nearStoreInfo: SingleLiveEvent<List<StoreInfo>?> = SingleLiveEvent()

    fun addNewStore(newStore: NewStoreRequest) {
        showLoading()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = repository.saveStore(newStore)

            if (result.isSuccessful) {
                val storeId = result.body()?.data?.storeId ?: -1
                _newStoreId.postValue(storeId)
            } else {
                _newStoreId.postValue(-1)
            }

            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    fun requestStoreInfo(location: LatLng?) {
        if (location == null || location == NaverMapUtils.DEFAULT_LOCATION) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getAllStore(location.latitude, location.longitude)
            if (data.isSuccessful) {
                nearStoreInfo.postValue(data.body()?.data)
                _isNearStoreExist.postValue(hasStoreDistanceUnder10M(data.body()?.data))
            }
        }
    }

    private fun hasStoreDistanceUnder10M(stores: List<StoreInfo>?) =
        stores?.find { store -> store.distance <= 10 } != null

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun updateCategory(list: List<SelectedCategory>) {
        _selectedCategory.value = list.toList()
    }

    fun removeCategory(item: SelectedCategory) {
        val newList = _selectedCategory.value?.map {
            SelectedCategory(
                if (item.menuType == it.menuType) false else it.isSelected,
                it.menuType
            )
        } ?: emptyList()
        _selectedCategory.value = newList
    }

    fun removeAllCategory() {
        val newList = _selectedCategory.value?.map {
            SelectedCategory(false, it.menuType)
        } ?: emptyList()
        _selectedCategory.value = newList
    }
}