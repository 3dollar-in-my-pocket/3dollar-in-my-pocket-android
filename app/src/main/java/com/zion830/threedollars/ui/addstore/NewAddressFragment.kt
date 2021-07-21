package com.zion830.threedollars.ui.addstore

import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.report_store.map.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment

class NewAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>(R.layout.fragment_new_address) {

    override val viewModel: AddStoreViewModel by viewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        initMap()

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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
        naverMapFragment = StoreAddNaverMapFragment {
            binding.tvAddress.text = getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }
}