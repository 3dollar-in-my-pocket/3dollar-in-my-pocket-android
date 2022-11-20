package com.zion830.threedollars.ui.addstore.view

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
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogNearExistBinding
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NearExistDialog : DialogFragment() {


    interface DialogListener {
        fun accept()
    }

    private var listener: DialogListener? = null

    private lateinit var binding: DialogNearExistBinding

    private var widthRatio = 0.88888889f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_near_exist, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dpMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(dpMetrics)

        } else {
            @Suppress("DEPRECATION")
            val display = activity?.windowManager?.defaultDisplay
            if (display != null) {
                @Suppress("DEPRECATION")
                display.getMetrics(dpMetrics)
            }
        }
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            setLayout(
                (dpMetrics.widthPixels * widthRatio).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val latitude = arguments?.getDouble(LATITUDE)
        val longitude = arguments?.getDouble(LONGITUDE)

        if (latitude != null && longitude != null) {
            binding.tvAddress.text = getCurrentLocationName(LatLng(latitude, longitude))
                ?: getString(R.string.location_no_address)
        }

        binding.ivClose.setOnClickListener { dismiss() }

        binding.btnFinish.setOnClickListener {
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
            longitude: Double
        ) = NearExistDialog().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, latitude)
                putDouble(LONGITUDE, longitude)
            }
        }
    }
}