package com.zion830.threedollars.ui.edit.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.utils.getCurrentLocationName
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAddressBottomSheetDialogFragment : BaseBottomSheetDialogFragment<FragmentNewAddressBinding>() {

    private val editStoreViewModel: EditStoreViewModel by viewModels({ requireParentFragment() })

    override val screenName: ScreenName = ScreenName.EMPTY

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isDraggable = false
    }

    override fun initView() {
        initFlow()
        setupButtons()
        binding.root.post {
            initMap()
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.mapContainer, naverMapFragment)
            .commitAllowingStateLoss()
    }

    private fun setupButtons() {
        binding.backButton.onSingleClick {
            editStoreViewModel.processIntent(EditStoreContract.Intent.CancelLocationEdit)
            dismiss()
        }
        binding.finishButton.onSingleClick {
            val currentAddress = binding.addressTextView.text?.toString() ?: ""
            editStoreViewModel.processIntent(EditStoreContract.Intent.UpdateAddress(currentAddress))
            editStoreViewModel.processIntent(EditStoreContract.Intent.ConfirmLocation)
            dismiss()
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    editStoreViewModel.state.collectLatest { state ->
                        state.tempLocation?.let { latLng ->
                            binding.addressTextView.text = getCurrentLocationName(latLng)
                                ?: getString(CommonR.string.location_no_address)
                        }
                    }
                }
            }
        }
    }

    /**
     * TH-919: predictive back 에서는 KEYCODE_BACK 키 리스너가 호출되지 않는다.
     * 뒤로가기는 두 모드 모두 다이얼로그 cancel 로 오므로 여기서 위치 수정을 취소한다.
     */
    override fun onCancel(dialog: DialogInterface) {
        editStoreViewModel.processIntent(EditStoreContract.Intent.CancelLocationEdit)
        super.onCancel(dialog)
    }

    companion object {
        const val TAG = "EditAddressBottomSheetDialogFragment"

        fun newInstance() = EditAddressBottomSheetDialogFragment()
    }
}
