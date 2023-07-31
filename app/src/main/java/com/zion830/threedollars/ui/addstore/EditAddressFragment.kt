package com.zion830.threedollars.ui.addstore

import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentEditAddressBinding
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.report_store.map.StoreEditNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseFragment

@AndroidEntryPoint
class EditAddressFragment :
    BaseFragment<FragmentEditAddressBinding, StoreDetailViewModel>(R.layout.fragment_edit_address) {

    private var isFirstOpen = true

    override val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreEditNaverMapFragment

    override fun initView() {
        initMap()
        binding.btnBack.setOnClickListener {
            viewModel.updateLocation(viewModel.storeLocation.value)
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnFinish.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        viewModel.storeInfo.observe(viewLifecycleOwner) {
            binding.tvAddress.text = getCurrentLocationName(LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0))
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.updateLocation(viewModel.storeLocation.value)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initMap() {
        naverMapFragment = StoreEditNaverMapFragment {
            binding.tvAddress.text =
                getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()

        if (isFirstOpen) {
            viewModel.updateLocation(viewModel.storeLocation.value) // 최초 맵 위치
            isFirstOpen = false
        }
    }
}