package com.zion830.threedollars.ui.write.viewModel

import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
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
        }
    }

    private fun initWithStoreData(intent: EditStoreContract.Intent.InitWithStoreData) {
        _state.update {
            it.copy(
                storeId = intent.storeId,
                storeName = intent.storeName,
                storeType = intent.storeType,
                selectedLocation = intent.location,
                selectCategoryList = intent.categories,
                selectedPaymentMethods = intent.paymentMethods,
                selectedDays = intent.appearanceDays,
                openingHours = intent.openingHours,
                isInitialized = true
            )
        }
    }

    private fun updateLocation(location: LatLng?) {
        _state.update { it.copy(selectedLocation = location) }
    }

    private fun updateTempLocation(location: LatLng?) {
        _state.update { it.copy(tempLocation = location) }
    }

    private fun confirmLocation() {
        _state.update { it.copy(selectedLocation = it.tempLocation, tempLocation = null) }
    }

    private fun cancelLocationEdit() {
        _state.update { it.copy(tempLocation = null) }
    }

    private fun setSelectCategoryList(list: List<SelectCategoryModel>) {
        _state.update { it.copy(selectCategoryList = list) }
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

            currentState.copy(selectCategoryList = newList)
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
}
