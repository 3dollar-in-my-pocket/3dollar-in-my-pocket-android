package com.zion830.threedollars.ui.write.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.AddCategoryModel
import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.PaymentType
import com.home.domain.request.MenuModelRequest
import com.home.domain.request.OpeningHourRequest
import com.home.domain.request.UserStoreModelRequest
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentAddStoreBinding
import com.zion830.threedollars.ui.dialog.AddStoreMenuCategoryDialogFragment
import com.zion830.threedollars.ui.dialog.OnClickDoneListener
import com.zion830.threedollars.ui.dialog.OpeningHourNumberPickerDialog
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.map.ui.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.write.adapter.AddCategoryRecyclerAdapter
import com.zion830.threedollars.ui.write.adapter.EditCategoryMenuRecyclerAdapter
import com.zion830.threedollars.ui.write.adapter.EditMenuRecyclerAdapter
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.navigateSafe
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddStoreDetailFragment : BaseFragment<FragmentAddStoreBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private val addCategoryRecyclerAdapter: AddCategoryRecyclerAdapter by lazy {
        AddCategoryRecyclerAdapter(
            {
                AddStoreMenuCategoryDialogFragment().show(
                    parentFragmentManager,
                    AddStoreMenuCategoryDialogFragment::class.java.name,
                )
            },
            {
                viewModel.removeCategory(it)
            },
        )
    }

    private val editCategoryMenuRecyclerAdapter: EditCategoryMenuRecyclerAdapter by lazy {
        EditCategoryMenuRecyclerAdapter { viewModel.removeCategory(it) }
    }

    private val naverMapFragment: StoreDetailNaverMapFragment = StoreDetailNaverMapFragment()

    private lateinit var callback: OnBackPressedCallback

    private var startTime: String? = null
    private var endTime: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        initNavigationBar()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initView() {
        initMap()
        initButton()
        initAdapter()
        initFlows()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "AddStoreDetailFragment", screenName = "write_address_detail")
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectedLocation.collect {
                        binding.addressTextView.text = getCurrentLocationName(it)
                        it?.let {
                            delay(500L)
                            val latLng = LatLng(it.latitude, it.longitude)
                            naverMapFragment.initMap(latLng, false)
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }

                launch {
                    viewModel.postUserStoreModel.collect {
                        it?.let {
                            findNavController().navigateSafe(R.id.action_navigation_write_detail_to_home)
                            showToast(R.string.add_store_success)
                        }
                    }
                }
                launch {
                    viewModel.selectCategoryList.collect {
                        addCategoryRecyclerAdapter.submitList(listOf(AddCategoryModel(it.size < 3)) + it.map { model -> model.menuType })
                        editCategoryMenuRecyclerAdapter.setItems(it)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvCategory.adapter = addCategoryRecyclerAdapter
        binding.rvCategory.itemAnimator = null
        binding.menuRecyclerView.adapter = editCategoryMenuRecyclerAdapter
        binding.menuRecyclerView.itemAnimator = null
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
        }
        binding.editAddressTextView.setOnClickListener {
            EventTracker.logEvent(Constants.EDIT_ADDRESS_BTN_CLICKED)
            findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
        }
        binding.btnClearCategory.setOnClickListener {
            addCategoryRecyclerAdapter.submitList(listOf(AddCategoryModel()))
            viewModel.removeAllCategory()
        }

        binding.submitButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "write_address_detail")
            }
            EventTracker.logEvent("click_write_store", bundle)
            saveStore()
        }
        binding.fullScreenButton.setOnClickListener {
            moveFullScreenMap()
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

    private fun initMap() {
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        parentFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
        naverMapFragment.setIsShowOverlay(false)
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
            UserStoreModelRequest(
                appearanceDays = getAppearanceDays(),
                latitude = viewModel.selectedLocation.value?.latitude ?: NaverMapUtils.DEFAULT_LOCATION.latitude,
                longitude = viewModel.selectedLocation.value?.longitude ?: NaverMapUtils.DEFAULT_LOCATION.longitude,
                menuRequests = getMenuList().reversed(),
                paymentMethods = getPaymentMethod(),
                openingHours = OpeningHourRequest(startTime = startTime, endTime = endTime),
                storeName = binding.storeNameEditTextView.text.toString(),
                storeType = getStoreType(),
            ),
        )
    }

    private fun moveFullScreenMap() {
        val latLng = viewModel.selectedLocation.value
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

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddStoreBinding =
        FragmentAddStoreBinding.inflate(inflater, container, false)
}
