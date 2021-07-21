package com.zion830.threedollars.ui.addstore

import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentEditAddressBinding
import com.zion830.threedollars.ui.report_store.map.StoreEditNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.getCurrentLocationName
import zion830.com.common.base.BaseFragment

class EditAddressFragment : BaseFragment<FragmentEditAddressBinding, StoreDetailViewModel>(R.layout.fragment_edit_address) {

    override val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreEditNaverMapFragment

    override fun initView() {
        initMap()
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnFinish.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        viewModel.storeInfo.observe(viewLifecycleOwner) {
            binding.tvAddress.text = getCurrentLocationName(LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0))
        }
    }

    private fun initMap() {
        naverMapFragment = StoreEditNaverMapFragment({
            binding.tvAddress.text = getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }, null)

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }
}