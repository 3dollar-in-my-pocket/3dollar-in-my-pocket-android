package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.isNotNullOrBlank


class AddStoreViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val storeName: MutableLiveData<String> = MutableLiveData<String>()

    private val _addStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val addStoreResult: LiveData<Boolean>
        get() = _addStoreResult

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    fun addNewStore(
        images: List<MultipartBody.Part>,
        category: String,
        latitude: Double,
        longitude: Double,
        menus: List<Menu>
    ) {
        val params = hashMapOf<String, String>(
            Pair("userId", SharedPrefUtils.getUserId().toString()),
            Pair("latitude", latitude.toString()),
            Pair("longitude", longitude.toString()),
            Pair("category", category),
            Pair("storeName", storeName.value ?: "")
        )
        menus.forEachIndexed { index, menu ->
            params["menu[$index].name"] = menu.name
            params["menu[$index].price"] = menu.price
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = if (images.isEmpty()) {
                repository.saveStore(params).execute()
            } else {
                repository.saveStore(params, images).execute()
            }

            _addStoreResult.postValue(result.isSuccessful)
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    fun changeCategory(menuType: MenuType) {
        if (_category.value != menuType) {
            _category.value = menuType
        }
    }
}