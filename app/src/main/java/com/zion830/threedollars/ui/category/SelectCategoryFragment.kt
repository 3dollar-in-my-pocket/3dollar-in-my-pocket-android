package com.zion830.threedollars.ui.category

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentSelectCategoryBinding
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class SelectCategoryFragment :
    BaseFragment<FragmentSelectCategoryBinding, CategoryViewModel>(R.layout.fragment_select_category) {

    override val viewModel: CategoryViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    override fun initView() {
        popupViewModel.getPopups("MENU_CATEGORY_BANNER")
        initViewModel()
        if (viewModel.categories.value?.isEmpty() == true) {
            viewModel.loadCategories()
        }

        val categoryAdapter =
            SelectCategoryRecyclerAdapter(object : OnItemClickListener<CategoryInfo> {
                override fun onClick(item: CategoryInfo) {
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
                binding.tvAdTitle.setTextColor(Color.parseColor(popup.fontColor))

                binding.tvAdBody.text = popup.subTitle
                binding.tvAdBody.setTextColor(Color.parseColor(popup.fontColor))

                binding.cdAdCategory.setCardBackgroundColor(Color.parseColor(popup.bgColor))

                Glide.with(requireContext())
                    .load(popup.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivAdImage)

                binding.cdAdCategory.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popup.linkUrl)))
                }
            }
            binding.cdAdCategory.isVisible = popups.isNotEmpty()

        })
    }
}