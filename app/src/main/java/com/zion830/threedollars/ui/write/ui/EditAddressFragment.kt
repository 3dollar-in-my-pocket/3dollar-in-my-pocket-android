package com.zion830.threedollars.ui.write.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.dialog.NearExistDialog
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    override fun initView() {
        initMap()
        initFlow()
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.finishButton.setOnClickListener {
            viewModel.getStoreNearExists(viewModel.selectedLocation.value ?: LatLng(0.0, 0.0))
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent("EditAddressFragment")
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mapContainer, naverMapFragment)?.commit()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectedLocation.collect { latLng ->
                        if (latLng != null) {
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(R.string.location_no_address)
                            viewModel.requestStoreInfo(latLng)
                        }
                    }
                }
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
            }
        }
    }

    private fun showNearExistDialog() {
        NearExistDialog.getInstance(viewModel.selectedLocation.value?.latitude ?: 0.0, viewModel.selectedLocation.value?.longitude ?: 0.0)
            .apply {
                setDialogListener(object : NearExistDialog.DialogListener {
                    override fun accept() {
                        moveAddStoreDetailFragment()
                    }
                })
            }.show(parentFragmentManager, "")
    }

    private fun moveAddStoreDetailFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)
}