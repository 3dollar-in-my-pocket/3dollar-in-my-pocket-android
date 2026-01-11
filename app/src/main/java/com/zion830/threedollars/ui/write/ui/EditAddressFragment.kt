package com.zion830.threedollars.ui.write.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class EditAddressFragment : BaseFragment<FragmentNewAddressBinding, EditStoreViewModel>() {

    override val viewModel: EditStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    override fun initView() {
        initMap()
        initFlow()
        binding.backButton.onSingleClick {
            viewModel.processIntent(EditStoreContract.Intent.CancelLocationEdit)
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.finishButton.onSingleClick {
            viewModel.processIntent(EditStoreContract.Intent.ConfirmLocation)
            requireActivity().supportFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.processIntent(EditStoreContract.Intent.CancelLocationEdit)
            requireActivity().supportFragmentManager.popBackStack()
        }
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
                        state.tempLocation?.let { latLng ->
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(CommonR.string.location_no_address)
                        }
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)
}