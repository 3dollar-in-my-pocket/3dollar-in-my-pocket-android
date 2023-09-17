package com.zion830.threedollars.ui.addstore

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.addstore.view.NearExistDialog
import com.zion830.threedollars.ui.report_store.map.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import com.threedollar.common.ext.addNewFragment
import kotlin.properties.Delegates

@AndroidEntryPoint
class NewAddressFragment :
    LegacyBaseFragment<FragmentNewAddressBinding, AddStoreViewModel>(R.layout.fragment_new_address) {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    override fun initView() {
        latitude = arguments?.getDouble(NewStoreActivity.KEY_LATITUDE) ?: -1.0
        longitude = arguments?.getDouble(NewStoreActivity.KEY_LONGITUDE) ?: -1.0

        initMap()
        initViewModel()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnFinish.setOnClickListener {
            if (viewModel.isNearStoreExist.value == true) {
                showNearExistDialog()
            } else {
                moveAddStoreDetailFragment()
            }
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment.getInstance(LatLng(latitude, longitude))
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    private fun initViewModel() {
        viewModel.run {
            nearStoreInfo.observe(viewLifecycleOwner) { res ->
                naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, res ?: listOf())
            }
            selectedLocation.observe(viewLifecycleOwner) { latLng ->
                if (latLng != null) {
                    binding.tvAddress.text =
                        getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
                    requestStoreInfo(latLng)
                    latitude = latLng.latitude
                    longitude = latLng.longitude
                }
            }
        }
    }

    private fun showNearExistDialog() {
        NearExistDialog.getInstance(latitude, longitude)
            .apply {
                setDialogListener(object : NearExistDialog.DialogListener {
                    override fun accept() {
                        moveAddStoreDetailFragment()
                    }
                })
            }.show(parentFragmentManager, "")
    }

    private fun moveAddStoreDetailFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.container,
            AddStoreDetailFragment(),
            AddStoreDetailFragment::class.java.name,
            false
        )
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