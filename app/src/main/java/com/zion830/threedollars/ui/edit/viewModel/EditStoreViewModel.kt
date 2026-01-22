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
import com.threedollar.domain.home.request.MenuModelRequest
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.TimeUtils
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
            is EditStoreContract.Intent.LoadStoreDetail -> loadStoreDetail(intent.storeId)
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
            is EditStoreContract.Intent.StartInfoEdit -> startInfoEdit()
            is EditStoreContract.Intent.ConfirmInfoChanges -> confirmInfoChanges()
            is EditStoreContract.Intent.CancelInfoEdit -> cancelInfoEdit()
            is EditStoreContract.Intent.StartMenuEdit -> startMenuEdit()
            is EditStoreContract.Intent.ConfirmMenuChanges -> confirmMenuChanges()
            is EditStoreContract.Intent.CancelMenuEdit -> cancelMenuEdit()
        }
    }


    private fun loadStoreDetail(storeId: Int) {
        if (_state.value.isInitialized) return

        viewModelScope.launch(coroutineExceptionHandler) {
            _state.update { it.copy(isLoading = true) }
            showLoading()

            homeRepository.getUserStoreDetail(
                storeId = storeId,
                deviceLatitude = 0.0,
                deviceLongitude = 0.0,
                storeImagesCount = null,
                reviewsCount = null,
                visitHistoriesCount = null,
                filterVisitStartDate = ""
            ).collect { response ->
                hideLoading()
                val storeDetail = response.data
                if (response.ok && storeDetail != null) {
                    initializeFromStoreDetail(storeDetail)
                } else {
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(EditStoreContract.Effect.ShowError(response.message ?: "Failed to load store"))
                }
            }
        }
    }

    private fun initializeFromStoreDetail(storeDetail: com.threedollar.domain.home.data.store.UserStoreDetailModel) {
        val store = storeDetail.store
        val location = LatLng(store.location.latitude, store.location.longitude)

        val selectCategoryModelList = store.categories.map { categoryModel ->
            val menus = store.menus.filter { menuModel ->
                menuModel.category.categoryId == categoryModel.categoryId
            }
            SelectCategoryModel(menuType = categoryModel, menus)
        }

        val openingHours = com.threedollar.domain.home.request.OpeningHourRequest(
            startTime = store.openingHoursModel.startTime,
            endTime = store.openingHoursModel.endTime
        )

        _state.update {
            it.copy(
                storeId = store.storeId,
                storeName = store.name,
                storeType = store.salesType.name,
                selectedLocation = location,
                address = store.address.fullAddress,
                selectCategoryList = selectCategoryModelList,
                selectedPaymentMethods = store.paymentMethods.toSet(),
                selectedDays = store.appearanceDays.toSet(),
                openingHours = openingHours,
                isLoading = false,
                isInitialized = true,
                originalStoreData = EditStoreContract.OriginalStoreData(
                    storeName = store.name,
                    storeType = store.salesType.name,
                    location = location,
                    address = store.address.fullAddress,
                    paymentMethods = store.paymentMethods.toSet(),
                    appearanceDays = store.appearanceDays.toSet(),
                    openingHours = openingHours,
                    categories = selectCategoryModelList
                )
            )
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
            val list = currentState.tempSelectCategoryList ?: currentState.selectCategoryList
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

            currentState.copy(tempSelectCategoryList = newList)
        }
    }

    private fun removeCategory(categoryModel: CategoryModel) {
        _state.update { currentState ->
            val list = currentState.tempSelectCategoryList ?: currentState.selectCategoryList
            currentState.copy(
                tempSelectCategoryList = list.filter {
                    it.menuType.categoryId != categoryModel.categoryId
                }
            )
        }
    }

    private fun removeAllCategories() {
        _state.update { currentState ->
            currentState.copy(tempSelectCategoryList = emptyList())
        }
    }


    private fun validateAndNormalizeMenu(menu: UserStoreMenuModel): UserStoreMenuModel {
        val hasName = !menu.name.isNullOrBlank()
        val hasPrice = !menu.price.isNullOrBlank()
        val hasCount = menu.count != null

        return when {
            !hasName && hasPrice && !hasCount -> menu.copy(price = "", count = null)
            !hasName && !hasPrice && hasCount -> menu.copy(price = "", count = null)
            hasName && hasPrice && !hasCount -> menu.copy(count = 1)
            else -> menu
        }
    }

    private fun buildValidatedMenuRequests(categories: List<SelectCategoryModel>): List<MenuModelRequest> {
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

    private fun buildSubmitRequest(): UserStoreModelRequest {
        val state = _state.value
        val original = state.originalStoreData

        val hasLocationChange = original?.location?.latitude != state.selectedLocation?.latitude ||
                               original?.location?.longitude != state.selectedLocation?.longitude
        val hasNameChange = original?.storeName != state.storeName
        val hasTypeChange = original?.storeType != state.storeType
        val hasDaysChange = original?.appearanceDays != state.selectedDays
        val hasHoursChange = original?.openingHours != state.openingHours
        val hasPaymentChange = original?.paymentMethods != state.selectedPaymentMethods
        val hasMenuChange = original?.categories != state.selectCategoryList

        val menuRequests = if (hasMenuChange) {
            buildValidatedMenuRequests(state.selectCategoryList)
        } else null

        return UserStoreModelRequest(
            latitude = if (hasLocationChange) state.selectedLocation?.latitude else null,
            longitude = if (hasLocationChange) state.selectedLocation?.longitude else null,
            storeName = if (hasNameChange) state.storeName else null,
            salesType = if (hasTypeChange) state.storeType else null,
            appearanceDays = if (hasDaysChange) state.selectedDays.toList() else null,
            openingHours = if (hasHoursChange) {
                OpeningHourRequest(
                    startTime = TimeUtils.convertTo24HourFormat(state.openingHours.startTime),
                    endTime = TimeUtils.convertTo24HourFormat(state.openingHours.endTime)
                )
            } else null,
            paymentMethods = if (hasPaymentChange) state.selectedPaymentMethods.toList() else null,
            menuRequests = menuRequests
        )
    }

    private fun submitEdit(requestParam: UserStoreModelRequest? = null) {
        val request = requestParam ?: buildSubmitRequest()
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
        when (screen) {
            EditStoreContract.EditScreen.Location -> {
                viewModelScope.launch {
                    _effect.emit(EditStoreContract.Effect.NavigateToLocationEdit)
                }
            }
            EditStoreContract.EditScreen.StoreInfo -> {
                startInfoEdit()
                _state.update { it.copy(currentScreen = screen) }
            }
            EditStoreContract.EditScreen.StoreMenu -> {
                startMenuEdit()
                _state.update { it.copy(currentScreen = screen) }
            }
            else -> {
                _state.update { it.copy(currentScreen = screen) }
            }
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
        _state.update { it.copy(tempStoreName = name) }
    }

    private fun updateStoreType(type: String) {
        _state.update { it.copy(tempStoreType = type) }
    }

    private fun togglePaymentMethod(method: PaymentType) {
        _state.update { currentState ->
            val currentMethods = currentState.tempSelectedPaymentMethods ?: currentState.selectedPaymentMethods
            val newMethods = if (currentMethods.contains(method)) {
                currentMethods - method
            } else {
                currentMethods + method
            }
            currentState.copy(tempSelectedPaymentMethods = newMethods)
        }
    }

    private fun toggleAppearanceDay(day: DayOfTheWeekType) {
        _state.update { currentState ->
            val currentDays = currentState.tempSelectedDays ?: currentState.selectedDays
            val newDays = if (currentDays.contains(day)) {
                currentDays - day
            } else {
                currentDays + day
            }.sortedBy { it.ordinal }.toCollection(linkedSetOf())
            currentState.copy(tempSelectedDays = newDays)
        }
    }

    private fun updateStartTime(time: String?) {
        _state.update { currentState ->
            val currentHours = currentState.tempOpeningHours ?: currentState.openingHours
            currentState.copy(tempOpeningHours = currentHours.copy(startTime = time))
        }
    }

    private fun updateEndTime(time: String?) {
        _state.update { currentState ->
            val currentHours = currentState.tempOpeningHours ?: currentState.openingHours
            currentState.copy(tempOpeningHours = currentHours.copy(endTime = time))
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
            val list = currentState.tempSelectCategoryList ?: currentState.selectCategoryList
            val updatedList = list.map { category ->
                if (category.menuType.categoryId == categoryId) {
                    val currentMenus = category.menuDetail?.toMutableList() ?: mutableListOf()
                    currentMenus.add(UserStoreMenuModel())
                    category.copy(menuDetail = currentMenus)
                } else {
                    category
                }
            }
            currentState.copy(tempSelectCategoryList = updatedList)
        }
    }

    private fun removeMenuFromCategory(categoryId: String, menuIndex: Int) {
        _state.update { currentState ->
            val list = currentState.tempSelectCategoryList ?: currentState.selectCategoryList
            val updatedList = list.map { category ->
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
            currentState.copy(tempSelectCategoryList = updatedList)
        }
    }

    private fun updateMenuInCategory(categoryId: String, menuIndex: Int, name: String, price: String, count: Int?) {
        _state.update { currentState ->
            val list = currentState.tempSelectCategoryList ?: currentState.selectCategoryList
            val updatedList = list.map { category ->
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
            currentState.copy(tempSelectCategoryList = updatedList)
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

    private fun startInfoEdit() {
        _state.update { currentState ->
            currentState.copy(
                tempStoreName = currentState.storeName,
                tempStoreType = currentState.storeType,
                tempSelectedPaymentMethods = currentState.selectedPaymentMethods,
                tempSelectedDays = currentState.selectedDays,
                tempOpeningHours = currentState.openingHours
            )
        }
    }

    private fun confirmInfoChanges() {
        _state.update { currentState ->
            val newState = currentState.copy(
                storeName = currentState.tempStoreName ?: currentState.storeName,
                storeType = currentState.tempStoreType ?: currentState.storeType,
                selectedPaymentMethods = currentState.tempSelectedPaymentMethods ?: currentState.selectedPaymentMethods,
                selectedDays = currentState.tempSelectedDays ?: currentState.selectedDays,
                openingHours = currentState.tempOpeningHours ?: currentState.openingHours,
                tempStoreName = null,
                tempStoreType = null,
                tempSelectedPaymentMethods = null,
                tempSelectedDays = null,
                tempOpeningHours = null,
                currentScreen = EditStoreContract.EditScreen.Selection
            )
            newState.copy(hasInfoChanges = checkInfoChanges(newState))
        }
    }

    private fun cancelInfoEdit() {
        _state.update { currentState ->
            currentState.copy(
                tempStoreName = null,
                tempStoreType = null,
                tempSelectedPaymentMethods = null,
                tempSelectedDays = null,
                tempOpeningHours = null,
                currentScreen = EditStoreContract.EditScreen.Selection
            )
        }
    }

    private fun startMenuEdit() {
        _state.update { currentState ->
            currentState.copy(
                tempSelectCategoryList = currentState.selectCategoryList
            )
        }
    }

    private fun confirmMenuChanges() {
        _state.update { currentState ->
            val newState = currentState.copy(
                selectCategoryList = currentState.tempSelectCategoryList ?: currentState.selectCategoryList,
                tempSelectCategoryList = null,
                currentScreen = EditStoreContract.EditScreen.Selection
            )
            newState.copy(hasMenuChanges = checkMenuChanges(newState))
        }
    }

    private fun cancelMenuEdit() {
        _state.update { currentState ->
            currentState.copy(
                tempSelectCategoryList = null,
                currentScreen = EditStoreContract.EditScreen.Selection
            )
        }
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
