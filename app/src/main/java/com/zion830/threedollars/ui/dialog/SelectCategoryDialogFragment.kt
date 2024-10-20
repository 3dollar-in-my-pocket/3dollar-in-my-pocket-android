package com.zion830.threedollars.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_CATEGORY
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

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomSelectCategoryBinding>() {

    private val viewModel: HomeViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private val streetCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            val bundle = Bundle().apply {
                putString("screen", "category_filter")
                putString("category_id", if (viewModel.selectCategory.value.categoryId == item.categoryId) null else item.categoryId)
            }

            EventTracker.logEvent(CLICK_CATEGORY, bundle)
            viewModel.changeSelectCategory(item)
            dismiss()
        }
    }

    private val bossCategoryAdapter by lazy {
        SelectCategoryRecyclerAdapter { item ->
            val bundle = Bundle().apply {
                putString("screen", "category_filter")
                putString("category_id", if (viewModel.selectCategory.value.categoryId == item.categoryId) null else item.categoryId)
            }

            EventTracker.logEvent(CLICK_CATEGORY, bundle)
            viewModel.changeSelectCategory(item)
            dismiss()
        }
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
        Log.e("SelectCategoryDialogFragment", viewModel.currentLocation.value.toString())
        viewModel.currentLocation.value?.let { latLng ->
            popupViewModel.getPopups(
                position = AdvertisementsPosition.MENU_CATEGORY_BANNER,
                latLng = latLng
            )
        }

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

                            binding.cdAdCategory.setOnClickListener {
                                val bundle = Bundle().apply {
                                    putString("screen", "category_filter")
                                    putString("advertisement_id", popup.advertisementId.toString())
                                }
                                EventTracker.logEvent(Constants.CLICK_AD_BANNER, bundle)
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popup.link.url)))
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