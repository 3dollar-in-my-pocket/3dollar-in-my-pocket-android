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
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogBottomStreetSelectCategoryBinding
import com.zion830.threedollars.datasource.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.ui.category.adapter.StreetSelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.vm.StreetStoreByMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.BR
import zion830.com.common.base.loadUrlImg
import zion830.com.common.ext.toStringDefault
import zion830.com.common.listener.OnItemClickListener

@AndroidEntryPoint
class StreetSelectCategoryDialogFragment :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomStreetSelectCategoryBinding

    private val viewModel: StreetStoreByMenuViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


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
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_bottom_street_select_category,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        initView()
    }

    private fun initView() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        popupViewModel.getPopups("MENU_CATEGORY_BANNER")
        initViewModel()
        if (viewModel.categories.value?.isEmpty() == true) {
            viewModel.loadCategories()
        }

        val categoryAdapter =
            StreetSelectCategoryRecyclerAdapter(object : OnItemClickListener<CategoryInfo> {
                override fun onClick(item: CategoryInfo) {
                    EventTracker.logEvent(item.category + Constants.CATEGORY_BTN_CLICKED_FORMAT)
                    viewModel.changeCategory(menuType = item)
                    dismiss()
                }
            })

        binding.rvCategory.adapter = categoryAdapter
        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

        binding.tvTitle.text = buildSpannedString {
            append(getString(R.string.category_title1))
            bold {
                append(" ${getString(R.string.category_title2)} ")
            }
            append(getString(R.string.category_title3))
        }
    }

    @SuppressLint("Range")
    private fun initViewModel() {
        popupViewModel.popups.observe(viewLifecycleOwner) { popups ->
            if (popups.isNotEmpty()) {
                val popup = popups[0]
                binding.tvAdTitle.text = popup.title.toStringDefault()

                binding.tvAdBody.text = popup.subTitle.toStringDefault()

                popup.fontColor?.let {
                    binding.tvAdTitle.setTextColor(it.toColorInt())
                    binding.tvAdBody.setTextColor(it.toColorInt())
                }
                popup.bgColor?.let { binding.cdAdCategory.setCardBackgroundColor(it.toColorInt()) }

                binding.ivAdImage.loadUrlImg(popup.imageUrl)

                binding.cdAdCategory.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popup.linkUrl.toStringDefault())))
                    firebaseAnalytics.logEvent(Constants.CATEGORY_BANNER_CLICKED) {
                        param("referral", "category_page")
                    }
                }
            }
            binding.cdAdCategory.isVisible = popups.isNotEmpty()

        }
    }
}