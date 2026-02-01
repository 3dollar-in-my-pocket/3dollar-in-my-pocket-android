package com.zion830.threedollars.ui.dialog.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import base.compose.AppTheme
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.const.ArgumentKey
import com.zion830.threedollars.databinding.DialogBottomSelectCategoryBinding
import com.zion830.threedollars.ui.dialog.category.composable.CategoryListScreen
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomSelectCategoryBinding>() {

    private val viewModel: SelectCategoryViewModel by viewModels()

    private val homeViewModel: HomeViewModel by activityViewModels()
    override val screenName: ScreenName = ScreenName.CATEGORY_FILTER

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
                    },
                    onDismiss = {
                        dismiss()
                    }
                )
            }
        }
    }

    companion object {
        const val TAG = "SelectCategoryDialogFragment"

        fun newInstance(
            latLng: LatLng
        ): SelectCategoryDialogFragment = SelectCategoryDialogFragment().apply {
            arguments = bundleOf(
                ArgumentKey.LOCATION to latLng
            )
        }
    }
}
