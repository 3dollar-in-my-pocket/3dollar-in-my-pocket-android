package com.zion830.threedollars.ui.write.viewModel

import com.naver.maps.geometry.LatLng
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.threedollar.domain.home.data.store.PostUserStoreModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.request.OpeningHourRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.zion830.threedollars.ui.dialog.NearStoreInfo

object AddStoreContract {

    data class State(
        val storeName: String = "",
        val storeType: String? = null,
        val address: String = "",
        val selectedLocation: LatLng? = null,
        val selectCategoryList: List<SelectCategoryModel> = emptyList(),
        val selectedCategoryId: String? = null,
        val availableSnackCategories: List<CategoryModel> = emptyList(),
        val availableMealCategories: List<CategoryModel> = emptyList(),
        val selectedPaymentMethods: Set<PaymentType> = emptySet(),
        val selectedDays: Set<DayOfTheWeekType> = emptySet(),
        val openingHours: OpeningHourRequest = OpeningHourRequest(),
        val createdStoreId: Int? = null,
        val createdStoreInfo: PostUserStoreModel? = null,
        val isMenuDetailCompleted: Boolean = false,
        val isStoreDetailCompleted: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        val isRequiredInfoValid: Boolean
            get() = storeName.isNotBlank() && storeType != null && selectedLocation != null
    }

    sealed class Intent {
        data class SetStoreName(val name: String) : Intent()
        data class SetStoreType(val type: String?) : Intent()
        data class SetAddress(val address: String) : Intent()
        data class UpdateLocation(val location: LatLng?) : Intent()
        data class ChangeSelectCategory(val category: CategoryModel) : Intent()
        data class RemoveCategory(val category: CategoryModel) : Intent()
        object RemoveAllCategories : Intent()
        data class SetSelectedCategoryId(val categoryId: String?) : Intent()
        data class AddMenuToCategory(val categoryId: String) : Intent()
        data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent()
        data class UpdateMenuInCategory(
            val categoryId: String,
            val menuIndex: Int,
            val name: String,
            val price: String,
            val count: Int?
        ) : Intent()
        data class TogglePaymentMethod(val paymentType: PaymentType) : Intent()
        data class ToggleDay(val day: DayOfTheWeekType) : Intent()
        data class SetStartTime(val time: String) : Intent()
        data class SetEndTime(val time: String) : Intent()
        object SubmitNewStore : Intent()
        object UpdateStoreWithDetails : Intent()
        object MarkMenuDetailCompleted : Intent()
        object MarkStoreDetailCompleted : Intent()
        object ClearError : Intent()
        data class SetSelectCategoryList(val list: List<SelectCategoryModel>) : Intent()
        data class CheckNearStore(val location: LatLng) : Intent()
        object ResetState : Intent()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
        data class ShowError(val message: String) : Effect()
        data class StoreCreated(val storeId: Int, val storeInfo: PostUserStoreModel) : Effect()
        object StoreUpdated : Effect()
        data class NearStoreExists(
            val exists: Boolean,
            val nearStores: List<NearStoreInfo> = emptyList()
        ) : Effect()
    }
}
