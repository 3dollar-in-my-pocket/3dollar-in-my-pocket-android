package com.zion830.threedollars.ui.category

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.toColorInt
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentSelectCategoryBinding
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener

class SelectCategoryFragment :
    BaseFragment<FragmentSelectCategoryBinding, CategoryViewModel>(R.layout.fragment_select_category) {

    override val viewModel: CategoryViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun initView() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        popupViewModel.getPopups("MENU_CATEGORY_BANNER")
        initViewModel()
        if (viewModel.categories.value?.isEmpty() == true) {
            viewModel.loadCategories()
        }

        val categoryAdapter =
            SelectCategoryRecyclerAdapter(object : OnItemClickListener<CategoryInfo> {
                override fun onClick(item: CategoryInfo) {
                    EventTracker.logEvent(item.category + Constants.CATEGORY_BTN_CLICKED_FORMAT)
                    startActivity(StoreByMenuActivity.getIntent(requireContext(), item))
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
        popupViewModel.popups.observe(viewLifecycleOwner, { popups ->
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
                    firebaseAnalytics.logEvent(Constants.CATEGORY_BANNER_CLICKED) {
                        param("referral", "category_page")
                    }
                }
            }
            binding.cdAdCategory.isVisible = popups.isNotEmpty()

        })
    }
}