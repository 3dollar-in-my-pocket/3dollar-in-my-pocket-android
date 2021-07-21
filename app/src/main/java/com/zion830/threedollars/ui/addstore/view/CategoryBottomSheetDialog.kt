package com.zion830.threedollars.ui.addstore.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogSelectCategoryBinding
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.addstore.adapter.CategoryDialogRecyclerAdapter

class CategoryBottomSheetDialog : BottomSheetDialogFragment() {

    private val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var adapter: CategoryDialogRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogSelectCategoryBinding.inflate(inflater)

        adapter = CategoryDialogRecyclerAdapter {
            binding.btnSubmit.text = getString(R.string.select).format(adapter.getSelectedCount())
            binding.btnSubmit.isEnabled = it > 0
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvCategory.adapter = adapter
        binding.btnSubmit.text = getString(R.string.select).format(0)
        binding.btnSubmit.setOnClickListener {
            viewModel.updateCategory(adapter.items)
            dismiss()
        }
        viewModel.selectedCategory.observe(viewLifecycleOwner) {
            adapter.setItems(viewModel.selectedCategory.value ?: listOf())
            val count = viewModel.selectedCategory.value?.count { it.isSelected } ?: 0
            binding.btnSubmit.text = getString(R.string.select).format(count)
            binding.btnSubmit.isEnabled = count > 0
        }
        return binding.root
    }
}