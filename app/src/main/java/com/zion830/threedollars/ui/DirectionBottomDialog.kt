package com.zion830.threedollars.ui

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.databinding.DialogBottomDirectionBinding
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class DirectionBottomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomDirectionBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private var storeName = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomDirectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            latitude = getDouble(LATITUDE)
            longitude = getDouble(LONGITUDE)
            storeName = getString(STORE_NAME, "")
        }
        initButton()
    }

    private fun initButton() {
        binding.kakaoMapTextView.setOnClickListener {
            openKakaoMap()
            dismiss()
        }

        binding.naverMapTextView.setOnClickListener {
            openNaverMap()
            dismiss()
        }
        binding.closeTextView.setOnClickListener {
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