package com.zion830.threedollars.ui.category

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentCategoryBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.ui.category.adapter.SelectCategoryRecyclerAdapter
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class SelectCategoryFragment : BaseFragment<FragmentCategoryBinding, StoreDetailViewModel>(R.layout.fragment_category) {
    override val viewModel: StoreDetailViewModel by viewModels()

    override fun initView() {
        val categoryAdapter = SelectCategoryRecyclerAdapter(object : OnItemClickListener<MenuType> {
            override fun onClick(item: MenuType) {
                startActivity(StoreByMenuActivity.getIntent(requireContext(), item.key))
            }
        })

        binding.rvCategory.adapter = categoryAdapter
        categoryAdapter.submitList(MenuType.values().toList())
        binding.tvTitle.text = buildSpannedString {
            append(getString(R.string.category_title1))
            bold {
                append(" ${getString(R.string.category_title2)} ")
            }
            append(getString(R.string.category_title3))
        }
    }
}