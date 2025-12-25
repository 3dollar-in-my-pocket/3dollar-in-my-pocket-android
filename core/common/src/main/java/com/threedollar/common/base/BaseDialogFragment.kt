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
import com.google.firebase.analytics.logEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName

abstract class BaseDialogFragment<B : ViewBinding> :
    DialogFragment() {

    protected lateinit var binding: B

    protected abstract val screenName: ScreenName

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

    override fun onResume() {
        super.onResume()
        if (screenName != ScreenName.EMPTY) {
            sendScreenView(screenName)
        }
    }

    fun sendScreenView(screen: ScreenName, extraParameters: Map<ParameterName, Any> = emptyMap()) {
        LogManager.sendPageView(screen, this::class.java.simpleName, extraParameters)
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    @Deprecated(
        message = "No longer needed. Dialog.screenName is used automatically",
        replaceWith = ReplaceWith("")
    )
    open fun initFirebaseAnalytics() {
        // Override in legacy screens if needed
        // New screens should set Dialog.screenName instead
    }

    abstract fun initViews()
    companion object {
        const val DIALOG_HEIGHT_RATIO = "DIALOG_HEIGHT_RATIO"
        const val DIALOG_WIDTH_RATIO = "DIALOG_WIDTH_RATIO"
    }
}