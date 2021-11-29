package com.zion830.threedollars.ui.addstore

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.report_store.map.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment

class NewAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>(R.layout.fragment_new_address) {

    override val viewModel: AddStoreViewModel by viewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    override fun initView() {
        initMap()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnFinish.setOnClickListener {
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.container,
                AddStoreDetailFragment(),
                AddStoreDetailFragment::class.java.name,
                false
            )
        }
    }

    private fun initMap() {
        val latitude = arguments?.getDouble(NewStoreActivity.KEY_LATITUDE) ?: -1.0
        val longitude = arguments?.getDouble(NewStoreActivity.KEY_LONGITUDE) ?: -1.0
        naverMapFragment = StoreAddNaverMapFragment.getInstance(LatLng(latitude, longitude)) {
            binding.tvAddress.text = getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }

        if (latitude > 0) {
            val latLng = LatLng(latitude, longitude)
            naverMapFragment.moveCamera(latLng)
            viewModel.updateLocation(latLng)
            binding.tvAddress.text = getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
        }

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    companion object {
        fun getInstance(latLng: LatLng?) = NewAddressFragment().apply {
            latLng?.let {
                val bundle = Bundle()
                bundle.putDouble(NewStoreActivity.KEY_LATITUDE, latLng.latitude)
                bundle.putDouble(NewStoreActivity.KEY_LONGITUDE, latLng.longitude)
                arguments = bundle
            }
        }
    }
}