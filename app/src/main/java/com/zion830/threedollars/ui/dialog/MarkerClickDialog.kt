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
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseDialogFragment
import com.threedollar.common.ext.isNotNullOrEmpty
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogMarkerClickBinding
import com.zion830.threedollars.ui.map.viewModel.MarkerClickViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.zion830.threedollars.core.designsystem.R as DesignSystemR


@AndroidEntryPoint
class MarkerClickDialog(val latLng: LatLng) : BaseDialogFragment<DialogMarkerClickBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogMarkerClickBinding =
        DialogMarkerClickBinding.inflate(inflater, container, false)

    override val screenName: ScreenName = ScreenName.MARKER_POPUP

    private val viewModel: MarkerClickViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), DesignSystemR.style.TransparentDialog)
    }

    override fun initViews() {
        dialog?.window?.setGravity(Gravity.BOTTOM)
        if (latLng.isValid) {
            viewModel.getPopups(latLng = latLng)
        }

        binding.downloadTextView.onSingleClick {
            viewModel.popupsResponse.value?.let { advertisementModel ->
                viewModel.sendClickBottomButton(advertisementModel.advertisementId.toString())
                if (advertisementModel.link.url.isNotNullOrEmpty()) {
                    if (advertisementModel.link.type == "APP_SCHEME") {
                        startActivity(
                            Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                                putExtra("link", advertisementModel.link.url)
                            },
                        )
                    } else {
                        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(advertisementModel.link.url)))
                    }
                }
            }
        }

        binding.closeImageView.onSingleClick {
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
                            .load(it?.image?.url)
                            .transform(GranularRoundedCorners(30f, 30f, 0f, 0f))
                            .into(binding.bannerImageView)
                        binding.titleTextView.text = it?.title?.content
                        binding.bodyTextView.text = it?.subTitle?.content
                        binding.downloadTextView.text = it?.extra?.content
                    }
                }
            }
        }
    }
}