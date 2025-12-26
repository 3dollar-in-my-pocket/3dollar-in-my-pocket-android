package com.threedollar.common.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName

abstract class BaseComposeFragment<VM : BaseViewModel> : Fragment() {

    protected abstract val viewModel: VM

    protected abstract val screenName: ScreenName

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
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
}