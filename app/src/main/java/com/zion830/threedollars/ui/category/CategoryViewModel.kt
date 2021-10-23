package com.zion830.threedollars.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class CategoryViewModel: BaseViewModel() {

    private val repository = StoreRepository()

    private val _categories: MutableLiveData<List<CategoryInfo>> = MutableLiveData(SharedPrefUtils.getCategories())
    val categories: LiveData<List<CategoryInfo>> = _categories

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = repository.getCategories()

            if (response.isSuccessful) {
                _categories.postValue(response.body()?.data ?: emptyList())
                SharedPrefUtils.saveCategories(response.body()?.data ?: emptyList())
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }
}