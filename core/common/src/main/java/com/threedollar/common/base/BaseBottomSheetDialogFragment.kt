package com.threedollar.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.threedollar.common.R
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName

abstract class BaseBottomSheetDialogFragment<B : ViewBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: B

    protected abstract val screenName: ScreenName

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initFirebaseAnalytics()
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (screenName != ScreenName.EMPTY) {
            sendScreenView(screenName)
        }
    }

    @Deprecated(
        message = "Use sendScreenView(ScreenName) instead",
        replaceWith = ReplaceWith("sendScreenView(screen)")
    )
    fun setFirebaseAnalyticsLogEvent(className: String, screenName : String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }

    fun sendScreenView(screen: ScreenName, extraParameters: Map<ParameterName, Any> = emptyMap()) {
        LogManager.sendPageView(screen, this::class.java.simpleName, extraParameters)
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun initView()

    @Deprecated(
        message = "No longer needed. Dialog.screenName is used automatically",
        replaceWith = ReplaceWith("")
    )
    open fun initFirebaseAnalytics() {
        // Override in legacy screens if needed
        // New screens should set Dialog.screenName instead
    }

    abstract fun setupRatio(bottomSheetDialog: BottomSheetDialog)

}