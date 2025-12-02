package com.zion830.threedollars.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.databinding.DialogOpeningHourNumberPickerBinding
import zion830.com.common.base.onSingleClick

interface OnClickDoneListener {
    fun onClickDoneButton(hour: Int?)
}

class OpeningHourNumberPickerDialog :
    BaseBottomSheetDialogFragment<DialogOpeningHourNumberPickerBinding>() {
    private var listener: OnClickDoneListener? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogOpeningHourNumberPickerBinding =
        DialogOpeningHourNumberPickerBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.doneTextView.onSingleClick {
            listener?.onClickDoneButton(binding.hourNumberPicker.value)
            dismiss()
        }
        binding.deleteTextView.onSingleClick {
            listener?.onClickDoneButton(null)
            dismiss()
        }

        binding.hourNumberPicker.minValue = 0
        binding.hourNumberPicker.maxValue = 23
        binding.hourNumberPicker.value = 0
    }

    fun setDialogListener(listener: OnClickDoneListener) {
        this.listener = listener
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "OpeningHourNumberPickerDialog", screenName = null)
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        fun getInstance() = OpeningHourNumberPickerDialog()
    }
}
