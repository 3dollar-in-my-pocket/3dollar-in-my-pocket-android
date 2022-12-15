package com.zion830.threedollars.ui.map

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
import com.zion830.threedollars.databinding.DialogMarkerClickBinding
import com.zion830.threedollars.databinding.DialogMarketingBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseDialogFragment
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty

@AndroidEntryPoint
class MarkerClickDialog : BaseDialogFragment<DialogMarkerClickBinding>(R.layout.dialog_marker_click) {

    private val viewModel: MarkerClickViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog)
    }

    override fun initViews() {
        binding.viewModel = viewModel
        dialog?.window?.setGravity(Gravity.BOTTOM)
        viewModel.getPopups()

        binding.downloadTextView.onSingleClick {
            if (viewModel.popupsResponse.value?.linkUrl.isNotNullOrEmpty()) {
                viewModel.eventClick("ADVERTISEMENT", viewModel.popupsResponse.value?.advertisementId.toString())
                context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.popupsResponse.value?.linkUrl)))
            }
        }
        binding.closeImageView.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "MarkerClickDialog"
    }
}