package com.zion830.threedollars.ui.favorite

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.threedollar.common.base.BaseDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAllDeleteFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class AllDeleteFavoriteDialog : BaseDialogFragment<DialogAllDeleteFavoriteBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogAllDeleteFavoriteBinding =
        DialogAllDeleteFavoriteBinding.inflate(inflater, container, false)

    private var listener: DialogListener? = null

    interface DialogListener {
        fun click()
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog)
    }

    override fun initViews() {
        dialog?.window?.setGravity(Gravity.CENTER)
        binding.deleteTextView.onSingleClick {
            listener?.click()
            dismiss()
        }
        binding.cancelTextView.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "AllDeleteFavoriteDialog"
    }
}