package com.zion830.threedollars.ui.write.viewModel

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
        val isInitialized: Boolean = false
    )

    sealed class Intent {
        data class InitWithStoreData(
            val storeId: Int,
            val storeName: String,
            val storeType: String?,
            val location: LatLng,
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
        object RemoveAllCategories : Intent()
        data class SubmitEdit(val request: UserStoreModelRequest) : Intent()
    }

    sealed class Effect {
        object StoreUpdated : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
