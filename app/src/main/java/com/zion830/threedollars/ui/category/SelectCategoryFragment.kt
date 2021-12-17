package com.zion830.threedollars.ui.category

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentSelectCategoryBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class SelectCategoryFragment : BaseFragment<FragmentSelectCategoryBinding, CategoryViewModel>(R.layout.fragment_select_category) {

    override val viewModel: CategoryViewModel by activityViewModels()

    override fun initView() {
        if (viewModel.categories.value?.isEmpty() == true) {
            viewModel.loadCategories()
        }

        val categoryAdapter = SelectCategoryRecyclerAdapter(object : OnItemClickListener<CategoryInfo> {
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
}