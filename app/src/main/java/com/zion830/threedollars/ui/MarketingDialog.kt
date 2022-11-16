package com.zion830.threedollars.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogMarketingBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseDialogFragment

@AndroidEntryPoint
class MarketingDialog : BaseDialogFragment<DialogMarketingBinding>(R.layout.dialog_marketing) {

    private var check1 = false
    private var check2 = false
    private var check3 = false

    private lateinit var firebaseAnalytics : FirebaseAnalytics

    private var listener: DialogListener? = null

    interface DialogListener {
        fun accept()
    }

    fun setDialogListener(listener: DialogListener){
        this.listener = listener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog)
    }

    override fun initViews() {
        dialog?.window?.setGravity(Gravity.BOTTOM)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding.run {

            allAgreeTextView.setOnClickListener {
                if(check1 && check2 && check3){
                    check1 = false
                    check2 = false
                    check3 = false
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_uncheck,0,0,0)
                    term1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                    term2TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                    term3TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                }else{
                    check1 = true
                    check2 = true
                    check3 = true
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_check,0,0,0)
                    term1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                    term2TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                    term3TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                }
                agreeContinueTextView.isEnabled = check1
            }

            term1TextView.setOnClickListener {
                if(check1){
                    check1 = false
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_uncheck,0,0,0)
                    term1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                }else{
                    check1 = true
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_check,0,0,0)
                    term1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                }
                agreeContinueTextView.isEnabled = check1
            }
            term2TextView.setOnClickListener {
                if(check2){
                    check2 = false
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_uncheck,0,0,0)
                    term2TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                }else{
                    check2 = true
                    if(check1 && check3){
                        allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_check,0,0,0)
                    }
                    term2TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                }
            }
            term3TextView.setOnClickListener {
                if(check3){
                    check3 = false
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_uncheck,0,0,0)
                    term3TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_uncheck_gray,0,0,0)
                }else{
                    check3 = true
                    if(check1 && check2){
                    allAgreeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_circle_check,0,0,0)
                    }
                    term3TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_check_pink,0,0,0)
                }
            }
            term1ImageView.setOnClickListener {
                // TODO: 약관 웹으로 이동
            }
            term2ImageView.setOnClickListener {
                // TODO: 약관 웹으로 이동
            }
            term3ImageView.setOnClickListener {
                // TODO: 약관 웹으로 이동
            }
            agreeContinueTextView.setOnClickListener {
                firebaseAnalytics.setUserProperty("isAgreedMarketingAd", "true")
                listener?.accept()
                dismiss()
            }
        }
    }
    companion object{
        const val TAG = "MarketingDialog"
    }
}