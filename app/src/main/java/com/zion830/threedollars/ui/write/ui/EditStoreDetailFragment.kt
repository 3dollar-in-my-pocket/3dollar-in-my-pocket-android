package com.zion830.threedollars.ui.write.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.AddCategoryModel
import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.PaymentType
import com.home.domain.data.store.SalesType
import com.home.domain.data.store.SelectCategoryModel
import com.home.domain.request.MenuModelRequest
import com.home.domain.request.OpeningHourRequest
import com.home.domain.request.UserStoreModelRequest
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.getMonthFirstDate
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.replaceFragment
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentEditDetailBinding
import com.zion830.threedollars.ui.dialog.AddStoreMenuCategoryDialogFragment
import com.zion830.threedollars.ui.dialog.OnClickDoneListener
import com.zion830.threedollars.ui.dialog.OpeningHourNumberPickerDialog
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.map.ui.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.ui.write.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.write.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.write.adapter.EditMenuRecyclerAdapter
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditStoreDetailFragment : BaseFragment<FragmentEditDetailBinding, StoreDetailViewModel>() {

    override val viewModel: StoreDetailViewModel by activityViewModels()
    private val addStoreViewModel: AddStoreViewModel by activityViewModels()

    private val addCategoryRecyclerAdapter: AddCategoryRecyclerAdapter by lazy {
        AddCategoryRecyclerAdapter(
            {
                AddStoreMenuCategoryDialogFragment().show(
                    parentFragmentManager,
                    AddStoreMenuCategoryDialogFragment::class.java.name,
                )
            },
            {
                addStoreViewModel.removeCategory(it)
            },
        )
    }

    private val editCategoryMenuRecyclerAdapter: EditCategoryMenuRecyclerAdapter by lazy {
        EditCategoryMenuRecyclerAdapter { addStoreViewModel.removeCategory(it) }
    }

    private val naverMapFragment: StoreDetailNaverMapFragment = StoreDetailNaverMapFragment()

    private var startTime: String? = null
    private var endTime: String? = null
    override fun initView() {
        initMap()
        viewModel.getUserStoreDetail(
            storeId = viewModel.userStoreDetailModel.value?.store?.storeId ?: -1,
            deviceLatitude = viewModel.userStoreDetailModel.value?.store?.location?.latitude,
            deviceLongitude = viewModel.userStoreDetailModel.value?.store?.location?.longitude,
            filterVisitStartDate = getMonthFirstDate(),
        )
        initButton()
        initAdapter()
        initFlow()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "EditStoreDetailFragment", screenName = "write_address_detail")
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    addStoreViewModel.selectedLocation.collect {
                        binding.addressTextView.text = getCurrentLocationName(it)
                        it?.let {
                            delay(500L)
                            val latLng = LatLng(it.latitude, it.longitude)
                            naverMapFragment.initMap(latLng, false)
                        }
                    }
                }
                launch {
                    addStoreViewModel.postUserStoreModel.collect {
                        it?.let {
                            viewModel.getUserStoreDetail(
                                storeId = viewModel.userStoreDetailModel.value?.store?.storeId ?: -1,
                                deviceLatitude = it.latitude,
                                deviceLongitude = it.longitude,
                                filterVisitStartDate = getMonthFirstDate(),
                            )
                            showToast(R.string.edit_store_success)
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
                launch {
                    addStoreViewModel.selectCategoryList.collect { categoryModelList ->
                        addCategoryRecyclerAdapter.submitList(listOf(AddCategoryModel(categoryModelList.size < 3)) + categoryModelList.map { it.menuType })
                        editCategoryMenuRecyclerAdapter.setItems(categoryModelList)
                    }
                }
                launch {
                    viewModel.userStoreDetailModel.collect {
                        it?.let {
                            val location = it.store.location
                            val selectCategoryModelList = it.store.categories.map { model ->
                                val menu = it.store.menus.filter { userStoreMenuModel ->
                                    userStoreMenuModel.category.categoryId == model.categoryId
                                }
                                SelectCategoryModel(menuType = model, menu)
                            }
                            addStoreViewModel.setSelectCategoryModelList(selectCategoryModelList)
                            if (addStoreViewModel.selectedLocation.value == null) {
                                addStoreViewModel.updateLocation(LatLng(location.latitude, location.longitude))
                            }
                            binding.storeNameEditTextView.setText(it.store.name)

                            when (it.store.salesType) {
                                SalesType.ROAD -> {
                                    binding.rbType1.isChecked = true
                                }

                                SalesType.STORE -> {
                                    binding.rbType2.isChecked = true
                                }

                                SalesType.CONVENIENCE_STORE -> {
                                    binding.rbType3.isChecked = true
                                }

                                else -> {}
                            }

                            it.store.paymentMethods.forEach { method ->
                                when (method) {
                                    PaymentType.CASH -> {
                                        binding.cbType1.isChecked = true
                                    }

                                    PaymentType.CARD -> {
                                        binding.cbType2.isChecked = true
                                    }

                                    PaymentType.ACCOUNT_TRANSFER -> {
                                        binding.cbType3.isChecked = true
                                    }
                                }
                            }
                            it.store.appearanceDays.forEach { day ->
                                when (day) {
                                    DayOfTheWeekType.MONDAY -> {
                                        binding.tbMon.isChecked = true
                                    }

                                    DayOfTheWeekType.TUESDAY -> {
                                        binding.tbTue.isChecked = true
                                    }

                                    DayOfTheWeekType.WEDNESDAY -> {
                                        binding.tbWen.isChecked = true
                                    }

                                    DayOfTheWeekType.THURSDAY -> {
                                        binding.tbThur.isChecked = true
                                    }

                                    DayOfTheWeekType.FRIDAY -> {
                                        binding.tbFri.isChecked = true
                                    }

                                    DayOfTheWeekType.SATURDAY -> {
                                        binding.tbSat.isChecked = true
                                    }

                                    DayOfTheWeekType.SUNDAY -> {
                                        binding.tbSun.isChecked = true
                                    }
                                }
                            }
                            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("a h시", Locale.getDefault())

                            if (it.store.openingHoursModel.startTime.isNotNullOrEmpty()) {
                                startTime = it.store.openingHoursModel.startTime
                                inputFormat.parse(startTime!!)?.let { startDate ->
                                    binding.openingHourStartTimeTextView.text = outputFormat.format(startDate)
                                }
                            }
                            if (it.store.openingHoursModel.endTime.isNotNullOrEmpty()) {
                                endTime = it.store.openingHoursModel.endTime
                                inputFormat.parse(endTime!!)?.let { endDate ->
                                    binding.openingHourEndTimeTextView.text = outputFormat.format(endDate)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initMap() {
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        childFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
        naverMapFragment.setIsShowOverlay(false)
    }

    private fun initAdapter() {
        binding.rvCategory.adapter = addCategoryRecyclerAdapter
        binding.rvCategory.itemAnimator = null
        binding.menuRecyclerView.adapter = editCategoryMenuRecyclerAdapter
        binding.menuRecyclerView.itemAnimator = null
    }

    private fun initButton() {
        binding.editAddressTextView.setOnClickListener {
            requireActivity().supportFragmentManager.replaceFragment(
                R.id.container,
                EditAddressFragment(),
                EditAddressFragment::class.java.name,
                false,
            )
        }
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnClearCategory.setOnClickListener {
            addCategoryRecyclerAdapter.submitList(listOf(AddCategoryModel()))
            addStoreViewModel.removeAllCategory()
        }
        binding.fullScreenButton.setOnClickListener {
            moveFullScreenMap()
        }
        binding.submitButton.setOnClickListener {
            if (binding.storeNameEditTextView.text.isNullOrBlank()) {
                showToast(R.string.store_name_empty)
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString("screen", "write_address_detail")
            }
            EventTracker.logEvent(Constants.CLICK_WRITE_STORE, bundle)
            addStoreViewModel.editStore(
                UserStoreModelRequest(
                    appearanceDays = getAppearanceDays(),
                    latitude = addStoreViewModel.selectedLocation.value?.latitude ?: NaverMapUtils.DEFAULT_LOCATION.latitude,
                    longitude = addStoreViewModel.selectedLocation.value?.longitude ?: NaverMapUtils.DEFAULT_LOCATION.longitude,
                    menuRequests = getMenuList().reversed(),
                    paymentMethods = getPaymentMethod(),
                    openingHours = OpeningHourRequest(
                        startTime = startTime,
                        endTime = endTime,
                    ),
                    storeName = binding.storeNameEditTextView.text.toString(),
                    storeType = getStoreType(),
                ),
                viewModel.userStoreDetailModel.value?.store?.storeId ?: 0,
            )
        }
        binding.openingHourStartTimeTextView.setOnClickListener {
            OpeningHourNumberPickerDialog.getInstance()
                .apply {
                    setDialogListener(object : OnClickDoneListener {
                        override fun onClickDoneButton(hour: Int?) {
                            if (hour == null) {
                                binding.openingHourStartTimeTextView.text = ""
                                startTime = null
                            } else {
                                binding.openingHourStartTimeTextView.text = if (hour < 13) "오전 ${hour}시" else "오후 ${hour}시"
                                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                startTime = dateFormat.format(dateFormat.parse("$hour:00") as Date)
                            }
                        }
                    })
                }.show(parentFragmentManager, OpeningHourNumberPickerDialog().tag)
        }
        binding.openingHourEndTimeTextView.setOnClickListener {
            OpeningHourNumberPickerDialog.getInstance()
                .apply {
                    setDialogListener(object : OnClickDoneListener {
                        override fun onClickDoneButton(hour: Int?) {
                            if (hour == null) {
                                binding.openingHourEndTimeTextView.text = ""
                                endTime = null
                            } else {
                                binding.openingHourEndTimeTextView.text = if (hour < 13) "오전 ${hour}시" else "오후 ${hour}시"
                                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                endTime = dateFormat.format(dateFormat.parse("$hour:00") as Date)
                            }
                        }
                    })
                }.show(parentFragmentManager, OpeningHourNumberPickerDialog().tag)
        }
    }

    private fun moveFullScreenMap() {
        val latLng = addStoreViewModel.selectedLocation.value
        val intent = FullScreenMapActivity.getIntent(
            context = requireContext(),
            latitude = latLng?.latitude,
            longitude = latLng?.longitude,
            name = binding.storeNameTextView.text.toString(),
        )
        startActivity(intent)
    }

    private fun getPaymentMethod(): List<PaymentType> {
        val result = arrayListOf<PaymentType>()
        if (binding.cbType1.isChecked) {
            result.add(PaymentType.CASH)
        }
        if (binding.cbType2.isChecked) {
            result.add(PaymentType.CARD)
        }
        if (binding.cbType3.isChecked) {
            result.add(PaymentType.ACCOUNT_TRANSFER)
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

    private fun getMenuList(): List<MenuModelRequest> {
        val menuList = arrayListOf<MenuModelRequest>()

        for (i in 0 until editCategoryMenuRecyclerAdapter.itemCount) {
            binding.menuRecyclerView.getChildAt(i)?.let {
                val view = it.findViewById<RecyclerView>(R.id.rv_menu_edit)
                val editMenuRecyclerView = (view as RecyclerView)
                val menuSize = (editMenuRecyclerView.adapter as? EditMenuRecyclerAdapter)?.itemCount ?: 0
                val category = if (editCategoryMenuRecyclerAdapter.items.isNotEmpty()) {
                    editCategoryMenuRecyclerAdapter.items[i].menuType.categoryId
                } else {
                    ""
                }

                var isEmptyCategory = true
                repeat(menuSize) { index ->
                    val menuRow = editMenuRecyclerView.getChildAt(index)
                    val name = (menuRow.findViewById(R.id.et_name) as EditText).text.toString()
                    val price = (menuRow.findViewById(R.id.et_price) as EditText).text.toString()

                    if (name.isNotEmpty() || price.isNotEmpty()) {
                        menuList.add(MenuModelRequest(category, name, price))
                        isEmptyCategory = false
                    }
                }

                if (isEmptyCategory) {
                    menuList.add(MenuModelRequest(category, "", ""))
                }
            }
        }

        return menuList
    }

    private fun getAppearanceDays(): List<DayOfTheWeekType> {
        val result = arrayListOf<DayOfTheWeekType>()
        val const = listOf(
            DayOfTheWeekType.MONDAY,
            DayOfTheWeekType.TUESDAY,
            DayOfTheWeekType.WEDNESDAY,
            DayOfTheWeekType.THURSDAY,
            DayOfTheWeekType.FRIDAY,
            DayOfTheWeekType.SATURDAY,
            DayOfTheWeekType.SUNDAY,
        )
        if (binding.tbMon.isChecked) {
            result.add(const[0])
        }
        if (binding.tbTue.isChecked) {
            result.add(const[1])
        }
        if (binding.tbWen.isChecked) {
            result.add(const[2])
        }
        if (binding.tbThur.isChecked) {
            result.add(const[3])
        }
        if (binding.tbFri.isChecked) {
            result.add(const[4])
        }
        if (binding.tbSat.isChecked) {
            result.add(const[5])
        }
        if (binding.tbSun.isChecked) {
            result.add(const[6])
        }
        return result
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEditDetailBinding =
        FragmentEditDetailBinding.inflate(inflater, container, false)
}
