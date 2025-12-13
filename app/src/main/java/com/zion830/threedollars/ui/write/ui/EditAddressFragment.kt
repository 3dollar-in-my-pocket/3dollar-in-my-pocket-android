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
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.dialog.NearExistBottomSheetDialogFragment
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class EditAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    override fun initView() {
        initMap()
        initFlow()
        binding.backButton.onSingleClick {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.finishButton.onSingleClick {
            viewModel.processIntent(AddStoreContract.Intent.CheckNearStore(viewModel.state.value.selectedLocation ?: LatLng(0.0, 0.0)))
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "EditAddressFragment", screenName = "write_address")
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mapContainer, naverMapFragment)?.commit()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.state.collect { state ->
                        state.selectedLocation?.let { latLng ->
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(CommonR.string.location_no_address)
                        }
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is AddStoreContract.Effect.NearStoreExists -> {
                                if (effect.exists) {
                                    showNearExistDialog()
                                } else {
                                    moveAddStoreDetailFragment()
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showNearExistDialog() {
        NearExistBottomSheetDialogFragment.getInstance(
            viewModel.state.value.selectedLocation?.latitude ?: 0.0,
            viewModel.state.value.selectedLocation?.longitude ?: 0.0
        ).apply {
            setDialogListener(object : NearExistBottomSheetDialogFragment.DialogListener {
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