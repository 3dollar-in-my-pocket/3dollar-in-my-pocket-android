package com.zion830.threedollars.ui.map

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.threedollar.common.base.BaseDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogMarkerClickBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import com.threedollar.common.ext.isNotNullOrEmpty


@AndroidEntryPoint
class MarkerClickDialog : BaseDialogFragment<DialogMarkerClickBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogMarkerClickBinding =
        DialogMarkerClickBinding.inflate(inflater, container, false)

    private val viewModel: MarkerClickViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog)
    }

    override fun initViews() {
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

        viewModel.popupsResponse.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it.imageUrl)
                .transform(GranularRoundedCorners(30f, 30f, 0f, 0f))
                .into(binding.bannerImageView)
        }
    }

    companion object {
        const val TAG = "MarkerClickDialog"
    }
}