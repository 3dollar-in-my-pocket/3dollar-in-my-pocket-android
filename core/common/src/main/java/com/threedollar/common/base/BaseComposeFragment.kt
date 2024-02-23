package com.threedollar.common.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

abstract class BaseComposeFragment<VM : BaseViewModel> : Fragment() {


    protected abstract val viewModel: VM

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initFirebaseAnalytics()
    }

    fun setFirebaseAnalyticsLogEvent(className: String, screenName: String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }

    abstract fun initFirebaseAnalytics()
}