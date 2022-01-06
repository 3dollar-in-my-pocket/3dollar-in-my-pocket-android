package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.request.NewStoreRequest
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.isNotNullOrBlank


class AddStoreViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val storeName: MutableLiveData<String> = MutableLiveData<String>()

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

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
        SharedPrefUtils.getCategories().map { SelectedCategory(false, it) }
    )

    private val _isNearExist: MutableLiveData<Boolean> = MutableLiveData()
    val isNearExist: LiveData<Boolean> get() = _isNearExist

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

    fun getNearExists(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val nearExistResponse = repository.getNearExist(latitude, longitude)

            if (nearExistResponse.isSuccessful) {
                _isNearExist.postValue(nearExistResponse.body()?.nearExist?.isExists)
            }
        }
    }

    fun requestStoreInfo(location: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getAllStore(location.latitude, location.longitude)
            if (data.isSuccessful) {
                nearStoreInfo.postValue(data.body()?.data)
            }
        }
    }

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