package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.ContentModel
import com.home.domain.data.store.PostUserStoreModel
import com.home.domain.data.store.SelectCategoryModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.UserStoreModelRequest
import com.home.presentation.data.HomeSortType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.MenuType
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(private val homeRepository: HomeRepository, private val repository: StoreDataSource) :
    BaseViewModel() {


    private val _selectedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val selectedLocation: StateFlow<LatLng?> get() = _selectedLocation

    private val _category: MutableLiveData<MenuType> = MutableLiveData(MenuType.BUNGEOPPANG)
    val category: LiveData<MenuType>
        get() = _category

    private val _postUserStoreModel: MutableSharedFlow<PostUserStoreModel?> = MutableSharedFlow()
    val postUserStoreModel: SharedFlow<PostUserStoreModel?> get() = _postUserStoreModel

    private val _selectCategoryList: MutableStateFlow<List<SelectCategoryModel>> = MutableStateFlow(listOf())
    val selectCategoryList: StateFlow<List<SelectCategoryModel>> get() = _selectCategoryList.asStateFlow()


    private val _isNearStoreExist = MutableSharedFlow<Boolean>()
    val isNearStoreExist: SharedFlow<Boolean> get() = _isNearStoreExist

    private val _aroundStoreModels: MutableStateFlow<List<ContentModel>?> = MutableStateFlow(null)
    val aroundStoreModels: StateFlow<List<ContentModel>?> get() = _aroundStoreModels

    fun addNewStore(userStoreModelRequest: UserStoreModelRequest) {
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postUserStore(userStoreModelRequest).collect {
                if (it.ok) {
                    _postUserStoreModel.emit(it.data)
                } else {
                    _serverError.emit(it.error)
                }
            }
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    fun editStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int) {
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putUserStore(userStoreModelRequest, storeId).collect {
                if (it.ok) {
                    _postUserStoreModel.emit(it.data)
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

    fun removeCategory(categoryModel: CategoryModel) {
        _selectCategoryList.update { list ->
            list.filter { it.menuType.name != categoryModel.name }
        }
    }

    fun removeAllCategory() {
        _selectCategoryList.value = listOf()
    }

    fun changeSelectCategory(categoryModel: CategoryModel) {
        _selectCategoryList.update { list ->
            if (!categoryModel.isSelected) {
                list.filter { it.menuType.name != categoryModel.name }
            } else {
                if (list.size < 3) {
                    list + SelectCategoryModel(menuType = categoryModel)
                } else {
                    list
                }
            }
        }
    }

    fun setSelectCategoryModelList(selectCategoryModelList: List<SelectCategoryModel>) {
        _selectCategoryList.value = selectCategoryModelList
    }
}