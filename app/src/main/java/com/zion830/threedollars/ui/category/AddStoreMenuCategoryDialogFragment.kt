package com.zion830.threedollars.ui.category

import android.app.Dialog
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.DialogBottomAddStoreMenuCategoryBinding
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddStoreMenuCategoryDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomAddStoreMenuCategoryBinding

    private val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val streetCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            EventTracker.logEvent(item.category + Constants.CATEGORY_BTN_CLICKED_FORMAT)
            viewModel.changeSelectCategory(item)
            initStreetAdapterSubmit()

        }
    }

    private val bossCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            EventTracker.logEvent(item.category + Constants.CATEGORY_BTN_CLICKED_FORMAT)
            viewModel.changeSelectCategory(item)
            initTruckAdapterSubmit()

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogBottomAddStoreMenuCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initButton()
        initFlow()
    }

    private fun initView() {

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding.streetCategoryRecyclerView.adapter = streetCategoryAdapter.apply {
            initStreetAdapterSubmit()
        }
        binding.bossCategoryRecyclerView.adapter = bossCategoryAdapter.apply {
            initTruckAdapterSubmit()
        }
    }

    private fun initStreetAdapterSubmit() {
        val categories = LegacySharedPrefUtils.getCategories()
        streetCategoryAdapter.submitList(categories.map {
            if (viewModel.selectCategoryList.value.contains(it)) {
                it.copy(isSelected = true)
            } else {
                it
            }
        })
    }

    private fun initTruckAdapterSubmit() {
        val categories = LegacySharedPrefUtils.getTruckCategories()
        bossCategoryAdapter.submitList(categories.map {
            if (viewModel.selectCategoryList.value.contains(it)) {
                it.copy(isSelected = true)
            } else {
                it
            }
        })
    }

    private fun initButton() {
        binding.finishButton.setOnClickListener {
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