package com.zion830.threedollars.ui.edit.viewModel

import com.naver.maps.geometry.LatLng
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest

object EditStoreContract {

    data class State(
        val storeId: Int = 0,
        val storeName: String = "",
        val storeType: String? = null,
        val address: String = "",
        val selectedLocation: LatLng? = null,
        val tempLocation: LatLng? = null,
        val selectCategoryList: List<SelectCategoryModel> = emptyList(),
        val availableSnackCategories: List<CategoryModel> = emptyList(),
        val availableMealCategories: List<CategoryModel> = emptyList(),
        val selectedPaymentMethods: Set<PaymentType> = emptySet(),
        val selectedDays: Set<DayOfTheWeekType> = emptySet(),
        val openingHours: OpeningHourRequest = OpeningHourRequest(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isInitialized: Boolean = false,
        val currentScreen: EditScreen = EditScreen.Selection,
        val originalStoreData: OriginalStoreData? = null,
        val hasLocationChanges: Boolean = false,
        val hasInfoChanges: Boolean = false,
        val hasMenuChanges: Boolean = false,
        val showExitConfirmDialog: Boolean = false,
        val selectedCategoryId: String? = null
    ) {
        val totalChangedCount: Int
            get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges).count { it }

        val hasAnyChanges: Boolean
            get() = hasLocationChanges || hasInfoChanges || hasMenuChanges

        val menuCount: Int
            get() = selectCategoryList.sumOf { it.menuDetail?.size ?: 0 }

        val isSubmitEnabled: Boolean
            get() = hasAnyChanges && !isLoading
    }

    enum class EditScreen {
        Selection,
        Location,
        StoreInfo,
        StoreMenu
    }

    data class OriginalStoreData(
        val storeName: String,
        val storeType: String?,
        val location: LatLng?,
        val address: String,
        val paymentMethods: Set<PaymentType>,
        val appearanceDays: Set<DayOfTheWeekType>,
        val openingHours: OpeningHourRequest,
        val categories: List<SelectCategoryModel>
    )

    sealed class Intent {
        data class InitWithStoreData(
            val storeId: Int,
            val storeName: String,
            val storeType: String?,
            val location: LatLng,
            val address: String,
            val categories: List<SelectCategoryModel>,
            val paymentMethods: Set<PaymentType>,
            val appearanceDays: Set<DayOfTheWeekType>,
            val openingHours: OpeningHourRequest
        ) : Intent()

        data class UpdateLocation(val location: LatLng?) : Intent()
        data class UpdateTempLocation(val location: LatLng?) : Intent()
        data object ConfirmLocation : Intent()
        data object CancelLocationEdit : Intent()
        data class SetSelectCategoryList(val list: List<SelectCategoryModel>) : Intent()
        data class ChangeSelectCategory(val category: CategoryModel) : Intent()
        data class RemoveCategory(val category: CategoryModel) : Intent()
        data object RemoveAllCategories : Intent()
        data class SubmitEdit(val request: UserStoreModelRequest) : Intent()

        data class NavigateToScreen(val screen: EditScreen) : Intent()
        data object NavigateBack : Intent()

        data class UpdateStoreName(val name: String) : Intent()
        data class UpdateStoreType(val type: String) : Intent()
        data class TogglePaymentMethod(val method: PaymentType) : Intent()
        data class ToggleAppearanceDay(val day: DayOfTheWeekType) : Intent()
        data class UpdateStartTime(val time: String?) : Intent()
        data class UpdateEndTime(val time: String?) : Intent()

        data class UpdateAddress(val address: String) : Intent()

        data class SetSelectedCategoryId(val categoryId: String?) : Intent()
        data class AddMenuToCategory(val categoryId: String) : Intent()
        data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent()
        data class UpdateMenuInCategory(
            val categoryId: String,
            val menuIndex: Int,
            val name: String,
            val price: String
        ) : Intent()

        data object ShowExitConfirmDialog : Intent()
        data object HideExitConfirmDialog : Intent()
        data object ConfirmExit : Intent()

        data object ClearError : Intent()
    }

    sealed class Effect {
        data object StoreUpdated : Effect()
        data class ShowError(val message: String) : Effect()
        data class ShowToast(val message: String) : Effect()
        data object NavigateToLocationEdit : Effect()
        data object NavigateBack : Effect()
        data object CloseScreen : Effect()
    }
}
