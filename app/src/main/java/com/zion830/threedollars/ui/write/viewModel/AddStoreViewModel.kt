package com.zion830.threedollars.ui.write.viewModel

import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.MenuModelRequest
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.zion830.threedollars.ui.dialog.NearStoreInfo
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(AddStoreContract.State())
    val state: StateFlow<AddStoreContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddStoreContract.Effect>()
    val effect: SharedFlow<AddStoreContract.Effect> = _effect.asSharedFlow()

    private val _removedCategoriesData: MutableMap<String, List<UserStoreMenuModel>> = mutableMapOf()

    init {
        loadAvailableCategories()
    }

    fun processIntent(intent: AddStoreContract.Intent) {
        when (intent) {
            is AddStoreContract.Intent.SetStoreName -> setStoreName(intent.name)
            is AddStoreContract.Intent.SetStoreType -> setStoreType(intent.type)
            is AddStoreContract.Intent.SetAddress -> setAddress(intent.address)
            is AddStoreContract.Intent.UpdateLocation -> updateLocation(intent.location)
            is AddStoreContract.Intent.ChangeSelectCategory -> changeSelectCategory(intent.category)
            is AddStoreContract.Intent.RemoveCategory -> removeCategory(intent.category)
            is AddStoreContract.Intent.RemoveAllCategories -> removeAllCategories()
            is AddStoreContract.Intent.SetSelectedCategoryId -> setSelectedCategoryId(intent.categoryId)
            is AddStoreContract.Intent.AddMenuToCategory -> addMenuToCategory(intent.categoryId)
            is AddStoreContract.Intent.RemoveMenuFromCategory -> removeMenuFromCategory(intent.categoryId, intent.menuIndex)
            is AddStoreContract.Intent.UpdateMenuInCategory -> updateMenuInCategory(intent.categoryId, intent.menuIndex, intent.name, intent.price, intent.count)
            is AddStoreContract.Intent.TogglePaymentMethod -> togglePaymentMethod(intent.paymentType)
            is AddStoreContract.Intent.ToggleDay -> toggleDay(intent.day)
            is AddStoreContract.Intent.SetStartTime -> setStartTime(intent.time)
            is AddStoreContract.Intent.SetEndTime -> setEndTime(intent.time)
            is AddStoreContract.Intent.SubmitNewStore -> submitNewStore()
            is AddStoreContract.Intent.UpdateStoreWithDetails -> updateStoreWithDetails()
            is AddStoreContract.Intent.MarkMenuDetailCompleted -> markMenuDetailCompleted()
            is AddStoreContract.Intent.MarkStoreDetailCompleted -> markStoreDetailCompleted()
            is AddStoreContract.Intent.ClearError -> clearError()
            is AddStoreContract.Intent.SetSelectCategoryList -> setSelectCategoryModelList(intent.list)
            is AddStoreContract.Intent.EditStore -> editStore(intent.request, intent.storeId)
            is AddStoreContract.Intent.CheckNearStore -> checkNearStore(intent.location)
            is AddStoreContract.Intent.ResetState -> resetState()
        }
    }

    private fun loadAvailableCategories() {
        _state.update {
            it.copy(
                availableSnackCategories = LegacySharedPrefUtils.getCategories(),
                availableMealCategories = LegacySharedPrefUtils.getTruckCategories()
            )
        }
    }

    private fun setStoreName(name: String) {
        _state.update { it.copy(storeName = name) }
    }

    private fun setStoreType(type: String?) {
        _state.update { it.copy(storeType = type) }
    }

    private fun setAddress(address: String) {
        _state.update { it.copy(address = address) }
    }

    private fun updateLocation(location: LatLng?) {
        _state.update { it.copy(selectedLocation = location) }
    }

    private fun setSelectedCategoryId(categoryId: String?) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    private fun changeSelectCategory(categoryModel: CategoryModel) {
        _state.update { currentState ->
            val list = currentState.selectCategoryList
            val newList: List<SelectCategoryModel>
            var newSelectedCategoryId = currentState.selectedCategoryId

            val existsInList = list.any { it.menuType.categoryId == categoryModel.categoryId }

            if (existsInList) {
                val categoryToRemove = list.find { it.menuType.categoryId == categoryModel.categoryId }
                categoryToRemove?.menuDetail?.let { menuList ->
                    if (menuList.isNotEmpty()) {
                        _removedCategoriesData[categoryModel.categoryId] = menuList
                    }
                }

                if (currentState.selectedCategoryId == categoryModel.categoryId) {
                    val remaining = list.filter { it.menuType.categoryId != categoryModel.categoryId }
                    newSelectedCategoryId = remaining.firstOrNull()?.menuType?.categoryId
                }

                newList = list.filter { it.menuType.categoryId != categoryModel.categoryId }
            } else {
                if (list.size < 10) {
                    val savedMenus = _removedCategoriesData[categoryModel.categoryId]
                    val menuDetail = savedMenus ?: listOf(
                        UserStoreMenuModel(
                            category = categoryModel,
                            menuId = 0,
                            name = "",
                            price = ""
                        )
                    )
                    newList = list + SelectCategoryModel(menuType = categoryModel, menuDetail = menuDetail)

                    if (list.isEmpty()) {
                        newSelectedCategoryId = categoryModel.categoryId
                    }
                } else {
                    newList = list
                }
            }

            currentState.copy(
                selectCategoryList = newList,
                selectedCategoryId = newSelectedCategoryId
            )
        }
    }

    private fun removeCategory(categoryModel: CategoryModel) {
        _state.update { currentState ->
            currentState.copy(
                selectCategoryList = currentState.selectCategoryList.filter {
                    it.menuType.categoryId != categoryModel.categoryId
                }
            )
        }
    }

    private fun removeAllCategories() {
        _state.update { it.copy(selectCategoryList = emptyList()) }
    }

    private fun addMenuToCategory(categoryId: String) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { selectCategory ->
                if (selectCategory.menuType.categoryId == categoryId) {
                    val currentMenus = selectCategory.menuDetail ?: emptyList()
                    val newMenu = UserStoreMenuModel(
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
            currentState.copy(selectCategoryList = updatedList)
        }
    }

    private fun removeMenuFromCategory(categoryId: String, menuIndex: Int) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { selectCategory ->
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
            currentState.copy(selectCategoryList = updatedList)
        }
    }

    private fun updateMenuInCategory(categoryId: String, menuIndex: Int, name: String, price: String, count: Int?) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { selectCategory ->
                if (selectCategory.menuType.categoryId == categoryId) {
                    val currentMenus = selectCategory.menuDetail ?: emptyList()
                    val updatedMenus = currentMenus.mapIndexed { index, menu ->
                        if (index == menuIndex) {
                            menu.copy(name = name, price = price, count = count)
                        } else {
                            menu
                        }
                    }
                    selectCategory.copy(menuDetail = updatedMenus)
                } else {
                    selectCategory
                }
            }
            currentState.copy(selectCategoryList = updatedList)
        }
    }

    private fun togglePaymentMethod(paymentType: PaymentType) {
        _state.update { currentState ->
            val current = currentState.selectedPaymentMethods
            val updated = if (current.contains(paymentType)) {
                current - paymentType
            } else {
                current + paymentType
            }
            currentState.copy(selectedPaymentMethods = updated)
        }
    }

    private fun toggleDay(day: DayOfTheWeekType) {
        _state.update { currentState ->
            val current = currentState.selectedDays
            val updated = if (current.contains(day)) {
                current - day
            } else {
                current + day
            }
            currentState.copy(selectedDays = updated)
        }
    }

    private fun setStartTime(time: String) {
        _state.update { currentState ->
            currentState.copy(openingHours = currentState.openingHours.copy(startTime = time))
        }
    }

    private fun setEndTime(time: String) {
        _state.update { currentState ->
            currentState.copy(openingHours = currentState.openingHours.copy(endTime = time))
        }
    }

    private fun convertTo24HourFormat(timeString: String?): String? {
        if (timeString.isNullOrBlank()) return null

        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("a h시 mm분", Locale.KOREAN)
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(timeString, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            timeString
        }
    }

    private fun markMenuDetailCompleted() {
        _state.update { it.copy(isMenuDetailCompleted = true) }
    }

    private fun markStoreDetailCompleted() {
        _state.update { it.copy(isStoreDetailCompleted = true) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateAndNormalizeMenu(menu: UserStoreMenuModel): UserStoreMenuModel {
        val hasName = !menu.name.isNullOrBlank()
        val hasPrice = !menu.price.isNullOrBlank()
        val hasCount = menu.count != null

        return when {
            // 메뉴명 없고 가격/수량 중 하나만 있으면 비어있게
            !hasName && hasPrice && !hasCount -> {
                menu.copy(price = "", count = null)
            }
            !hasName && !hasPrice && hasCount -> {
                menu.copy(price = "", count = null)
            }
            // 메뉴명 있고 가격 있는데 수량 없으면 수량 1로
            hasName && hasPrice && !hasCount -> {
                menu.copy(count = 1)
            }
            // 메뉴명 없고 가격/수량 둘 다 있으면 그대로 사용
            else -> menu
        }
    }

    private fun buildValidatedMenuRequests(
        categories: List<SelectCategoryModel>
    ): List<MenuModelRequest> {
        val validatedMenus = categories.flatMap { category ->
            category.menuDetail?.map { menu ->
                validateAndNormalizeMenu(menu) to category.menuType.categoryId
            } ?: emptyList()
        }

        val menuRequests = validatedMenus.mapNotNull { (menu, categoryId) ->
            val hasName = !menu.name.isNullOrBlank()
            val hasPrice = !menu.price.isNullOrBlank()
            val hasCount = menu.count != null
            if (hasName || (hasPrice && hasCount)) {
                MenuModelRequest(
                    name = menu.name ?: "",
                    count = menu.count,
                    price = menu.price?.toIntOrNull(),
                    category = categoryId,
                    description = null
                )
            } else null
        }

        // 각 카테고리별로 최소 1개 메뉴 보장
        val categoriesWithMenu = menuRequests.map { it.category }.toSet()
        val missingCategoryMenus = categories
            .filter { it.menuType.categoryId !in categoriesWithMenu }
            .map { category ->
                MenuModelRequest(
                    name = "",
                    count = null,
                    price = null,
                    category = category.menuType.categoryId,
                    description = null
                )
            }

        return menuRequests + missingCategoryMenus
    }

    private fun submitNewStore() {
        val currentState = _state.value
        val location = currentState.selectedLocation ?: return
        val name = currentState.storeName

        if (name.isBlank()) return

        val finalMenuRequests = buildValidatedMenuRequests(currentState.selectCategoryList)

        val request = UserStoreModelRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            storeName = name,
            salesType = currentState.storeType,
            appearanceDays = currentState.selectedDays.toList(),
            openingHours = currentState.openingHours.let {
                OpeningHourRequest(
                    startTime = convertTo24HourFormat(it.startTime),
                    endTime = convertTo24HourFormat(it.endTime)
                )
            }.takeIf {
                !it.startTime.isNullOrBlank() || !it.endTime.isNullOrBlank()
            },
            paymentMethods = currentState.selectedPaymentMethods.toList(),
            menuRequests = finalMenuRequests,
        )

        addNewStore(request)
    }

    private fun addNewStore(userStoreModelRequest: UserStoreModelRequest) {
        _state.update { it.copy(isLoading = true, error = null) }
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postUserStore(userStoreModelRequest).collect {
                if (it.ok) {
                    it.data?.let { data ->
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                createdStoreId = data.storeId,
                                createdStoreInfo = data
                            )
                        }
                        _effect.emit(AddStoreContract.Effect.StoreCreated(data.storeId, data))
                    }
                } else {
                    _state.update { state -> state.copy(isLoading = false, error = it.message) }
                    _effect.emit(AddStoreContract.Effect.ShowError(it.message ?: "Unknown error"))
                    _serverError.emit(it.message)
                }
            }
            hideLoading()
        }
    }

    private fun updateStoreWithDetails() {
        val currentState = _state.value
        val storeId = currentState.createdStoreId ?: return
        val location = currentState.selectedLocation ?: return

        val menuRequests = buildValidatedMenuRequests(currentState.selectCategoryList)

        val request = UserStoreModelRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            storeName = currentState.storeName,
            salesType = currentState.storeType,
            appearanceDays = currentState.selectedDays.toList(),
            openingHours = currentState.openingHours.let {
                OpeningHourRequest(
                    startTime = convertTo24HourFormat(it.startTime),
                    endTime = convertTo24HourFormat(it.endTime)
                )
            }.takeIf {
                !it.startTime.isNullOrBlank() || !it.endTime.isNullOrBlank()
            },
            paymentMethods = currentState.selectedPaymentMethods.toList(),
            menuRequests = menuRequests,
        )

        _state.update { it.copy(isLoading = true, error = null) }
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putUserStore(request, storeId).collect {
                if (it.ok) {
                    it.data?.let { data ->
                        _state.update { state -> state.copy(isLoading = false, createdStoreInfo = data) }
                    }
                    _effect.emit(AddStoreContract.Effect.StoreUpdated)
                } else {
                    _state.update { state -> state.copy(isLoading = false, error = it.message) }
                    _effect.emit(AddStoreContract.Effect.ShowError(it.message ?: "Unknown error"))
                    _serverError.emit(it.message)
                }
            }
            hideLoading()
        }
    }

    private fun setSelectCategoryModelList(list: List<SelectCategoryModel>) {
        _state.update { it.copy(selectCategoryList = list) }
    }

    private fun editStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int) {
        _state.update { it.copy(isLoading = true, error = null) }
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putUserStore(userStoreModelRequest, storeId).collect {
                if (it.ok) {
                    it.data?.let { data ->
                        _state.update { state -> state.copy(isLoading = false, createdStoreInfo = data) }
                    }
                    _effect.emit(AddStoreContract.Effect.StoreUpdated)
                } else {
                    _state.update { state -> state.copy(isLoading = false, error = it.message) }
                    _effect.emit(AddStoreContract.Effect.ShowError(it.message ?: "Unknown error"))
                    _serverError.emit(it.message)
                }
            }
            hideLoading()
        }
    }

    private fun checkNearStore(location: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getStoreNearExists(
                distance = 50.0,
                mapLatitude = location.latitude,
                mapLongitude = location.longitude
            ).collect { response ->
                val stores = response.data?.contentModels ?: emptyList()
                val nearStoreInfos = NearStoreInfo.fromContentModels(stores)
                _effect.emit(
                    AddStoreContract.Effect.NearStoreExists(
                        exists = stores.isNotEmpty(),
                        nearStores = nearStoreInfos
                    )
                )
            }
        }
    }

    private fun resetState() {
        _state.update { AddStoreContract.State() }
        _removedCategoriesData.clear()
        loadAvailableCategories()
    }
}
