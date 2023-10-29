package com.zion830.threedollars.ui.addstore.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogBottomDirectionBinding
import com.zion830.threedollars.databinding.DialogNearExistBinding
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NearExistDialog : BottomSheetDialogFragment() {

    interface DialogListener {
        fun accept()
    }

    private var listener: DialogListener? = null

    private lateinit var binding: DialogNearExistBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogNearExistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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