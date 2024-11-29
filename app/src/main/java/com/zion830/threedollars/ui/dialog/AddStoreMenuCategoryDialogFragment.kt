package com.zion830.threedollars.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.DialogBottomAddStoreMenuCategoryBinding
import com.zion830.threedollars.ui.home.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty

@AndroidEntryPoint
class AddStoreMenuCategoryDialogFragment : BaseBottomSheetDialogFragment<DialogBottomAddStoreMenuCategoryBinding>() {

    private val viewModel: AddStoreViewModel by activityViewModels()

    private val streetCategories by lazy { LegacySharedPrefUtils.getCategories() }

    private val truckCategories by lazy { LegacySharedPrefUtils.getTruckCategories() }

    private val streetCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter(
            onCategoryClickListener = { item ->
                viewModel.changeSelectCategory(item)
                initStreetAdapterSubmit()
            },
            onAdClickListener = {}
        )
    }

    private val bossCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter(
            onCategoryClickListener = { item ->
                viewModel.changeSelectCategory(item)
                initTruckAdapterSubmit()
            },
            onAdClickListener = {}
        )
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogBottomAddStoreMenuCategoryBinding =
        DialogBottomAddStoreMenuCategoryBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "AddStoreMenuCategoryDialogFragment", screenName = "category_selection")
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        initButton()
        initFlow()
        initAdapter()
    }

    private fun initAdapter() {
        binding.streetCategoryRecyclerView.adapter = streetCategoryAdapter.apply {
            initStreetAdapterSubmit()
        }
        binding.bossCategoryRecyclerView.adapter = bossCategoryAdapter.apply {
            initTruckAdapterSubmit()
        }
    }

    private fun initStreetAdapterSubmit() {
        val list = viewModel.selectCategoryList.value
        streetCategoryAdapter.submitList(
            streetCategories.map { item ->
                val sameItem = list.find { it.menuType.name == item.name }
                if (sameItem == null) {
                    item.copy(isSelected = false)
                } else {
                    item.copy(isSelected = true)
                }
            },
        )
    }

    private fun initTruckAdapterSubmit() {
        val list = viewModel.selectCategoryList.value
        bossCategoryAdapter.submitList(
            truckCategories.map { item ->
                val sameItem = list.find { it.menuType.name == item.name }
                if (sameItem == null) {
                    item.copy(isSelected = false)
                } else {
                    item.copy(isSelected = true)
                }
            },
        )
    }

    private fun initButton() {
        binding.finishButton.onSingleClick {
            if (viewModel.selectCategoryList.value.isNotNullOrEmpty()) {
                val bundle = Bundle().apply {
                    putString("screen", "category_selection")
                    putString("category_name", viewModel.selectCategoryList.value.joinToString { it.menuType.name })
                }
                EventTracker.logEvent(Constants.CLICK_CATEGORY, bundle)
            }
            dismiss()
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }
}
