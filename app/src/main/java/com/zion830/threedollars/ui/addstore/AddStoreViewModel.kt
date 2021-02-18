package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.isNotNullOrBlank


class AddStoreViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val storeName: MutableLiveData<String> = MutableLiveData<String>()

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    private val _newStoreId: MutableLiveData<Int> = MutableLiveData()
    val newStoreId: LiveData<Int>
        get() = _newStoreId

    private fun saveImages(storeId: Int?, images: List<MultipartBody.Part>) {
        if (images.isEmpty()) {
            return
        }

        if (storeId == null) {
            _newStoreId.postValue(-1)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val responses = images.map { image ->
                async(Dispatchers.IO + coroutineExceptionHandler) {
                    repository.saveImage(storeId, image)
                }
            }
            responses.awaitAll()
        }
    }

    fun addNewStore(
        images: List<MultipartBody.Part>,
        category: String,
        latitude: Double,
        longitude: Double,
        menus: List<Menu>
    ) {
        showLoading()

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
            val result = repository.saveStore(params).execute()

            if (result.isSuccessful && images.isEmpty()) {
                _newStoreId.postValue(result.body()?.storeId ?: -1)
            } else if (result.isSuccessful && images.isNotEmpty()) {
                val storeId = result.body()?.storeId
                saveImages(storeId, images)
            } else {
                _newStoreId.postValue(-1)
            }

            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }
}