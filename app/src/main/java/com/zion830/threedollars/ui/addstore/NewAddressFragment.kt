package com.zion830.threedollars.ui.addstore

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.addstore.view.NearExistDialog
import com.zion830.threedollars.ui.report_store.map.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment

class NewAddressFragment :
    BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>(R.layout.fragment_new_address) {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private val latitude by lazy {
        arguments?.getDouble(NewStoreActivity.KEY_LATITUDE) ?: -1.0
    }
    private val longitude by lazy {
        arguments?.getDouble(NewStoreActivity.KEY_LONGITUDE) ?: -1.0
    }

    override fun initView() {
        initMap()
        initViewModels()
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnFinish.setOnClickListener {
            viewModel.getNearExists(latitude, longitude)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000)
            viewModel.requestStoreInfo(naverMapFragment.getMapCenterLatLng())
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment.getInstance(LatLng(latitude, longitude)) {
            binding.tvAddress.text =
                getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }

        if (latitude > 0) {
            val latLng = LatLng(latitude, longitude)
            naverMapFragment.moveCamera(latLng)
            viewModel.updateLocation(latLng)
            binding.tvAddress.text =
                getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    private fun initViewModels() {
        viewModel.run {
            isNearExist.observe(viewLifecycleOwner, {
                if (it) {
                    showNearExistDialog()
                } else {
                    moveAddStoreDetailFragment()
                }
            })
            nearStoreInfo.observe(viewLifecycleOwner) { res ->
                naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, res ?: listOf())
            }
            selectedLocation.observe(viewLifecycleOwner, { latLng ->
                if (latLng != null)
                    requestStoreInfo(latLng)
            })
        }
    }

    private fun showNearExistDialog() {
        val dialog = NearExistDialog.getInstance(latitude, longitude)
        dialog.setDialogListener(object : NearExistDialog.DialogListener {
            override fun accept() {
                moveAddStoreDetailFragment()
            }
        })
        dialog.show(parentFragmentManager, "")
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