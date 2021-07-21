package com.zion830.threedollars.ui.addstore

import android.graphics.Rect
import android.os.Handler
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentAddStoreBinding
import com.zion830.threedollars.ui.addstore.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.view.CategoryBottomSheetDialog
import com.zion830.threedollars.utils.getCurrentLocationName
import zion830.com.common.base.BaseFragment

class AddStoreDetailFragment : BaseFragment<FragmentAddStoreBinding, AddStoreViewModel>(R.layout.fragment_add_store) {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private var isFirstOpen = true

    private lateinit var addCategoryRecyclerAdapter: AddCategoryRecyclerAdapter

    private lateinit var editCategoryMenuRecyclerAdapter: EditCategoryMenuRecyclerAdapter

    override fun initView() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnEditAddress.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            binding.tvAddress.text = getCurrentLocationName(it)
        }
        viewModel.selectedCategory.observe(viewLifecycleOwner) {
            addCategoryRecyclerAdapter.submitList(it.filter { category -> category.isSelected })
            editCategoryMenuRecyclerAdapter.setItems(it.filter { category -> category.isSelected })
        }

        addCategoryRecyclerAdapter = AddCategoryRecyclerAdapter({
            CategoryBottomSheetDialog().show(
                parentFragmentManager,
                CategoryBottomSheetDialog::class.java.name
            )
        }, {
            viewModel.removeCategory(it)
        }
        )
        editCategoryMenuRecyclerAdapter = EditCategoryMenuRecyclerAdapter {
            viewModel.removeCategory(it)
        }

        binding.btnClearCategory.setOnClickListener {
            editCategoryMenuRecyclerAdapter.clear()
            addCategoryRecyclerAdapter.clear()
            viewModel.removeAllCategory()
        }
        binding.rvCategory.adapter = addCategoryRecyclerAdapter
        binding.rvCategory.itemAnimator = null
        binding.rvMenu.adapter = editCategoryMenuRecyclerAdapter
        binding.rvMenu.itemAnimator = null
        binding.btnSubmit.setOnClickListener {
            // viewModel.addNewStore()
        }

        initKeyboard()
    }


    private fun initKeyboard() {
        var keypadBaseHeight = 0

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect(); // 키보드 위로 보여지는 공간
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height

            val keypadHeight = screenHeight - r.bottom

            if (keypadBaseHeight == 0) {
                keypadBaseHeight = keypadHeight
            }

            if (keypadHeight > screenHeight * 0.15) {
                binding.btnSubmit.visibility = View.GONE
                binding.viewSubmitBack.visibility = View.GONE
            } else {
                Handler().postDelayed({
                    if (!isFirstOpen) {
                        binding.btnSubmit.visibility = View.VISIBLE
                        binding.viewSubmitBack.visibility = View.VISIBLE
                    }
                }, 50)
            }
        }

        isFirstOpen = false
    }
}