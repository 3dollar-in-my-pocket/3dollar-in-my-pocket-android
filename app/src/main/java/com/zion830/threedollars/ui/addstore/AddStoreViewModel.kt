package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.MenuType
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.threedollar.common.ext.isNotNullOrBlank
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(private val repository: StoreDataSource) :
    BaseViewModel() {


    val storeName: MutableLiveData<String> = MutableLiveData<String>()

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

    private val _isMapUpdated: MutableLiveData<Unit> = MutableLiveData()
    val isMapUpdated: LiveData<Unit>
        get() = _isMapUpdated

    private val _selectedLocation: MutableLiveData<LatLng?> = MutableLiveData()
    val selectedLocation: LiveData<LatLng?>
        get() = _selectedLocation

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    private val _newStoreId: MutableLiveData<Int> = MutableLiveData()
    val newStoreId: LiveData<Int>
        get() = _newStoreId

    private val _selectedCategory: MutableLiveData<List<SelectedCategory>> = MutableLiveData(
        LegacySharedPrefUtils.getCategories().map { SelectedCategory(false, it) }
    )

    private val _isNearStoreExist: MutableLiveData<Boolean> = MutableLiveData()
    val isNearStoreExist: LiveData<Boolean> get() = _isNearStoreExist

    val selectedCategory: LiveData<List<SelectedCategory>>
        get() = _selectedCategory

    val selectedCount: LiveData<Int> = Transformations.map(selectedCategory) {
        it.count { item -> item.isSelected }
    }

    val nearStoreInfo: MutableLiveData<List<StoreInfo>?> = MutableLiveData()

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