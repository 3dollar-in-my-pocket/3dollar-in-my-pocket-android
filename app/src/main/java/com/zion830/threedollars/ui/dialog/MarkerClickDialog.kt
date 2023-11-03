package com.zion830.threedollars.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.threedollar.common.base.BaseDialogFragment
import com.threedollar.common.ext.isNotNullOrEmpty
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogMarkerClickBinding
import com.zion830.threedollars.ui.map.viewModel.MarkerClickViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick


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

        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.popupsResponse.collect {
                        Glide.with(requireContext())
                            .load(it?.imageUrl)
                            .transform(GranularRoundedCorners(30f, 30f, 0f, 0f))
                            .into(binding.bannerImageView)
                        binding.titleTextView.text = it?.title
                        binding.bodyTextView.text = it?.subTitle
                        binding.downloadTextView.text = it?.extraContent
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "MarkerClickDialog"
    }
}