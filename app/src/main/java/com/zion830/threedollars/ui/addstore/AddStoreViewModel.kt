package com.zion830.threedollars.ui.addstore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.ContentModel
import com.home.domain.data.store.PostUserStoreModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.UserStoreModelRequest
import com.home.presentation.data.HomeSortType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.isNotNullOrBlank
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.MenuType
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(private val homeRepository: HomeRepository, private val repository: StoreDataSource) :
    BaseViewModel() {


    val storeName: MutableLiveData<String> = MutableLiveData<String>()

    val isFinished: LiveData<Boolean> = Transformations.map(storeName) {
        it.isNotNullOrBlank()
    }

    private val _selectedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val selectedLocation: StateFlow<LatLng?> get() = _selectedLocation

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    private val _postUserStoreModel: MutableStateFlow<PostUserStoreModel?> = MutableStateFlow(null)
    val postUserStoreModel: StateFlow<PostUserStoreModel?> get() = _postUserStoreModel

    private val _selectCategoryList: MutableStateFlow<List<CategoriesModel>> = MutableStateFlow(listOf())
    val selectCategoryList: StateFlow<List<CategoriesModel>> get() = _selectCategoryList.asStateFlow()


    private val _isNearStoreExist = MutableSharedFlow<Boolean>()
    val isNearStoreExist: SharedFlow<Boolean> get() = _isNearStoreExist

    private val _aroundStoreModels: MutableStateFlow<List<ContentModel>?> = MutableStateFlow(null)
    val aroundStoreModels: StateFlow<List<ContentModel>?> get() = _aroundStoreModels

    fun addNewStore(userStoreModelRequest: UserStoreModelRequest) {
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postUserStore(userStoreModelRequest).collect {
                if (it.ok) {
                    _postUserStoreModel.value = it.data
                } else {
                    _serverError.emit(it.error)
                }
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

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAroundStores(
                categoryIds = null,
                targetStores = null,
                sortType = HomeSortType.DISTANCE_ASC.name,
                filterCertifiedStores = false,
                mapLatitude = location.latitude,
                mapLongitude = location.longitude,
                deviceLatitude = location.latitude,
                deviceLongitude = location.longitude
            ).collect {
                if (it.ok) {
                    _aroundStoreModels.value = it.data?.contentModels
                } else {
                    _serverError.emit(it.error)
                }
            }
        }
    }

    fun getStoreNearExists(location: LatLng) {
        viewModelScope.launch {
            homeRepository.getStoreNearExists(distance = 10.0, mapLatitude = location.latitude, mapLongitude = location.longitude).collect {
                if (it.ok) {
                    _isNearStoreExist.emit(it.data?.isExists ?: false)
                } else {
                    _serverError.emit(it.error)
                }
            }
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun updateCategory(list: List<SelectedCategory>) {
//        _selectedCategory.value = list.toList()
    }

    fun removeCategory(item: SelectedCategory) {
//        val newList = _selectedCategory.value?.map {
//            SelectedCategory(
//                if (item.menuType == it.menuType) false else it.isSelected,
//                it.menuType
//            )
//        } ?: emptyList()
//        _selectedCategory.value = newList
    }

    fun removeAllCategory() {
        _selectCategoryList.value = listOf()
    }

    fun changeSelectCategory(categoriesModel: CategoriesModel) {
        _selectCategoryList.update {
            if (it.contains(categoriesModel)) {
                it.drop(it.indexOf(categoriesModel))
            } else {
                if (it.size < 4) {
                    it + categoriesModel
                } else {
                    it
                }
            }
        }
        Log.e("sadasdasd",_selectCategoryList.value.map { (it.name to ) })
    }
}