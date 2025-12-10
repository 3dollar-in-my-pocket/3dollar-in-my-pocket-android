package com.zion830.threedollars.ui.write.viewModel

import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.PostUserStoreModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(private val homeRepository: HomeRepository, private val repository: StoreDataSource) :
    BaseViewModel() {


    private val _selectedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val selectedLocation: StateFlow<LatLng?> get() = _selectedLocation

    private val _postUserStoreModel: MutableSharedFlow<PostUserStoreModel?> = MutableSharedFlow()
    val postUserStoreModel: SharedFlow<PostUserStoreModel?> get() = _postUserStoreModel

    private val _selectCategoryList: MutableStateFlow<List<SelectCategoryModel>> = MutableStateFlow(listOf())
    val selectCategoryList: StateFlow<List<SelectCategoryModel>> get() = _selectCategoryList.asStateFlow()

    private val _selectedCategoryId: MutableStateFlow<String?> = MutableStateFlow(null)
    val selectedCategoryId: StateFlow<String?> get() = _selectedCategoryId.asStateFlow()

    private val _availableSnackCategories: MutableStateFlow<List<CategoryModel>> = MutableStateFlow(emptyList())
    val availableSnackCategories: StateFlow<List<CategoryModel>> get() = _availableSnackCategories.asStateFlow()

    private val _availableMealCategories: MutableStateFlow<List<CategoryModel>> = MutableStateFlow(emptyList())
    val availableMealCategories: StateFlow<List<CategoryModel>> get() = _availableMealCategories.asStateFlow()

    // Temporary storage for removed categories to restore data if re-selected
    private val _removedCategoriesData: MutableMap<String, List<com.threedollar.domain.home.data.store.UserStoreMenuModel>> = mutableMapOf()

    private val _isNearStoreExist = MutableSharedFlow<Boolean>()
    val isNearStoreExist: SharedFlow<Boolean> get() = _isNearStoreExist

    private val _selectedPaymentMethods: MutableStateFlow<Set<PaymentType>> = MutableStateFlow(emptySet())
    val selectedPaymentMethods: StateFlow<Set<PaymentType>> get() = _selectedPaymentMethods.asStateFlow()

    private val _selectedDays: MutableStateFlow<Set<DayOfTheWeekType>> = MutableStateFlow(emptySet())
    val selectedDays: StateFlow<Set<DayOfTheWeekType>> get() = _selectedDays.asStateFlow()

    private val _openingHours: MutableStateFlow<OpeningHourRequest> = MutableStateFlow(OpeningHourRequest())
    val openingHours: StateFlow<OpeningHourRequest> get() = _openingHours.asStateFlow()

    private val _createdStoreId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val createdStoreId: StateFlow<Int?> get() = _createdStoreId.asStateFlow()

    private val _createdStoreInfo: MutableStateFlow<PostUserStoreModel?> = MutableStateFlow(null)
    val createdStoreInfo: StateFlow<PostUserStoreModel?> get() = _createdStoreInfo.asStateFlow()

    private val _isMenuDetailCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isMenuDetailCompleted: StateFlow<Boolean> get() = _isMenuDetailCompleted.asStateFlow()

    private val _isStoreDetailCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isStoreDetailCompleted: StateFlow<Boolean> get() = _isStoreDetailCompleted.asStateFlow()

    private val _storeName: MutableStateFlow<String> = MutableStateFlow("")
    val storeName: StateFlow<String> get() = _storeName.asStateFlow()

    private val _storeType: MutableStateFlow<String?> = MutableStateFlow(null)
    val storeType: StateFlow<String?> get() = _storeType.asStateFlow()

    private val _address: MutableStateFlow<String> = MutableStateFlow("")
    val address: StateFlow<String> get() = _address.asStateFlow()

    val isRequiredInfoValid: StateFlow<Boolean> = combine(
        _storeName,
        _storeType,
        _selectedLocation
    ) { name, type, location ->
        name.isNotBlank() && type != null && location != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

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
                    it.data?.let { data ->
                        _createdStoreId.value = data.storeId
                        _createdStoreInfo.value = data
                    }
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
                val categoryToRemove = list.find { it.menuType.categoryId == categoryModel.categoryId }
                categoryToRemove?.menuDetail?.let { menuList ->
                    if (menuList.isNotEmpty()) {
                        _removedCategoriesData[categoryModel.categoryId] = menuList
                    }
                }

                if (_selectedCategoryId.value == categoryModel.categoryId) {
                    val remaining = list.filter { it.menuType.name != categoryModel.name }
                    _selectedCategoryId.value = remaining.firstOrNull()?.menuType?.categoryId
                }

                list.filter { it.menuType.name != categoryModel.name }
            } else {
                if (list.size < 10) {
                    val savedMenus = _removedCategoriesData[categoryModel.categoryId]
                    val menuDetail = savedMenus ?: listOf(
                        com.threedollar.domain.home.data.store.UserStoreMenuModel(
                            category = categoryModel,
                            menuId = 0,
                            name = "",
                            price = ""
                        )
                    )
                    val newList = list + SelectCategoryModel(menuType = categoryModel, menuDetail = menuDetail)

                    if (list.isEmpty()) {
                        _selectedCategoryId.value = categoryModel.categoryId
                    }

                    newList
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

    fun togglePaymentMethod(paymentType: PaymentType) {
        _selectedPaymentMethods.update { current ->
            if (current.contains(paymentType)) {
                current - paymentType
            } else {
                current + paymentType
            }
        }
    }

    fun toggleDay(day: DayOfTheWeekType) {
        _selectedDays.update { current ->
            if (current.contains(day)) {
                current - day
            } else {
                current + day
            }
        }
    }

    fun setStartTime(time: String) {
        _openingHours.update { it.copy(startTime = time) }
    }

    fun setEndTime(time: String) {
        _openingHours.update { it.copy(endTime = time) }
    }

    fun setStoreName(name: String) {
        _storeName.value = name
    }

    fun setStoreType(type: String?) {
        _storeType.value = type
    }

    fun setAddress(address: String) {
        _address.value = address
    }

    fun setSelectedCategoryId(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun setCreatedStoreId(id: Int) {
        _createdStoreId.value = id
    }

    fun setCreatedStoreInfo(info: PostUserStoreModel) {
        _createdStoreInfo.value = info
    }

    fun markMenuDetailCompleted() {
        _isMenuDetailCompleted.value = true
    }

    fun markStoreDetailCompleted() {
        _isStoreDetailCompleted.value = true
    }

    fun updateStoreWithDetails(onSuccess: () -> Unit) {
        val storeId = _createdStoreId.value ?: return
        val location = _selectedLocation.value ?: return
        val categories = _selectCategoryList.value
        val name = _storeName.value

        val menuRequests = categories.flatMap { category ->
            category.menuDetail?.mapNotNull { menu ->
                if (!menu.name.isNullOrBlank()) {
                    com.threedollar.domain.home.request.MenuModelRequest(
                        name = menu.name ?: "",
                        count = null,
                        price = menu.price?.toIntOrNull(),
                        category = category.menuType.categoryId,
                        description = null
                    )
                } else null
            } ?: emptyList()
        }

        val request = UserStoreModelRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            storeName = name,
            salesType = _storeType.value,
            appearanceDays = _selectedDays.value.toList(),
            openingHours = _openingHours.value.takeIf {
                !it.startTime.isNullOrBlank() || !it.endTime.isNullOrBlank()
            },
            paymentMethods = _selectedPaymentMethods.value.toList(),
            menuRequests = menuRequests,
        )

        showLoading()
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putUserStore(request, storeId).collect {
                if (it.ok) {
                    it.data?.let { data -> _createdStoreInfo.value = data }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    fun submitNewStore() {
        val location = _selectedLocation.value ?: return
        val name = _storeName.value
        val salesType = _storeType.value
        val categories = _selectCategoryList.value

        if (name.isBlank()) return

        val menuRequests = categories.flatMap { category ->
            category.menuDetail?.mapNotNull { menu ->
                if (!menu.name.isNullOrBlank()) {
                    com.threedollar.domain.home.request.MenuModelRequest(
                        name = menu.name ?: "",
                        count = extractCount(menu.price),
                        price = extractPriceValue(menu.price),
                        category = category.menuType.categoryId,
                        description = null
                    )
                } else null
            } ?: emptyList()
        }

        val request = UserStoreModelRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            storeName = name,
            salesType = salesType,
            appearanceDays = _selectedDays.value.toList(),
            openingHours = _openingHours.value.takeIf {
                !it.startTime.isNullOrBlank() || !it.endTime.isNullOrBlank()
            },
            paymentMethods = _selectedPaymentMethods.value.toList(),
            menuRequests = menuRequests,
        )

        addNewStore(request)
    }

    private fun extractCount(priceString: String?): Int? {
        if (priceString.isNullOrBlank()) return null
        val parts = priceString.split(" ")
        return if (parts.size >= 2) parts[0].toIntOrNull() else null
    }

    private fun extractPriceValue(priceString: String?): Int? {
        if (priceString.isNullOrBlank()) return null
        val parts = priceString.split(" ")
        return if (parts.size >= 2) parts[1].toIntOrNull() else parts[0].toIntOrNull()
    }
}