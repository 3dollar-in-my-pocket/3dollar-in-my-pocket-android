package com.zion830.threedollars.ui.edit.viewModel

import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.UserStoreModelRequest
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
class EditStoreViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(EditStoreContract.State())
    val state: StateFlow<EditStoreContract.State> = _state.asStateFlow()
    override val screenName: ScreenName = ScreenName.EDIT_STORE

    private val _effect = MutableSharedFlow<EditStoreContract.Effect>()
    val effect: SharedFlow<EditStoreContract.Effect> = _effect.asSharedFlow()

    init {
        loadAvailableCategories()
    }

    private fun loadAvailableCategories() {
        _state.update {
            it.copy(
                availableSnackCategories = LegacySharedPrefUtils.getCategories(),
                availableMealCategories = LegacySharedPrefUtils.getTruckCategories()
            )
        }
    }

    fun processIntent(intent: EditStoreContract.Intent) {
        when (intent) {
            is EditStoreContract.Intent.InitWithStoreData -> initWithStoreData(intent)
            is EditStoreContract.Intent.UpdateLocation -> updateLocation(intent.location)
            is EditStoreContract.Intent.UpdateTempLocation -> updateTempLocation(intent.location)
            is EditStoreContract.Intent.ConfirmLocation -> confirmLocation()
            is EditStoreContract.Intent.CancelLocationEdit -> cancelLocationEdit()
            is EditStoreContract.Intent.SetSelectCategoryList -> setSelectCategoryList(intent.list)
            is EditStoreContract.Intent.ChangeSelectCategory -> changeSelectCategory(intent.category)
            is EditStoreContract.Intent.RemoveCategory -> removeCategory(intent.category)
            is EditStoreContract.Intent.RemoveAllCategories -> removeAllCategories()
            is EditStoreContract.Intent.SubmitEdit -> submitEdit(intent.request)
            is EditStoreContract.Intent.NavigateToScreen -> navigateToScreen(intent.screen)
            is EditStoreContract.Intent.NavigateBack -> navigateBack()
            is EditStoreContract.Intent.UpdateStoreName -> updateStoreName(intent.name)
            is EditStoreContract.Intent.UpdateStoreType -> updateStoreType(intent.type)
            is EditStoreContract.Intent.TogglePaymentMethod -> togglePaymentMethod(intent.method)
            is EditStoreContract.Intent.ToggleAppearanceDay -> toggleAppearanceDay(intent.day)
            is EditStoreContract.Intent.UpdateStartTime -> updateStartTime(intent.time)
            is EditStoreContract.Intent.UpdateEndTime -> updateEndTime(intent.time)
            is EditStoreContract.Intent.UpdateAddress -> updateAddress(intent.address)
            is EditStoreContract.Intent.SetSelectedCategoryId -> setSelectedCategoryId(intent.categoryId)
            is EditStoreContract.Intent.AddMenuToCategory -> addMenuToCategory(intent.categoryId)
            is EditStoreContract.Intent.RemoveMenuFromCategory -> removeMenuFromCategory(intent.categoryId, intent.menuIndex)
            is EditStoreContract.Intent.UpdateMenuInCategory -> updateMenuInCategory(intent.categoryId, intent.menuIndex, intent.name, intent.price, intent.count)
            is EditStoreContract.Intent.ShowExitConfirmDialog -> showExitConfirmDialog()
            is EditStoreContract.Intent.HideExitConfirmDialog -> hideExitConfirmDialog()
            is EditStoreContract.Intent.ConfirmExit -> confirmExit()
            is EditStoreContract.Intent.ClearError -> clearError()
        }
    }

    private fun initWithStoreData(intent: EditStoreContract.Intent.InitWithStoreData) {
        _state.update {
            it.copy(
                storeId = intent.storeId,
                storeName = intent.storeName,
                storeType = intent.storeType,
                selectedLocation = intent.location,
                address = intent.address,
                selectCategoryList = intent.categories,
                selectedPaymentMethods = intent.paymentMethods,
                selectedDays = intent.appearanceDays,
                openingHours = intent.openingHours,
                isInitialized = true,
                originalStoreData = EditStoreContract.OriginalStoreData(
                    storeName = intent.storeName,
                    storeType = intent.storeType,
                    location = intent.location,
                    address = intent.address,
                    paymentMethods = intent.paymentMethods,
                    appearanceDays = intent.appearanceDays,
                    openingHours = intent.openingHours,
                    categories = intent.categories
                )
            )
        }
    }

    private fun updateLocation(location: LatLng?) {
        _state.update { currentState ->
            val newState = currentState.copy(selectedLocation = location)
            newState.copy(hasLocationChanges = checkLocationChanges(newState))
        }
    }

    private fun updateTempLocation(location: LatLng?) {
        _state.update { it.copy(tempLocation = location) }
    }

    private fun confirmLocation() {
        _state.update { currentState ->
            val newState = currentState.copy(selectedLocation = currentState.tempLocation, tempLocation = null)
            newState.copy(hasLocationChanges = checkLocationChanges(newState))
        }
    }

    private fun cancelLocationEdit() {
        _state.update { it.copy(tempLocation = null) }
    }

    private fun setSelectCategoryList(list: List<SelectCategoryModel>) {
        _state.update { currentState ->
            val newState = currentState.copy(selectCategoryList = list)
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun changeSelectCategory(categoryModel: CategoryModel) {
        _state.update { currentState ->
            val list = currentState.selectCategoryList
            val existsInList = list.any { it.menuType.categoryId == categoryModel.categoryId }

            val newList = if (existsInList) {
                list.filter { it.menuType.categoryId != categoryModel.categoryId }
            } else {
                if (list.size < 10) {
                    list + SelectCategoryModel(menuType = categoryModel, menuDetail = emptyList())
                } else {
                    list
                }
            }

            val newState = currentState.copy(selectCategoryList = newList)
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun removeCategory(categoryModel: CategoryModel) {
        _state.update { currentState ->
            val newState = currentState.copy(
                selectCategoryList = currentState.selectCategoryList.filter {
                    it.menuType.categoryId != categoryModel.categoryId
                }
            )
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun removeAllCategories() {
        _state.update { currentState ->
            val newState = currentState.copy(selectCategoryList = emptyList())
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun submitEdit(request: UserStoreModelRequest) {
        val storeId = _state.value.storeId
        if (storeId == 0) return

        _state.update { it.copy(isLoading = true, error = null) }
        showLoading()

        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putUserStore(request, storeId).collect {
                hideLoading()
                if (it.ok) {
                    _state.update { state -> state.copy(isLoading = false) }
                    _effect.emit(EditStoreContract.Effect.StoreUpdated)
                } else {
                    _state.update { state -> state.copy(isLoading = false, error = it.message) }
                    _effect.emit(EditStoreContract.Effect.ShowError(it.message ?: "Unknown error"))
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun navigateToScreen(screen: EditStoreContract.EditScreen) {
        if (screen == EditStoreContract.EditScreen.Location) {
            viewModelScope.launch {
                _effect.emit(EditStoreContract.Effect.NavigateToLocationEdit)
            }
        } else {
            _state.update { it.copy(currentScreen = screen) }
        }
    }

    private fun navigateBack() {
        val currentState = _state.value
        if (currentState.currentScreen == EditStoreContract.EditScreen.Selection) {
            viewModelScope.launch {
                _effect.emit(EditStoreContract.Effect.NavigateBack)
            }
        } else {
            _state.update { it.copy(currentScreen = EditStoreContract.EditScreen.Selection) }
        }
    }

    private fun updateStoreName(name: String) {
        _state.update { currentState ->
            val newState = currentState.copy(storeName = name)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun updateStoreType(type: String) {
        _state.update { currentState ->
            val newState = currentState.copy(storeType = type)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun togglePaymentMethod(method: PaymentType) {
        _state.update { currentState ->
            val newMethods = if (currentState.selectedPaymentMethods.contains(method)) {
                currentState.selectedPaymentMethods - method
            } else {
                currentState.selectedPaymentMethods + method
            }
            val newState = currentState.copy(selectedPaymentMethods = newMethods)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun toggleAppearanceDay(day: DayOfTheWeekType) {
        _state.update { currentState ->
            val newDays = if (currentState.selectedDays.contains(day)) {
                currentState.selectedDays - day
            } else {
                currentState.selectedDays + day
            }
            val newState = currentState.copy(selectedDays = newDays)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun updateStartTime(time: String?) {
        _state.update { currentState ->
            val newHours = currentState.openingHours.copy(startTime = time)
            val newState = currentState.copy(openingHours = newHours)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun updateEndTime(time: String?) {
        _state.update { currentState ->
            val newHours = currentState.openingHours.copy(endTime = time)
            val newState = currentState.copy(openingHours = newHours)
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun updateAddress(address: String) {
        _state.update { currentState ->
            val newState = currentState.copy(address = address)
            newState.copy(hasLocationChanges = checkLocationChanges(newState))
        }
    }

    private fun setSelectedCategoryId(categoryId: String?) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    private fun addMenuToCategory(categoryId: String) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { category ->
                if (category.menuType.categoryId == categoryId) {
                    val currentMenus = category.menuDetail?.toMutableList() ?: mutableListOf()
                    currentMenus.add(UserStoreMenuModel())
                    category.copy(menuDetail = currentMenus)
                } else {
                    category
                }
            }
            val newState = currentState.copy(selectCategoryList = updatedList)
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun removeMenuFromCategory(categoryId: String, menuIndex: Int) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { category ->
                if (category.menuType.categoryId == categoryId) {
                    val currentMenus = category.menuDetail?.toMutableList() ?: mutableListOf()
                    if (menuIndex in currentMenus.indices) {
                        currentMenus.removeAt(menuIndex)
                    }
                    category.copy(menuDetail = currentMenus)
                } else {
                    category
                }
            }
            val newState = currentState.copy(selectCategoryList = updatedList)
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun updateMenuInCategory(categoryId: String, menuIndex: Int, name: String, price: String, count: Int?) {
        _state.update { currentState ->
            val updatedList = currentState.selectCategoryList.map { category ->
                if (category.menuType.categoryId == categoryId) {
                    val currentMenus = category.menuDetail?.toMutableList() ?: mutableListOf()
                    if (menuIndex in currentMenus.indices) {
                        val existingMenu = currentMenus[menuIndex]
                        currentMenus[menuIndex] = existingMenu.copy(name = name, price = price, count = count)
                    }
                    category.copy(menuDetail = currentMenus)
                } else {
                    category
                }
            }
            val newState = currentState.copy(selectCategoryList = updatedList)
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun showExitConfirmDialog() {
        _state.update { it.copy(showExitConfirmDialog = true) }
    }

    private fun hideExitConfirmDialog() {
        _state.update { it.copy(showExitConfirmDialog = false) }
    }

    private fun confirmExit() {
        viewModelScope.launch {
            _state.update { it.copy(showExitConfirmDialog = false) }
            _effect.emit(EditStoreContract.Effect.CloseScreen)
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun checkInfoChanges(state: EditStoreContract.State): Boolean {
        val original = state.originalStoreData ?: return false
        return state.storeName != original.storeName ||
                state.storeType != original.storeType ||
                state.selectedPaymentMethods != original.paymentMethods ||
                state.selectedDays != original.appearanceDays ||
                state.openingHours != original.openingHours
    }

    private fun checkLocationChanges(state: EditStoreContract.State): Boolean {
        val original = state.originalStoreData ?: return false
        return state.selectedLocation != original.location ||
                state.address != original.address
    }

    private fun checkMenuChanges(state: EditStoreContract.State): Boolean {
        val original = state.originalStoreData ?: return false
        return state.selectCategoryList != original.categories
    }
}
