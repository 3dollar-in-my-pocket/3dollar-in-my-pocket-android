package com.zion830.threedollars.ui.addstore.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogNearExistBinding
import com.zion830.threedollars.repository.model.v2.response.my.Review
import com.zion830.threedollars.ui.report_store.AddReviewDialog

class NearExistDialog : DialogFragment() {
    private lateinit var binding: DialogNearExistBinding

    private var heightRatio = -1f
    private var widthRatio = 0.88888889f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (heightRatio == -1f) {
            dialog?.window?.setLayout(
                (dpMetrics.widthPixels * widthRatio).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            dialog?.window?.setLayout(
                (dpMetrics.widthPixels * widthRatio).toInt(),
                (dpMetrics.heightPixels * heightRatio).toInt()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

    }

    companion object {
        fun getInstance() = NearExistDialog()
    }
}