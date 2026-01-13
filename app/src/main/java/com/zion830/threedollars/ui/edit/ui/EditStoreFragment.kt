package com.zion830.threedollars.ui.edit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.edit.ui.compose.EditMenuScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreInfoScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract.EditScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.ui.write.ui.EditAddressFragment
import com.threedollar.common.ext.getMonthFirstDate
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.threedollar.common.ext.replaceFragment

@AndroidEntryPoint
class EditStoreFragment : Fragment() {

    private val editStoreViewModel: EditStoreViewModel by activityViewModels()
    private val storeDetailViewModel: StoreDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by editStoreViewModel.state.collectAsStateWithLifecycle()

                when (state.currentScreen) {
                    EditScreen.Selection -> {
                        EditStoreScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.StoreInfo -> {
                        EditStoreInfoScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.Location -> {
                        EditStoreScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.StoreMenu -> {
                        EditMenuScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeStoreData()
        observeEffects()
    }

    private fun initializeStoreData() {
        if (!editStoreViewModel.state.value.isInitialized) {
            storeDetailViewModel.userStoreDetailModel.value?.let { storeDetail ->
                val location = storeDetail.store.location
                val selectCategoryModelList = storeDetail.store.categories.map { model ->
                    val menu = storeDetail.store.menus.filter { userStoreMenuModel ->
                        userStoreMenuModel.category.categoryId == model.categoryId
                    }
                    com.threedollar.domain.home.data.store.SelectCategoryModel(menuType = model, menu)
                }

                editStoreViewModel.processIntent(
                    EditStoreContract.Intent.InitWithStoreData(
                        storeId = storeDetail.store.storeId,
                        storeName = storeDetail.store.name,
                        storeType = storeDetail.store.salesType?.name,
                        location = com.naver.maps.geometry.LatLng(location.latitude, location.longitude),
                        address = storeDetail.store.address?.fullAddress ?: "",
                        categories = selectCategoryModelList,
                        paymentMethods = storeDetail.store.paymentMethods.toSet(),
                        appearanceDays = storeDetail.store.appearanceDays.toSet(),
                        openingHours = com.threedollar.domain.home.request.OpeningHourRequest(
                            startTime = storeDetail.store.openingHoursModel.startTime,
                            endTime = storeDetail.store.openingHoursModel.endTime
                        )
                    )
                )
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStoreViewModel.effect.collect { effect ->
                    when (effect) {
                        is EditStoreContract.Effect.StoreUpdated -> {
                            setFragmentResult(STORE_EDITED_RESULT_KEY, bundleOf(STORE_UPDATED to true))
                            showToast(CommonR.string.edit_store_success)
                            storeDetailViewModel.getUserStoreDetail(
                                storeId = storeDetailViewModel.userStoreDetailModel.value?.store?.storeId ?: -1,
                                deviceLatitude = storeDetailViewModel.userStoreDetailModel.value?.store?.location?.latitude,
                                deviceLongitude = storeDetailViewModel.userStoreDetailModel.value?.store?.location?.longitude,
                                filterVisitStartDate = getMonthFirstDate()
                            )
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        is EditStoreContract.Effect.ShowError -> {
                            showToast(effect.message)
                        }
                        is EditStoreContract.Effect.ShowToast -> {
                            showToast(effect.message)
                        }
                        is EditStoreContract.Effect.NavigateToLocationEdit -> {
                            requireActivity().supportFragmentManager.replaceFragment(
                                R.id.container,
                                EditAddressFragment(),
                                EditAddressFragment::class.java.name,
                                false
                            )
                        }
                        is EditStoreContract.Effect.NavigateBack -> {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        is EditStoreContract.Effect.CloseScreen -> {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val STORE_EDITED_RESULT_KEY = "storeEditedResult"
        const val STORE_UPDATED = "storeUpdated"
    }
}
