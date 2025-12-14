package com.zion830.threedollars.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.zion830.threedollars.databinding.DialogForceUpdateBinding
import com.zion830.threedollars.datasource.model.AppUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class VersionUpdateDialog(private val updateDialog: AppUpdateDialog) : DialogFragment() {
    val defaultUrl = "https://play.google.com/store/apps/details?id=com.zion830.threedollars"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogForceUpdateBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        binding.btnOk.onSingleClick {
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(updateDialog.linkUrl ?: defaultUrl)
                )
            )
            requireActivity().finish()
        }
        binding.tvDescription.text = updateDialog.message ?: getString(CommonR.string.update_available_desc).format(updateDialog)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                requireActivity().finish()
                true
            }
            else true
        }
    }

    companion object {
        fun getInstance(appUpdateDia: AppUpdateDialog) = VersionUpdateDialog(
            appUpdateDia
        )
    }
}