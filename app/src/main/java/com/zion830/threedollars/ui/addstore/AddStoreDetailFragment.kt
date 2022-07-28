package com.zion830.threedollars.ui.addstore

import android.graphics.Rect
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentAddStoreBinding
import com.zion830.threedollars.repository.model.v2.request.MyMenu
import com.zion830.threedollars.repository.model.v2.request.NewStoreRequest
import com.zion830.threedollars.ui.addstore.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.view.CategoryBottomSheetDialog
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.showToast
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
            EventTracker.logEvent(Constants.EDIT_ADDRESS_BTN_CLICKED)
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
            EventTracker.logEvent(Constants.STORE_REGISTER_SUBMIT_BTN_CLICKED)
            saveStore()
        }
        viewModel.newStoreId.observe(this) {
            if (it >= 0) {
                requireActivity().finish()
                showToast(R.string.add_store_success)
            } else {
                showToast(R.string.failed_add_store)
            }
        }
        initKeyboard()
    }

    private fun saveStore() {
        if (editCategoryMenuRecyclerAdapter.items.isEmpty()) {
            showToast(R.string.no_category_msg)
            return
        }
        if (getMenuList().isEmpty()) {
            showToast(R.string.no_menu_msg)
            return
        }

        viewModel.addNewStore(
            NewStoreRequest(
                getAppearanceDays(),
                viewModel.selectedLocation.value?.latitude ?: NaverMapUtils.DEFAULT_LOCATION.latitude,
                viewModel.selectedLocation.value?.longitude ?: NaverMapUtils.DEFAULT_LOCATION.longitude,
                getMenuList().reversed(),
                getPaymentMethod(),
                viewModel.storeName.value ?: "이름 없음",
                storeType = getStoreType()
            )
        )
    }

    private fun getPaymentMethod(): List<String> {
        val result = arrayListOf<String>()
        if (binding.cbType1.isChecked) {
            result.add("CASH")
        }
        if (binding.cbType2.isChecked) {
            result.add("CARD")
        }
        if (binding.cbType3.isChecked) {
            result.add("ACCOUNT_TRANSFER")
        }
        return result
    }

    private fun getStoreType(): String? {
        val result = arrayListOf<String>()
        if (binding.rbType1.isChecked) {
            result.add("ROAD")
        }
        if (binding.rbType2.isChecked) {
            result.add("STORE")
        }
        if (binding.rbType3.isChecked) {
            result.add("CONVENIENCE_STORE")
        }
        return result.firstOrNull()
    }

    private fun getMenuList(): List<MyMenu> {
        val menuList = arrayListOf<MyMenu>()

        for (i in 0 until editCategoryMenuRecyclerAdapter.itemCount) {
            binding.rvMenu.getChildAt(i)?.let {
                val view = it.findViewById<RecyclerView>(R.id.rv_menu_edit)
                val editMenuRecyclerView = (view as RecyclerView)
                val menuSize = (editMenuRecyclerView.adapter as? EditMenuRecyclerAdapter)?.itemCount ?: 0
                val category = if (editCategoryMenuRecyclerAdapter.items.isNotEmpty()) {
                    editCategoryMenuRecyclerAdapter.items[i].menuType.category
                } else {
                    ""
                }

                var isEmptyCategory = true
                repeat(menuSize) { index ->
                    val menuRow = editMenuRecyclerView.getChildAt(index)
                    val name = (menuRow.findViewById(R.id.et_name) as EditText).text.toString()
                    val price = (menuRow.findViewById(R.id.et_price) as EditText).text.toString()

                    if (name.isNotEmpty() || price.isNotEmpty()) {
                        menuList.add(MyMenu(category, name, price))
                        isEmptyCategory = false
                    }
                }

                if (isEmptyCategory) {
                    menuList.add(MyMenu(category, "", ""))
                }
            }
        }

        return menuList
    }

    private fun getAppearanceDays(): List<String> {
        val result = arrayListOf<String>()
        val const = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        if (binding.layoutBtnDayOfWeek.tbMon.isChecked) {
            result.add(const[0])
        }
        if (binding.layoutBtnDayOfWeek.tbTue.isChecked) {
            result.add(const[1])
        }
        if (binding.layoutBtnDayOfWeek.tbWen.isChecked) {
            result.add(const[2])
        }
        if (binding.layoutBtnDayOfWeek.tbThur.isChecked) {
            result.add(const[3])
        }
        if (binding.layoutBtnDayOfWeek.tbFri.isChecked) {
            result.add(const[4])
        }
        if (binding.layoutBtnDayOfWeek.tbSat.isChecked) {
            result.add(const[5])
        }
        if (binding.layoutBtnDayOfWeek.tbSun.isChecked) {
            result.add(const[6])
        }
        return result
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