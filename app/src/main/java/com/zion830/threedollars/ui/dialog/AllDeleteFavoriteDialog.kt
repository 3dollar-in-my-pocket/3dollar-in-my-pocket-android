package com.zion830.threedollars.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseDialogFragment
import com.zion830.threedollars.databinding.DialogAllDeleteFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class AllDeleteFavoriteDialog : BaseDialogFragment<DialogAllDeleteFavoriteBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogAllDeleteFavoriteBinding =
        DialogAllDeleteFavoriteBinding.inflate(inflater, container, false)

    override val screenName: ScreenName = ScreenName.EMPTY

    private var listener: DialogListener? = null

    interface DialogListener {
        fun click()
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), DesignSystemR.style.TransparentDialog)
    }

    override fun initViews() {
        dialog?.window?.setGravity(Gravity.CENTER)
        binding.deleteTextView.onSingleClick {
            listener?.click()
            dismiss()
        }
        binding.cancelTextView.onSingleClick {
            dismiss()
        }
    }

    companion object {
        const val TAG = "AllDeleteFavoriteDialog"
    }
}