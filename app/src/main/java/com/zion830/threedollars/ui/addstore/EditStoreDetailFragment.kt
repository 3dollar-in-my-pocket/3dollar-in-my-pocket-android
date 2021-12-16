package com.zion830.threedollars.ui.addstore

import android.graphics.Rect
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.databinding.FragmentEditDetailBinding
import com.zion830.threedollars.repository.model.v2.request.MyMenu
import com.zion830.threedollars.repository.model.v2.request.NewStoreRequest
import com.zion830.threedollars.ui.addstore.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.view.CategoryBottomSheetDialog
import com.zion830.threedollars.ui.addstore.view.EditCategoryBottomSheetDialog
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.store_detail.findStoreType
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.replaceFragment

class EditStoreDetailFragment :
    BaseFragment<FragmentEditDetailBinding, StoreDetailViewModel>(R.layout.fragment_edit_detail) {

    override val viewModel: StoreDetailViewModel by activityViewModels()

    val editStoreViewModel: EditStoreViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var addCategoryRecyclerAdapter: AddCategoryRecyclerAdapter // 카테고리
    private lateinit var editCategoryMenuRecyclerAdapter: EditCategoryMenuRecyclerAdapter // 상세 메뉴

    private var isFirstOpen = true

    private lateinit var naverMapFragment: NaverMapFragment

    private var storeTarget: LatLng = NaverMapUtils.DEFAULT_LOCATION

    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        refreshStoreInfo()

        initKeyboard()

        binding.ibWrite.setOnClickListener {
            requireActivity().supportFragmentManager.replaceFragment(
                R.id.container,
                EditAddressFragment(),
                EditAddressFragment::class.java.name,
                false
            )
        }
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        naverMapFragment = StoreDetailNaverMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()

        addCategoryRecyclerAdapter = AddCategoryRecyclerAdapter({
            EditCategoryBottomSheetDialog().show(
                parentFragmentManager,
                CategoryBottomSheetDialog::class.java.name
            )
        }, {
            viewModel.removeCategory(it)
        })
        editCategoryMenuRecyclerAdapter = EditCategoryMenuRecyclerAdapter {
            viewModel.removeCategory(it)
        }
        binding.rvCategory.adapter = addCategoryRecyclerAdapter
        binding.rvMenu.adapter = editCategoryMenuRecyclerAdapter

        editStoreViewModel.editStoreResult.observe(viewLifecycleOwner) {
            if (it) {
                refreshStoreInfo()
                requireActivity().onBackPressed()
            } else {
                showToast(R.string.failed_edit_store)
            }
        }
        viewModel.storeInfo.observe(viewLifecycleOwner) {
            storeTarget = LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0)
            binding.tvAddress.text = getCurrentLocationName(storeTarget)
            binding.etName.setText(it?.storeName)

            if (!it?.storeType.isNullOrBlank()) {
                val index = findStoreType(it?.storeType)
                listOf(binding.rbType1, binding.rbType2, binding.rbType3)[index].isChecked = true
            }

            it?.paymentMethods?.forEach { method ->
                if (method == "CASH") {
                    binding.cbType1.isChecked = true
                }
                if (method == "CARD") {
                    binding.cbType2.isChecked = true
                }
                if (method == "ACCOUNT_TRANSFER") {
                    binding.cbType3.isChecked = true
                }
            }

            when (it?.storeType) {
                "ROAD" -> binding.rbType1.isChecked = true
                "STORE" -> binding.rbType2.isChecked = true
                "CONVENIENCE_STORE" -> binding.rbType3.isChecked = true
            }

            it?.appearanceDays?.forEach { day ->
                if (day == "MONDAY") {
                    binding.layoutBtnDayOfWeek.tbMon.isChecked = true
                }
                if (day == "TUESDAY") {
                    binding.layoutBtnDayOfWeek.tbTue.isChecked = true
                }
                if (day == "WEDNESDAY") {
                    binding.layoutBtnDayOfWeek.tbWen.isChecked = true
                }
                if (day == "THURSDAY") {
                    binding.layoutBtnDayOfWeek.tbThur.isChecked = true
                }
                if (day == "FRIDAY") {
                    binding.layoutBtnDayOfWeek.tbFri.isChecked = true
                }
                if (day == "SATURDAY") {
                    binding.layoutBtnDayOfWeek.tbSat.isChecked = true
                }
                if (day == "SUNDAY") {
                    binding.layoutBtnDayOfWeek.tbSun.isChecked = true
                }
            }

            viewModel.categoryInfo.observe(viewLifecycleOwner) {
                viewModel.initSelectedCategory()
            }
            viewModel.selectedCategory.observe(viewLifecycleOwner) { allCategoryInfo ->
                addCategoryRecyclerAdapter.submitList(allCategoryInfo.filter { menu -> menu.isSelected })
                editCategoryMenuRecyclerAdapter.setItems(allCategoryInfo.filter { category -> category.isSelected })
            }
            viewModel.selectedLocation.observe(viewLifecycleOwner) { latlng ->
                if (latlng != null) {
                    storeTarget = latlng
                    binding.tvAddress.text = getCurrentLocationName(latlng)
                    naverMapFragment.moveCamera(latlng)
                    naverMapFragment.addMarker(R.drawable.ic_marker, latlng)
                }
            }
            binding.btnClearCategory.setOnClickListener {
                editCategoryMenuRecyclerAdapter.clear()
                addCategoryRecyclerAdapter.clear()
                viewModel.removeAllCategory()
            }
            binding.btnSubmit.setOnClickListener {
                if (binding.etName.text.isNullOrBlank()) {
                    showToast(R.string.store_name_empty)
                    return@setOnClickListener
                }

                editStoreViewModel.editStore(
                    viewModel.storeInfo.value?.storeId ?: 0,
                    NewStoreRequest(
                        getAppearanceDays(),
                        storeTarget.latitude,
                        storeTarget.longitude,
                        getMenuList(),
                        getPaymentMethod(),
                        binding.etName.text.toString(),
                        getStoreType()
                    )
                )
            }
        }
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

    private fun refreshStoreInfo() {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.requestStoreInfo(
                            viewModel.storeInfo.value?.storeId ?: 0,
                            it.latitude,
                            it.longitude
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
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