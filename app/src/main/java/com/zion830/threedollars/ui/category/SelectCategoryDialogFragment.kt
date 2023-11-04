package com.zion830.threedollars.ui.category

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogBottomSelectCategoryBinding
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.home.HomeViewModel
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.BR
import zion830.com.common.base.loadUrlImg

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomSelectCategoryBinding

    private val viewModel: HomeViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_bottom_select_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        initView()
        initFlow()
    }

    private fun initView() {

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        popupViewModel.getPopups("MENU_CATEGORY_BANNER")
        initViewModel()

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

    @SuppressLint("Range")
    private fun initViewModel() {
        popupViewModel.popups.observe(viewLifecycleOwner) { popups ->
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