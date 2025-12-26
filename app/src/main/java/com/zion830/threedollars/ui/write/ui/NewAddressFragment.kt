package com.zion830.threedollars.ui.write.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.dialog.NearExistDialog
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class NewAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.navigation_home)
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
        initFlows()
        initButton()
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }

    private fun initButton() {
        binding.backButton.onSingleClick {
            findNavController().navigate(R.id.navigation_home)
        }
        binding.finishButton.onSingleClick {
            viewModel.selectedLocation.value?.let { location -> viewModel.getStoreNearExists(location) }
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mapContainer, naverMapFragment)?.commit()
        naverMapFragment.setIsShowOverlay(false)
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                            val bundle = Bundle().apply {
                                putString("screen", "write_address")
                            }
                            moveAddStoreDetailFragment()
                        }
                    }
                }
                launch {
                    viewModel.selectedLocation.collect { latLng ->
                        if (latLng != null) {
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(CommonR.string.location_no_address)
                            val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
                            val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
                            viewModel.requestStoreInfo(
                                latLng,
                                if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showNearExistDialog() {
        viewModel.selectedLocation.value?.let { location ->
            NearExistDialog.getInstance(location.latitude, location.longitude)
                .apply {
                    setDialogListener(object : NearExistDialog.DialogListener {
                        override fun accept() {
                            moveAddStoreDetailFragment()
                        }
                    })
                }.show(parentFragmentManager, NearExistDialog().tag)
        }
    }

    private fun moveAddStoreDetailFragment() {
        findNavController().navigateSafe(R.id.action_navigation_write_to_navigation_write_detail)
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)
}
