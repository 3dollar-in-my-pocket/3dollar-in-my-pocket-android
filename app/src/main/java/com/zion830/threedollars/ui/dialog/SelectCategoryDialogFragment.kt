package com.zion830.threedollars.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.databinding.DialogBottomSelectCategoryBinding
import com.zion830.threedollars.ui.home.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomSelectCategoryBinding>() {

    private val viewModel: HomeViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private val streetCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            EventTracker.logEvent(item.categoryId + Constants.CATEGORY_BTN_CLICKED_FORMAT)
            viewModel.changeSelectCategory(item)
            dismiss()
        }
    }

    private val bossCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            EventTracker.logEvent(item.categoryId + Constants.CATEGORY_BTN_CLICKED_FORMAT)
            viewModel.changeSelectCategory(item)
            dismiss()
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogBottomSelectCategoryBinding =
        DialogBottomSelectCategoryBinding.inflate(inflater, container, false)


    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent("SelectCategoryDialogFragment")
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        popupViewModel.getPopups("MENU_CATEGORY_BANNER")
        initAdapter()
        initFlow()
    }

    private fun initAdapter() {
        binding.streetCategoryRecyclerView.adapter = streetCategoryAdapter.apply {
            val categories = LegacySharedPrefUtils.getCategories()
            submitList(categories.map {
                if (viewModel.selectCategory.value.name == it.name) {
                    it.copy(isSelected = true)
                } else {
                    it
                }
            })
        }
        binding.bossCategoryRecyclerView.adapter = bossCategoryAdapter.apply {
            val categories = LegacySharedPrefUtils.getTruckCategories()
            submitList(categories.map {
                if (viewModel.selectCategory.value.name == it.name) {
                    it.copy(isSelected = true)
                } else {
                    it
                }
            })
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
                launch {
                    popupViewModel.popups.collect { popups ->
                        if (popups.isNotEmpty()) {
                            val popup = popups[0]
                            binding.tvAdTitle.text = popup.title

                            binding.tvAdBody.text = popup.subTitle

                            popup.fontColor?.let {
                                binding.tvAdTitle.setTextColor(it.toColorInt())
                                binding.tvAdBody.setTextColor(it.toColorInt())
                            }
                            popup.bgColor?.let { binding.cdAdCategory.setCardBackgroundColor(it.toColorInt()) }

                            binding.ivAdImage.loadUrlImg(popup.imageUrl)

                            binding.cdAdCategory.setOnClickListener {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popup.linkUrl)))
                                EventTracker.logEvent(Constants.CATEGORY_AD_BANNER_CLICKED)
                            }
                        }
                        binding.cdAdCategory.isVisible = popups.isNotEmpty()
                    }
                }
                launch {
                    popupViewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }
}