package com.zion830.threedollars.ui.addstore.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogSelectCategoryBinding
import com.zion830.threedollars.ui.addstore.adapter.CategoryDialogRecyclerAdapter
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.bindItems

// 가게 수정 화면에서 띄울 다이얼로그
@AndroidEntryPoint
class CategoryEditBottomSheetDialog : BottomSheetDialogFragment() {

    // TODO : CategoryBottomSheet 두개 통일해야함
    private val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var adapter: CategoryDialogRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogSelectCategoryBinding.inflate(inflater)

        adapter = CategoryDialogRecyclerAdapter {
            binding.btnSubmit.text = getString(R.string.select).format(adapter.getSelectedCount())
            binding.btnSubmit.isEnabled = it > 0
        }

        binding.lifecycleOwner = this
        binding.rvCategory.adapter = adapter
        binding.btnSubmit.text = getString(R.string.select).format(0)
//        binding.btnSubmit.setOnClickListener {
//            viewModel.updateCategory(adapter.items)
//            dismiss()
//        }
//        viewModel.selectedCategory.observe(viewLifecycleOwner) {
//            adapter.setItems(viewModel.selectedCategory.value ?: listOf())
//            val count = viewModel.selectedCategory.value?.count { it.isSelected } ?: 0
//            binding.btnSubmit.text = getString(R.string.select).format(count)
//            binding.btnSubmit.isEnabled = count > 0
//            binding.rvCategory.bindItems(it)
//        }
        return binding.root
    }
}