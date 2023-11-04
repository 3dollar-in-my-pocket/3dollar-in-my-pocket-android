package com.zion830.threedollars.ui.addstore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.addstore.view.NearExistDialog
import com.zion830.threedollars.ui.report_store.map.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class NewAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    override fun initView() {
        latitude = arguments?.getDouble(NewStoreActivity.KEY_LATITUDE) ?: -1.0
        longitude = arguments?.getDouble(NewStoreActivity.KEY_LONGITUDE) ?: -1.0

        initMap()
        initFlows()
        initButton()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.finishButton.setOnClickListener {
            viewModel.getStoreNearExists(LatLng(latitude, longitude))
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment.getInstance(LatLng(latitude, longitude))
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mapContainer, naverMapFragment)?.commit()
        naverMapFragment.setIsShowOverlay(false)
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.aroundStoreModels.collect { res ->
                        naverMapFragment.addStoreMarkers(R.drawable.ic_mappin_focused_off, res ?: listOf())
                    }
                }
                launch {
                    viewModel.isNearStoreExist.collect {
                        if (it) {
                            showNearExistDialog()
                        } else {
                            moveAddStoreDetailFragment()
                        }
                    }
                }
                launch {
                    viewModel.selectedLocation.collect { latLng ->
                        if (latLng != null) {
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
                            viewModel.requestStoreInfo(latLng)
                            latitude = latLng.latitude
                            longitude = latLng.longitude
                        }
                    }
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

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)

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