package com.zion830.threedollars.ui.write.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.PostUserStoreModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.zion830.threedollars.ui.home.data.HomeSortType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.MenuType
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _availableSnackCategories: MutableStateFlow<List<CategoryModel>> = MutableStateFlow(emptyList())
    val availableSnackCategories: StateFlow<List<CategoryModel>> get() = _availableSnackCategories.asStateFlow()

    private val _availableMealCategories: MutableStateFlow<List<CategoryModel>> = MutableStateFlow(emptyList())
    val availableMealCategories: StateFlow<List<CategoryModel>> get() = _availableMealCategories.asStateFlow()

    // Temporary storage for removed categories to restore data if re-selected
    private val _removedCategoriesData: MutableMap<String, List<com.threedollar.domain.home.data.store.UserStoreMenuModel>> = mutableMapOf()

    private val _isNearStoreExist = MutableSharedFlow<Boolean>()
    val isNearStoreExist: SharedFlow<Boolean> get() = _isNearStoreExist

    init {
        loadAvailableCategories()
    }

    private fun loadAvailableCategories() {
        _availableSnackCategories.value = LegacySharedPrefUtils.getCategories()
        _availableMealCategories.value = LegacySharedPrefUtils.getTruckCategories()
    }

    fun addNewStore(userStoreModelRequest: UserStoreModelRequest) {
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postUserStore(userStoreModelRequest).collect {
                if (it.ok) {
                    _postUserStoreModel.emit(it.data)
                } else {
                    _serverError.emit(it.message)
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
                    _serverError.emit(it.message)
                }
            }
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    fun getStoreNearExists(location: LatLng) {
        viewModelScope.launch {
            homeRepository.getStoreNearExists(distance = 10.0, mapLatitude = location.latitude, mapLongitude = location.longitude).collect {
                if (it.ok) {
                    _isNearStoreExist.emit(it.data?.contentModels?.isNotEmpty() ?: false)
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun updateLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
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
                // Store menu data before removing category
                val categoryToRemove = list.find { it.menuType.categoryId == categoryModel.categoryId }
                categoryToRemove?.menuDetail?.let { menuList ->
                    if (menuList.isNotEmpty()) {
                        _removedCategoriesData[categoryModel.categoryId] = menuList
                    }
                }
                list.filter { it.menuType.name != categoryModel.name }
            } else {
                if (list.size < 10) {
                    // Restore previously saved menu data if exists
                    val savedMenus = _removedCategoriesData[categoryModel.categoryId]
                    val menuDetail = savedMenus ?: listOf(
                        com.threedollar.domain.home.data.store.UserStoreMenuModel(
                            category = categoryModel,
                            menuId = 0,
                            name = "",
                            price = ""
                        )
                    )
                    list + SelectCategoryModel(menuType = categoryModel, menuDetail = menuDetail)
                } else {
                    list
                }
            }
        }
    }

    fun setSelectCategoryModelList(selectCategoryModelList: List<SelectCategoryModel>) {
        _selectCategoryList.value = selectCategoryModelList
    }

    fun addMenuToCategory(categoryId: String) {
        _selectCategoryList.update { list ->
            list.map { selectCategory ->
                if (selectCategory.menuType.categoryId == categoryId) {
                    val currentMenus = selectCategory.menuDetail ?: emptyList()
                    val newMenu = com.threedollar.domain.home.data.store.UserStoreMenuModel(
                        category = selectCategory.menuType,
                        menuId = 0,
                        name = "",
                        price = ""
                    )
                    selectCategory.copy(menuDetail = currentMenus + newMenu)
                } else {
                    selectCategory
                }
            }
        }
    }

    fun removeMenuFromCategory(categoryId: String, menuIndex: Int) {
        _selectCategoryList.update { list ->
            list.map { selectCategory ->
                if (selectCategory.menuType.categoryId == categoryId) {
                    val currentMenus = selectCategory.menuDetail ?: emptyList()
                    if (currentMenus.size > 1) {
                        selectCategory.copy(menuDetail = currentMenus.filterIndexed { index, _ -> index != menuIndex })
                    } else {
                        selectCategory
                    }
                } else {
                    selectCategory
                }
            }
        }
    }

    fun updateMenuInCategory(categoryId: String, menuIndex: Int, name: String, price: String) {
        _selectCategoryList.update { list ->
            list.map { selectCategory ->
                if (selectCategory.menuType.categoryId == categoryId) {
                    val currentMenus = selectCategory.menuDetail ?: emptyList()
                    val updatedMenus = currentMenus.mapIndexed { index, menu ->
                        if (index == menuIndex) {
                            menu.copy(name = name, price = price)
                        } else {
                            menu
                        }
                    }
                    selectCategory.copy(menuDetail = updatedMenus)
                } else {
                    selectCategory
                }
            }
        }
    }

    fun validateMenuDetail(): Boolean {
        val categories = _selectCategoryList.value
        if (categories.isEmpty()) return false

        return categories.all { category ->
            val menus = category.menuDetail ?: emptyList()
            menus.isNotEmpty() && menus.all { menu ->
                !menu.name.isNullOrBlank() && !menu.price.isNullOrBlank()
            }
        }
    }
}