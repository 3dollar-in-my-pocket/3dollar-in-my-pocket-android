package com.zion830.threedollars.ui.addstore

import android.graphics.Rect
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.customview.NaverMapFragment
import com.zion830.threedollars.databinding.FragmentEditDetailBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.ui.addstore.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import com.zion830.threedollars.ui.addstore.view.CategoryBottomSheetDialog
import com.zion830.threedollars.ui.addstore.view.EditCategoryBottomSheetDialog
import com.zion830.threedollars.ui.store_detail.adapter.CategoryInfoRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.findStoreType
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.replaceFragment

class EditStoreDetailFragment :
    BaseFragment<FragmentEditDetailBinding, StoreDetailViewModel>(R.layout.fragment_edit_detail) {

    override val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var addCategoryRecyclerAdapter: AddCategoryRecyclerAdapter // 카테고리
    private lateinit var editCategoryMenuRecyclerAdapter: EditCategoryMenuRecyclerAdapter // 상세 메뉴

    private var isFirstOpen = true

    private lateinit var naverMapFragment: NaverMapFragment

    override fun initView() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        initMap()

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

        viewModel.storeInfo.observe(viewLifecycleOwner) {
            // 가게 정보 초기화
            binding.tvAddress.text =
                getCurrentLocationName(LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0))
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

            it?.appearanceDays?.forEach { day ->
                if (day == "MONDAY") {
                    binding.layoutBtnDayOfWeek.tbMon.performClick()
                }
                if (day == "TUESDAY") {
                    binding.layoutBtnDayOfWeek.tbTue.performClick()
                }
                if (day == "WEDNESDAY") {
                    binding.layoutBtnDayOfWeek.tbWen.performClick()
                }
                if (day == "THURSDAY") {
                    binding.layoutBtnDayOfWeek.tbThur.performClick()
                }
                if (day == "FRIDAY") {
                    binding.layoutBtnDayOfWeek.tbFri.performClick()
                }
                if (day == "SATURDAY") {
                    binding.layoutBtnDayOfWeek.tbSat.performClick()
                }
                if (day == "SUNDAY") {
                    binding.layoutBtnDayOfWeek.tbSun.performClick()
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
                binding.tvAddress.text = getCurrentLocationName(latlng)

            }
            binding.btnClearCategory.setOnClickListener {
                editCategoryMenuRecyclerAdapter.clear()
                addCategoryRecyclerAdapter.clear()
                viewModel.removeAllCategory()
            }
        }
    }

    private fun initMap() {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.requestStoreInfo(
                            viewModel.storeInfo.value?.id ?: 0,
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