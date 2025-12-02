package com.zion830.threedollars.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.databinding.DialogBottomDirectionBinding
import zion830.com.common.base.onSingleClick
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class DirectionBottomDialog : BaseBottomSheetDialogFragment<DialogBottomDirectionBinding>() {

    private var latitude = 0.0
    private var longitude = 0.0
    private var storeName = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogBottomDirectionBinding =
        DialogBottomDirectionBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "DirectionBottomDialog", screenName = null)
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    override fun initView() {
        arguments?.apply {
            latitude = getDouble(LATITUDE)
            longitude = getDouble(LONGITUDE)
            storeName = getString(STORE_NAME, "")
        }
        initButton()
    }

    private fun initButton() {
        binding.kakaoMapTextView.onSingleClick {
            openKakaoMap()
            dismiss()
        }

        binding.naverMapTextView.onSingleClick {
            openNaverMap()
            dismiss()
        }
        binding.closeTextView.onSingleClick {
            dismiss()
        }
    }

    private fun openKakaoMap() {
        try {
            val kakaoMapUri = "kakaomap://look?p=${latitude},${longitude}"
            val uri = Uri.parse(kakaoMapUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=net.daum.android.map")
            startActivity(intent)
        }
    }

    private fun openNaverMap() {
        try {
            val naverMapUri = "nmap://place?lat=${latitude}&lng=${longitude}&name=${urlEncode(storeName)}"

            val uri = Uri.parse(naverMapUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=com.nhn.android.nmap")
            startActivity(intent)
        }
    }

    private fun urlEncode(input: String): String {
        try {
            return URLEncoder.encode(input, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val STORE_NAME = "storeName"

        fun getInstance(
            latitude: Double?,
            longitude: Double?,
            storeName: String?
        ) = DirectionBottomDialog().apply {
            arguments = Bundle().apply {
                latitude?.let { putDouble(LATITUDE, it) }
                longitude?.let { putDouble(LONGITUDE, it) }
                storeName?.let { putString(STORE_NAME, it) }
            }
        }
    }
}