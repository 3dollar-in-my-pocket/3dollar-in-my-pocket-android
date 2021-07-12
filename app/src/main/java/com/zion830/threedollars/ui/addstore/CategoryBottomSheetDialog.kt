package com.zion830.threedollars.ui.addstore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.databinding.DialogSelectCategoryBinding
import com.zion830.threedollars.ui.addstore.adapter.CategoryDialogRecyclerAdapter

class CategoryBottomSheetDialog : BottomSheetDialogFragment() {

    private val viewModel: AddStoreViewModel by activityViewModels()

    private val adapter = CategoryDialogRecyclerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogSelectCategoryBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvCategory.adapter = adapter

        return binding.root
    }

    fun getSelectedCategory() {

    }
}