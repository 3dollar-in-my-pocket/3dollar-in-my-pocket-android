package com.zion830.threedollars.ui.edit.viewModel

import com.naver.maps.geometry.LatLng
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.zion830.threedollars.ui.dialog.category.StoreCategory

object EditStoreContract {

    data class State(
        val storeId: Int = 0,
        val storeName: String = "",
        val storeType: String? = null,
        val photoCount: Int = 0,
        val address: String = "",
        val selectedLocation: LatLng? = null,
        val tempLocation: LatLng? = null,
        val selectCategoryList: List<SelectCategoryModel> = emptyList(),
        val storeCategories: List<StoreCategory> = emptyList(),
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
        val pendingPhotos: List<PendingPhoto> = emptyList(),
        val isPhotoUploading: Boolean = false,
        val showExitConfirmDialog: Boolean = false,
        val showSubmitErrorDialog: Boolean = false,
        val submitErrorMessage: String? = null,
        val selectedCategoryId: String? = null,
        val tempStoreName: String? = null,
        val tempStoreType: String? = null,
        val tempSelectedPaymentMethods: Set<PaymentType>? = null,
        val tempSelectedDays: Set<DayOfTheWeekType>? = null,
        val tempOpeningHours: OpeningHourRequest? = null,
        val tempSelectCategoryList: List<SelectCategoryModel>? = null
    ) {
        val totalChangedCount: Int
            get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges, hasPhotoChanges).count { it }

        val pendingPhotoCount: Int
            get() = pendingPhotos.size

        val totalPhotoCount: Int
            get() = photoCount + pendingPhotoCount

        val hasPhotoChanges: Boolean
            get() = pendingPhotos.isNotEmpty()

        val hasAnyChanges: Boolean
            get() = hasLocationChanges || hasInfoChanges || hasMenuChanges || hasPhotoChanges

        val menuCount: Int
            get() = selectCategoryList.sumOf { it.menuDetail?.size ?: 0 }

        val isSubmitEnabled: Boolean
            get() = hasAnyChanges && !isLoading
    }

    data class PendingPhoto(
        val id: String,
        val uriString: String,
        val cachedFilePath: String,
        val displayName: String,
    )

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

    sealed interface Intent {
        data class LoadStoreDetail(val storeId: Int) : Intent

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
        ) : Intent

        data class UpdateLocation(val location: LatLng?) : Intent
        data class UpdateTempLocation(val location: LatLng?) : Intent
        data object ConfirmLocation : Intent
        data object CancelLocationEdit : Intent
        data class SetSelectCategoryList(val list: List<SelectCategoryModel>) : Intent
        data class ChangeSelectCategory(val category: CategoryModel) : Intent
        data class UpdateSelectedCategories(val categoryIds: List<String>) : Intent
        data class RemoveCategory(val category: CategoryModel) : Intent
        data object RemoveAllCategories : Intent
        data class SubmitEdit(val request: UserStoreModelRequest? = null) : Intent
        data object RetrySubmit : Intent

        data class NavigateToScreen(val screen: EditScreen) : Intent
        data object NavigateBack : Intent
        data object OpenPhotoPicker : Intent
        data class AddPendingPhotos(val photos: List<PendingPhoto>) : Intent
        data class RemovePendingPhoto(val photoId: String) : Intent

        data class UpdateStoreName(val name: String) : Intent
        data class UpdateStoreType(val type: String) : Intent
        data class TogglePaymentMethod(val method: PaymentType) : Intent
        data class ToggleAppearanceDay(val day: DayOfTheWeekType) : Intent
        data class UpdateStartTime(val time: String?) : Intent
        data class UpdateEndTime(val time: String?) : Intent

        data class UpdateAddress(val address: String) : Intent

        data class SetSelectedCategoryId(val categoryId: String?) : Intent
        data class AddMenuToCategory(val categoryId: String) : Intent
        data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent
        data class UpdateMenuInCategory(
            val categoryId: String,
            val menuIndex: Int,
            val name: String,
            val price: String,
            val count: Int?
        ) : Intent

        data object ShowExitConfirmDialog : Intent
        data object HideExitConfirmDialog : Intent
        data object ConfirmExit : Intent

        data object ClearError : Intent
        data object DismissSubmitError : Intent

        data object StartInfoEdit : Intent
        data object ConfirmInfoChanges : Intent
        data object CancelInfoEdit : Intent

        data object StartMenuEdit : Intent
        data object ConfirmMenuChanges : Intent
        data object CancelMenuEdit : Intent
    }

    sealed interface Effect {
        data object StoreUpdated : Effect
        data class ShowError(val message: String) : Effect
        data class ShowToast(val message: String) : Effect
        data object NavigateToLocationEdit : Effect
        data object NavigateBack : Effect
        data object LaunchPhotoPicker : Effect
        data object CloseScreen : Effect
    }
}
