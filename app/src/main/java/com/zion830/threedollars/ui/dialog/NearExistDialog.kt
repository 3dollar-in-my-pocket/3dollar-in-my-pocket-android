package com.zion830.threedollars.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogNearExistBinding
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NearExistDialog : BaseBottomSheetDialogFragment<DialogNearExistBinding>() {

    interface DialogListener {
        fun accept()
    }

    private var listener: DialogListener? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogNearExistBinding =
        DialogNearExistBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "NearExistDialog", screenName = null)
    }


    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        initViews()
        initButton()
    }

    private fun initViews() {
        val latitude = arguments?.getDouble(LATITUDE)
        val longitude = arguments?.getDouble(LONGITUDE)

        if (latitude != null && longitude != null) {
            binding.addressTextView.text = getCurrentLocationName(LatLng(latitude, longitude))
                ?: getString(R.string.location_no_address)
        }

    }

    private fun initButton() {
        binding.closeImageButton.setOnClickListener { dismiss() }

        binding.finishButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "write_address_popup")
                putString("address", binding.addressTextView.text.toString())
            }
            EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
            listener?.accept()
            dismiss()
        }
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"

        fun getInstance(
            latitude: Double,
            longitude: Double,
        ) = NearExistDialog().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, latitude)
                putDouble(LONGITUDE, longitude)
            }
        }
    }
}