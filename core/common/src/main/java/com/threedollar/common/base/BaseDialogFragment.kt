package com.threedollar.common.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

abstract class BaseDialogFragment<B : ViewBinding> :
    DialogFragment() {

    protected lateinit var binding: B

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    private var heightRatio = -1f  // default
    private var widthRatio = 0.88888889f // default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dpMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dpMetrics)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        arguments?.getFloat(DIALOG_WIDTH_RATIO)?.let {
            if (it > 0) {
                widthRatio = it
            }
        }

        arguments?.getFloat(DIALOG_HEIGHT_RATIO)?.let {
            if (it > 0) {
                heightRatio = it
            }
        }

        if (heightRatio == -1f) {
            dialog?.window?.setLayout(
                (dpMetrics.widthPixels * widthRatio).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            dialog?.window?.setLayout(
                (dpMetrics.widthPixels * widthRatio).toInt(),
                (dpMetrics.heightPixels * heightRatio).toInt()
            )
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initFirebaseAnalytics()
        initViews()
    }
    fun setFirebaseAnalyticsLogEvent(className: String, screenName : String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun initFirebaseAnalytics()

    abstract fun initViews()
    companion object {
        const val DIALOG_HEIGHT_RATIO = "DIALOG_HEIGHT_RATIO"
        const val DIALOG_WIDTH_RATIO = "DIALOG_WIDTH_RATIO"
    }
}