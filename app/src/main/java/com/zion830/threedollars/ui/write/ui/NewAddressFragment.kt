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
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.dialog.NearExistDialog
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "NewAddressFragment", screenName = "write_address")
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
        binding.finishButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "write_address")
            }
            EventTracker.logEvent(Constants.CLICK_CURRENT_LOCATION, bundle)
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
                            moveAddStoreDetailFragment()
                        }
                    }
                }
                launch {
                    viewModel.selectedLocation.collect { latLng ->
                        if (latLng != null) {
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
                            val bundle = Bundle().apply {
                                putString("screen", "write_address")
                                putString("address", binding.addressTextView.text.toString())
                            }
                            EventTracker.logEvent(Constants.CLICK_SET_ADDRESS, bundle)
                            viewModel.requestStoreInfo(latLng)
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