package com.zion830.threedollars.ui.favorite

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.viewModels
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.GlobalApplication.Companion.eventTracker
import com.zion830.threedollars.GlobalApplication.Companion.storeMarker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAllDeleteFavoriteBinding
import com.zion830.threedollars.databinding.DialogMarkerClickBinding
import com.zion830.threedollars.databinding.DialogMarketingBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseDialogFragment
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty

@AndroidEntryPoint
class AllDeleteFavoriteDialog : BaseDialogFragment<DialogAllDeleteFavoriteBinding>(R.layout.dialog_all_delete_favorite) {

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