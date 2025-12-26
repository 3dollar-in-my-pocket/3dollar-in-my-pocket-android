package com.zion830.threedollars.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_CATEGORY
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.DialogBottomSelectCategoryBinding
import com.zion830.threedollars.ui.home.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomSelectCategoryBinding>() {

    private val viewModel: HomeViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private val streetCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter(
            onCategoryClickListener = { item ->
                val bundle = Bundle().apply {
                    putString("screen", "category_filter")
                    putString(
                        "category_id",
                        if (viewModel.uiState.value.selectedCategory?.categoryId == item.categoryId) null
                        else item.categoryId
                    )
                }
                var selectedCategory: CategoryModel? = if (viewModel.uiState.value.selectedCategory?.categoryId == item.categoryId) {
                    null
                } else {
                    item
                }

                EventTracker.logEvent(CLICK_CATEGORY, bundle)
                viewModel.changeSelectCategory(selectedCategory)
                dismiss()
            },
            onAdClickListener = { item ->
                if (item.link.type == "APP_SCHEME") {
                    startActivity(
                        Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                            putExtra("link", item.link.url)
                        },
                    )
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link.url)))
                }
                dismiss()
            }
        )
    }

    private val bossCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter(
            onCategoryClickListener = { item ->
                val bundle = Bundle().apply {
                    putString("screen", "category_filter")
                    putString(
                        "category_id", if (viewModel.uiState.value.selectedCategory?.categoryId == item.categoryId) null
                        else item.categoryId
                    )
                }

                EventTracker.logEvent(CLICK_CATEGORY, bundle)
                viewModel.changeSelectCategory(item)
                dismiss()
            },
            onAdClickListener = {}
        )
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogBottomSelectCategoryBinding =
        DialogBottomSelectCategoryBinding.inflate(inflater, container, false)


    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SelectCategoryDialogFragment", screenName = "category_filter")
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        initAd()
        initAdapter()
        initFlow()
    }

    private fun initAd() {
        viewModel.currentLocation.value?.let { latLng ->
            popupViewModel.getPopups(
                position = AdvertisementsPosition.MENU_CATEGORY_BANNER,
                latLng = latLng
            )
            popupViewModel.getPopups(
                position = AdvertisementsPosition.MENU_CATEGORY_ICON,
                latLng = latLng
            )
        }
    }

    private fun initAdapter() {
        binding.streetCategoryRecyclerView.adapter = streetCategoryAdapter.apply {
            val categories = LegacySharedPrefUtils.getCategories().map {
                if (viewModel.uiState.value.selectedCategory?.name == it.name) {
                    it.copy(isSelected = true)
                } else {
                    it
                }
            }
            submitList(categories)
        }
        binding.bossCategoryRecyclerView.adapter = bossCategoryAdapter.apply {
            val categories = LegacySharedPrefUtils.getTruckCategories().map {
                if (viewModel.uiState.value.selectedCategory?.name == it.name) {
                    it.copy(isSelected = true)
                } else {
                    it
                }
            }
            submitList(categories)
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
                            binding.tvAdTitle.text = popup.title.content

                            binding.tvAdBody.text = popup.subTitle.content

                            popup.title.fontColor.let {
                                if (it.isNotEmpty()) {
                                    binding.tvAdTitle.setTextColor(Color.parseColor(it))
                                    binding.tvAdBody.setTextColor(Color.parseColor(it))
                                }
                            }
                            popup.background.color.let { if (it.isNotEmpty()) binding.cdAdCategory.setCardBackgroundColor(Color.parseColor(it)) }

                            binding.ivAdImage.loadUrlImg(popup.image.url)

                            binding.cdAdCategory.onSingleClick {
                                val bundle = Bundle().apply {
                                    putString("screen", "category_filter")
                                    putString("advertisement_id", popup.advertisementId.toString())
                                }
                                EventTracker.logEvent(Constants.CLICK_AD_BANNER, bundle)
                                if (popup.link.type == "APP_SCHEME") {
                                    startActivity(
                                        Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                                            putExtra("link", popup.link.url)
                                        },
                                    )
                                    dismiss()
                                } else {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popup.link.url)))
                                }
                                dismiss()
                            }
                        }
                        binding.cdAdCategory.isVisible = popups.isNotEmpty()
                    }
                }

                launch {
                    popupViewModel.categoryIconAd.collect { advertisementModelV2List ->
                        advertisementModelV2List?.let {
                            if (it.isNotEmpty()) {
                                streetCategoryAdapter.submitList(streetCategoryAdapter.currentList + advertisementModelV2List.first())
                            }
                        }
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