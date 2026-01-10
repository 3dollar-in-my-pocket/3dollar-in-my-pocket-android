package com.zion830.threedollars.ui.dialog.category

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import base.compose.AppTheme
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.utils.AdvertisementsPosition
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.databinding.DialogBottomSelectCategoryBinding
import com.zion830.threedollars.ui.dialog.category.composable.CategoryListScreen
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomSelectCategoryBinding>() {

    private val viewModel: SelectCategoryViewModel by activityViewModels()

    private val homeViewModel: HomeViewModel by activityViewModels()
    override val screenName: ScreenName = ScreenName.CATEGORY_FILTER

    private val popupViewModel: PopupViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogBottomSelectCategoryBinding =
        DialogBottomSelectCategoryBinding.inflate(inflater, container, false)

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        initCategoryList()
        initAd()
        initFlow()
    }

    private fun initAd() {
        lifecycleScope.launch {
            homeViewModel.currentLocation.collect { latLng ->
                popupViewModel.getPopups(
                    position = AdvertisementsPosition.MENU_CATEGORY_BANNER,
                    latLng = latLng
                )
//                popupViewModel.getPopups(
//                    position = AdvertisementsPosition.MENU_CATEGORY_ICON,
//                    latLng = latLng
//                )
            }
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    homeViewModel.serverError.collect {
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
                            popup.background.color.let { if (it.isNotEmpty()) binding.cdAdCategory.setCardBackgroundColor(
                                Color.parseColor(it)) }

                            binding.ivAdImage.loadUrlImg(popup.image.url)

                            binding.cdAdCategory.onSingleClick {
                                homeViewModel.sendClickCategoryBannerAd(popup.advertisementId.toString())
                                if (popup.link.type == "APP_SCHEME") {
                                    startActivity(
                                        Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                                            putExtra("link", popup.link.url)
                                        },
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(popup.link.url)
                                        )
                                    )
                                }
                                dismiss()
                            }
                        }
                        binding.cdAdCategory.isVisible = popups.isNotEmpty()
                    }
                }

                // TODO - SelectCategoryViewModel 로 마이그레이션 : https://3dollarinmypocket.atlassian.net/browse/TH-888
//                launch {
//                    popupViewModel.categoryIconAd.collect { advertisementModelV2List ->
//                        advertisementModelV2List?.let {
//                            if (it.isNotEmpty()) {
//                                streetCategoryAdapter.submitList(streetCategoryAdapter.currentList + advertisementModelV2List.first())
//                            }
//                        }
//                    }
//                }
//                launch {
//                    popupViewModel.serverError.collect {
//                        it?.let {
//                            showToast(it)
//                        }
//                    }
//                }
            }
        }
    }

    private fun initCategoryList() {
        binding.categoryList.setContent {
            AppTheme {
                CategoryListScreen(
                    viewModel = viewModel,
                    homeViewModel = homeViewModel,
                    onSelected = {
                        homeViewModel.changeSelectCategory(it)
                dismiss()
                    }
                )
            }
        }
    }
}
